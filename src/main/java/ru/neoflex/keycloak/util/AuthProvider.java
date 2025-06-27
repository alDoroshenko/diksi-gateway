package ru.neoflex.keycloak.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.exceptions.SmsGatewayException;
import ru.neoflex.keycloak.gateway.manzana.ManzanaService;
import ru.neoflex.keycloak.gateway.manzana.ManzanaServiceFactory;
import ru.neoflex.keycloak.gateway.sms.SmsService;
import ru.neoflex.keycloak.gateway.sms.SmsServiceFactory;
import ru.neoflex.keycloak.model.ManzanaUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
@Slf4j
public class AuthProvider {

    public static void execute(AuthenticatorConfigModel config, UserModel user) throws SmsGatewayException {
        String code = prepareOneTimePassword(config, user);
        sendSms(config, user.getUsername(), code);
        ManzanaUser manzanaUser = new ManzanaUser(user);
        manzanaUser = searchManzanaUser(config, manzanaUser);
        if (manzanaUser != null) {
            saveAttributes(getAttributesFromManzana(manzanaUser), user);
        }
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
        int length = Integer.parseInt(config.getConfig().getOrDefault(Constants.SmsAuthConstants.CODE_LENGTH, "4"));
        int ttl = Integer.parseInt(config.getConfig().getOrDefault(Constants.SmsAuthConstants.CODE_TTL, "300"));
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

    private ManzanaUser registerManzanaUser(AuthenticatorConfigModel config, ManzanaUser manzanaUser) {
        ManzanaService manzanaService = ManzanaServiceFactory.get(config.getConfig());
        UUID sessionId = manzanaService.identify();
        return manzanaService.register(sessionId, manzanaUser);
    }
    private ManzanaUser searchManzanaUser(AuthenticatorConfigModel config, ManzanaUser manzanaUser) {
        ManzanaService manzanaService = ManzanaServiceFactory.get(config.getConfig());
        UUID sessionId = manzanaService.identify();
        return manzanaService.getUser(sessionId, manzanaUser);
    }

    private Map<String, String> getAttributesFromManzana (ManzanaUser manzanaUser){
        Map<String, String> attributes = new HashMap<>();
        attributes.put(Constants.UserAttributes.FIRST_NAME, manzanaUser.getFirstName());
        attributes.put(Constants.UserAttributes.LAST_NAME, manzanaUser.getLastName());
        return attributes;

    }

}
