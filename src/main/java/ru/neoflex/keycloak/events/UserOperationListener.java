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
import ru.neoflex.keycloak.ManzanaConfiguration;
import ru.neoflex.keycloak.SmsConfiguration;
import ru.neoflex.keycloak.exceptions.ManzanaGatewayException;
import ru.neoflex.keycloak.exceptions.SmsGatewayException;
import ru.neoflex.keycloak.util.AuthProvider;
import ru.neoflex.keycloak.util.Constants;
import ru.neoflex.keycloak.util.ManzanaRegistrationProvider;
import ru.neoflex.keycloak.util.SessionUtil;


@Slf4j
@RequiredArgsConstructor
public class UserOperationListener implements EventListenerProvider {
    private final KeycloakSession session;

    @Override
    public void onEvent(Event event) {
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        if (ResourceType.USER.equals(adminEvent.getResourceType())) {
            if (OperationType.CREATE.equals(adminEvent.getOperationType())) {
                log.info("user creation event");
             //   createAdminUser(adminEvent);
            } else if (OperationType.UPDATE.equals(adminEvent.getOperationType())) {
                log.info("user update event");
                updateAdminUser(adminEvent);
            }
        }

    }

    @Override
    public void close() {

    }

    private void createAdminUser(AdminEvent adminEvent) {
        UserModel user = getUserFromAdminEvent(adminEvent);
        log.info("User {} created", user.getUsername());
        RealmModel realm = session.realms().getRealm(adminEvent.getRealmId());
        AuthenticatorConfigModel config = SessionUtil.getAuthenticatorConfig(realm,
                Constants.KeycloakConfiguration.SMS_AUTHENTICATOR_ID,
                Constants.KeycloakConfiguration.CUSTOM_DIRECT_GRANT_FLOW);
        ManzanaConfiguration manzanaConfig = new ManzanaConfiguration(config);
        SmsConfiguration smsConfig = new SmsConfiguration(config);
        try {
            AuthProvider.execute(smsConfig, manzanaConfig, user,null);
        } catch (SmsGatewayException e) {
            throw new RuntimeException("Not OK response from sms gateway");
        } catch (ManzanaGatewayException e) {
            throw new RuntimeException("Not OK response from manzana");
        }
    }

    private void updateAdminUser(AdminEvent adminEvent) {
        UserModel user = getUserFromAdminEvent(adminEvent);
        log.info("User {} updated", user.getUsername());
        RealmModel realm = session.realms().getRealm(adminEvent.getRealmId());
        AuthenticatorConfigModel config = SessionUtil.getAuthenticatorConfig(realm,
                Constants.KeycloakConfiguration.SMS_AUTHENTICATOR_ID,
                Constants.KeycloakConfiguration.CUSTOM_DIRECT_GRANT_FLOW);
        ManzanaConfiguration manzanaConfig = new ManzanaConfiguration(config);
        ManzanaRegistrationProvider.execute(manzanaConfig, user);
    }

    private UserModel getUserFromAdminEvent(AdminEvent adminEvent) {
        String resourcePath = adminEvent.getResourcePath();
        log.info("resourcePath {}", resourcePath);
        String userId = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        log.info("userId {}", userId);
        return session.users().getUserById(session.getContext().getRealm(), userId);
    }
}


