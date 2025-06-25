package ru.neoflex.keycloak.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.util.AuthProvider;
import ru.neoflex.keycloak.util.Constants;
import ru.neoflex.keycloak.util.SessionUtil;


@Slf4j
@RequiredArgsConstructor
public class CreateUserListener implements EventListenerProvider {
    private final KeycloakSession session;

    @Override
    public void onEvent(Event event) {

    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        if (OperationType.CREATE.equals(adminEvent.getOperationType())
                && ResourceType.USER.equals(adminEvent.getResourceType())) {
            log.info("User created");
            String resourcePath = adminEvent.getResourcePath();
            String userId = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
            UserModel user = session.users().getUserById(session.getContext().getRealm(), userId);
            RealmModel realm = session.realms().getRealm(adminEvent.getRealmId());
            AuthenticatorConfigModel config = SessionUtil.getAuthenticatorConfig(realm,
                    Constants.KeycloakConfiguration.SMS_AUTHENTICATOR_ID,
                    Constants.KeycloakConfiguration.CUSTOM_DIRECT_GRANT_FLOW);
            AuthProvider.execute(config, user);
            // sendSms(config, user.getUsername(), code);
            //  сходи в манзана
        }
    }

    @Override
    public void close() {

    }
}


