package ru.neoflex.keycloak.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.exceptions.SmsGatewayException;
import ru.neoflex.keycloak.gateway.SmsService;
import ru.neoflex.keycloak.gateway.SmsServiceFactory;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
@Slf4j
public class AuthProvider {

    public static void execute(AuthenticatorConfigModel config, UserModel user) throws SmsGatewayException {
        String code = prepareOneTimePassword(config, user);
        sendSms(config, user.getUsername(), code);
    }

    public static void saveAttributes(Map<String, String> attributes, UserModel user) {
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            user.setSingleAttribute(entry.getKey(), entry.getValue());
        }
    }

    private String generateCode(int length) {
        return SecretGenerator.getInstance().randomString(length, SecretGenerator.DIGITS);
    }

    private String prepareOneTimePassword(AuthenticatorConfigModel config, UserModel user) {
        int length = Integer.parseInt(config.getConfig().get(Constants.SmsAuthConstants.CODE_LENGTH));
        int ttl = Integer.parseInt(config.getConfig().get(Constants.SmsAuthConstants.CODE_TTL));
        String code = generateCode(length);
        Map<String, String> attributes = new HashMap<>();
        attributes.put(Constants.UserAttributes.SMS_CODE, code);
        attributes.put(Constants.UserAttributes.EXPIRY_DATE,
                Long.toString(System.currentTimeMillis() + (ttl * 1000L)));
        saveAttributes(attributes, user);
        return code;
    }

    private void sendSms(AuthenticatorConfigModel config, String mobileNumber, String code) throws SmsGatewayException {
        SmsService smsService = SmsServiceFactory.get(config.getConfig());
        String message = config.getConfig().get(Constants.SmsAuthConstants.TEXT) + code;
        log.info("Generated message: {}", message);
        smsService.send(mobileNumber, message);
    }


}
