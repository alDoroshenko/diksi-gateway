package ru.neoflex.keycloak.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.exception.ManzanaGatewayException;
import ru.neoflex.keycloak.provider.ManzanaProvider;
import ru.neoflex.keycloak.util.Constants;
import ru.neoflex.keycloak.util.SessionUtil;
import ru.neoflex.keycloak.util.UserUtil;


@Slf4j
@RequiredArgsConstructor
public class UserOperationListener implements EventListenerProvider {
    private final KeycloakSession session;

    @Override
    public void onEvent(Event event) {
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        if (ResourceType.USER.equals(adminEvent.getResourceType())
                && OperationType.UPDATE.equals(adminEvent.getOperationType())) {
            log.info("user update event");
            updateUser(adminEvent);
        }
    }

    @Override
    public void close() {
    }

    private void updateUser(AdminEvent adminEvent) {
        UserModel user = getUserFromAdminEvent(adminEvent);
        log.info("User {} updated", UserUtil.maskString(user.getUsername()));
        RealmModel realm = session.realms().getRealm(adminEvent.getRealmId());
        AuthenticatorConfigModel config = SessionUtil.getAuthenticatorConfig(realm,
                Constants.KeycloakConfiguration.SMS_AUTHENTICATOR_ID,
                Constants.KeycloakConfiguration.CUSTOM_DIRECT_GRANT_FLOW);
        ComponentModel model = getComponentModel(realm, user);
        if (model == null) {
            log.error("Can't get ComponentModel");
            return;
        }
        ManzanaProvider manzanaProvider = new ManzanaProvider(config, user);
        try {
            manzanaProvider.execute();
        } catch (ManzanaGatewayException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private UserModel getUserFromAdminEvent(AdminEvent adminEvent) {
        String resourcePath = adminEvent.getResourcePath();
        String userId = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        return session.users().getUserById(session.getContext().getRealm(), userId);
    }

    private ComponentModel getComponentModel(RealmModel realm, UserModel user) {
        String federationLink = user.getFederationLink();
        if (federationLink != null) {
            return realm.getComponent(federationLink);
        }
        return null;
    }

}


