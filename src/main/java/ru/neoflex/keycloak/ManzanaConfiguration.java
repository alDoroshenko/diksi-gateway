package ru.neoflex.keycloak;


import lombok.Getter;
import org.keycloak.models.AuthenticatorConfigModel;
import ru.neoflex.keycloak.exceptions.ConfigurationException;
import ru.neoflex.keycloak.util.Constants;

import java.util.Map;
import java.util.UUID;

@Getter
public class ManzanaConfiguration extends Configuration {
    private String uri;
    private UUID sessionId;
    private UUID partnerId;
    private UUID virtualCardTypeId;

    public ManzanaConfiguration(AuthenticatorConfigModel configModel) {
        Map<String, String> config = configModel.getConfig();
        simulationMode = Boolean.parseBoolean(config.getOrDefault(Constants.SmsAuthConstants.SIMULATION_MODE, "false"));
        uri = config.get(Constants.ManzanaConstants.MANZANA_URI);
        try {
            partnerId = UUID.fromString(config.get(Constants.ManzanaConstants.PARTNER_ID));
            sessionId = UUID.fromString(config.get(Constants.ManzanaConstants.SESSION_ID));
            virtualCardTypeId = UUID.fromString(config.get(Constants.ManzanaConstants.VIRTUAL_CARD_TYPE_ID));
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("Required parameters for Manzana Gateway service are missing " +
                    "or has wrong format (required UUID)");
        }
        validate();

    }

    private void validate() {
        if (uri.isBlank()) {
            throw new ConfigurationException("Required parameters for Manzana Gateway service are missing");
        }
    }

}


