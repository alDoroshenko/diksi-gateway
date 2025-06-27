package ru.neoflex.keycloak.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.AuthenticatorConfigModel;

import org.keycloak.models.RealmModel;

@UtilityClass
@Slf4j
public class SessionUtil {
    public static AuthenticatorConfigModel getAuthenticatorConfig(RealmModel realm, String authenticatorId, String flowAlias) {

        AuthenticationFlowModel flow = realm.getFlowByAlias(flowAlias);
        if (flow == null) {
            log.info("Flow {} not found", flowAlias);
            return null;
        }
        return realm.getAuthenticationExecutionsStream(flow.getId())
                .filter(execution -> {
                    String providerId = execution.getAuthenticator();
                    log.info("providerId: " + providerId);
                    return authenticatorId.equalsIgnoreCase(providerId);
                })
                .findFirst()
                .map(execution -> {
                    String configId = execution.getAuthenticatorConfig();
                    if (configId == null) {
                        log.info("No config ID found for {}", authenticatorId);
                        return null;
                    }
                    return realm.getAuthenticatorConfigById(configId);

                })
                .orElseGet(() -> {
                    log.info("{} not found in flow", authenticatorId);
                    return null;
                });
    }

}
