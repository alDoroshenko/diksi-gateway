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
import ru.neoflex.keycloak.exceptions.SmsGatewayException;
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
            String resourcePath = adminEvent.getResourcePath();
            String userId = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
            UserModel user = session.users().getUserById(session.getContext().getRealm(), userId);
            log.info("User {} created", user.getUsername());
            RealmModel realm = session.realms().getRealm(adminEvent.getRealmId());
            AuthenticatorConfigModel config = SessionUtil.getAuthenticatorConfig(realm,
                    Constants.KeycloakConfiguration.SMS_AUTHENTICATOR_ID,
                    Constants.KeycloakConfiguration.CUSTOM_DIRECT_GRANT_FLOW);
            try {
                AuthProvider.execute(config, user);
            } catch (SmsGatewayException e) {
                throw new RuntimeException("Not OK response from sms gateway");
            }
        }
    }

    @Override
    public void close() {

    }
}


