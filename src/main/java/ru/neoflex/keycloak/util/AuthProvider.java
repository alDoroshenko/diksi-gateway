package ru.neoflex.keycloak.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.ManzanaConfiguration;
import ru.neoflex.keycloak.SmsConfiguration;
import ru.neoflex.keycloak.exceptions.SmsGatewayException;
import ru.neoflex.keycloak.gateway.manzana.ManzanaService;
import ru.neoflex.keycloak.gateway.manzana.ManzanaServiceFactory;
import ru.neoflex.keycloak.gateway.sms.SmsService;
import ru.neoflex.keycloak.gateway.sms.SmsServiceFactory;
import ru.neoflex.keycloak.model.ManzanaUser;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
@Slf4j
public class AuthProvider {

    public static void execute(SmsConfiguration smsConfig, ManzanaConfiguration manzanaConfig, UserModel user) throws SmsGatewayException {
        String code = prepareOneTimePassword(smsConfig, user);
        sendSms(smsConfig, user.getUsername(), code);
        ManzanaUser manzanaUser = new ManzanaUser(user);
        manzanaUser = searchManzanaUser(manzanaConfig, manzanaUser);
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

    private String prepareOneTimePassword(SmsConfiguration config, UserModel user) {
        int length = config.getCodeLenght();
        int ttl = config.getTtl();
        String code = generateCode(length);
        Map<String, String> attributes = new HashMap<>();
        attributes.put(Constants.UserAttributes.SMS_CODE, code);
        attributes.put(Constants.UserAttributes.EXPIRY_DATE,
                Long.toString(System.currentTimeMillis() + (ttl * 1000L)));
        saveAttributes(attributes, user);
        return code;
    }

    private void sendSms(SmsConfiguration config, String mobileNumber, String code) throws SmsGatewayException {
        SmsService smsService = SmsServiceFactory.get(config);
        String message = config.getMessage() + code;
        log.info("Generated message: {}", message);
        smsService.send(mobileNumber, message);
    }


    private ManzanaUser searchManzanaUser(ManzanaConfiguration config, ManzanaUser manzanaUser) {
        ManzanaService manzanaService = ManzanaServiceFactory.get(config);
        // UUID sessionId = manzanaService.identify();
        return manzanaService.getUser(manzanaUser);
    }

    private Map<String, String> getAttributesFromManzana(ManzanaUser manzanaUser) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(Constants.UserAttributes.FIRST_NAME, manzanaUser.getFirstName());
        attributes.put(Constants.UserAttributes.LAST_NAME, manzanaUser.getLastName());
        attributes.put(Constants.UserAttributes.EMAIL, manzanaUser.getEmail());
        attributes.put(Constants.UserAttributes.BIRTHDAY, manzanaUser.getBirthDate());
        attributes.put(Constants.UserAttributes.REGION, manzanaUser.getRegion().toString());
        return attributes;

    }

}
