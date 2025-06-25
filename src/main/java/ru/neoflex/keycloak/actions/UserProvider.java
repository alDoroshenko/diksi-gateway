package ru.neoflex.keycloak.actions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.util.Constants;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class UserProvider {
    private final AuthenticatorConfigModel config;
    private final UserModel user;

    public void saveAttributes(Map<String,String> attributes) {
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            user.setSingleAttribute(entry.getKey(), entry.getValue());
        }
    }

    public  String prepareOneTimePassword() {
        int length = Integer.parseInt(config.getConfig().get(Constants.SmsAuthConstants.CODE_LENGTH));
        int ttl = Integer.parseInt(config.getConfig().get(Constants.SmsAuthConstants.CODE_TTL));
        String code = SecretGenerator.getInstance().randomString(length, SecretGenerator.DIGITS);
        Map<String,String> attributes = new HashMap<>();
        attributes.put(Constants.UserAttributes.SMS_CODE, code);
        attributes.put(Constants.UserAttributes.EXPIRY_DATE,
                Long.toString(System.currentTimeMillis() + (ttl * 1000L)));
        saveAttributes(attributes);
        return code;
    }

    public void execute(){

    }

}
