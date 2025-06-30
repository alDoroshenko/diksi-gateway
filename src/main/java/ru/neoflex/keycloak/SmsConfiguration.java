package ru.neoflex.keycloak;

import lombok.Getter;
import org.keycloak.models.AuthenticatorConfigModel;
import ru.neoflex.keycloak.exceptions.ConfigurationException;
import ru.neoflex.keycloak.util.Constants;

import java.util.Map;

@Getter
public class SmsConfiguration extends Configuration {

    private int codeLenght;
    private int ttl;
    private String senderId;
    private String uri;
    private String login;
    private String password;
    private String message;



    public SmsConfiguration(AuthenticatorConfigModel configModel) {
        Map<String, String> config = configModel.getConfig();
        simulationMode = Boolean.parseBoolean(config.getOrDefault(Constants.SmsAuthConstants.SIMULATION_MODE, "false"));
        codeLenght = Integer.parseInt(config.get(Constants.SmsAuthConstants.CODE_LENGTH));
        ttl = Integer.parseInt(config.get(Constants.SmsAuthConstants.CODE_TTL));
        senderId = config.get(Constants.SmsAuthConstants.SENDER_ID);
        uri = config.get(Constants.SmsAuthConstants.SMS_URI);
        login = config.get(Constants.SmsAuthConstants.LOGIN);
        password = config.get(Constants.SmsAuthConstants.PASSWORD);
        message = config.get(Constants.SmsAuthConstants.TEXT);
        validate();
    }

    private void validate() {
        if ((codeLenght <= 0)
                || (ttl <= 0)
                || (senderId.isBlank())
                || (uri.isBlank())
                || (login.isBlank())
                || (password.isBlank())
                || (message.isBlank())) {
            throw new ConfigurationException("Required parameters for SMS Gateway service are missing");
        }
    }
}


