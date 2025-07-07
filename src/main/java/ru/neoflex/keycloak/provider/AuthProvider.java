package ru.neoflex.keycloak.provider;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.ManzanaConfiguration;
import ru.neoflex.keycloak.SmsConfiguration;
import ru.neoflex.keycloak.exception.ManzanaGatewayException;
import ru.neoflex.keycloak.exception.SmsGatewayException;
import ru.neoflex.keycloak.gateway.manzana.ManzanaService;
import ru.neoflex.keycloak.gateway.manzana.ManzanaServiceFactory;
import ru.neoflex.keycloak.gateway.sms.SmsService;
import ru.neoflex.keycloak.gateway.sms.SmsServiceFactory;
import ru.neoflex.keycloak.model.ManzanaUser;
import ru.neoflex.keycloak.storage.UserRepository;
import ru.neoflex.keycloak.util.Constants;
import ru.neoflex.keycloak.util.UserUtil;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AuthProvider {
    private final UserModel user;
    private final UserRepository userRepository;
    private final SmsConfiguration smsConfig;
    private final SmsService smsService;
    private final ManzanaService manzanaService;

    public void execute() throws SmsGatewayException, ManzanaGatewayException {
        String manzanaId = user.getFirstAttribute(Constants.UserAttributes.MANZANA_ID);
        if (manzanaId == null) {
            ManzanaUser manzanaUser = searchManzanaUser(user.getUsername());
            if (manzanaUser != null) {
                String sessionId = getSessionId(user.getUsername());
                manzanaUser.setSessionId(sessionId);
                UserUtil.saveAttributes(getAttrsFromManzanaUser(manzanaUser), user);
                 userRepository.updateEntity(user);
            } else {
                log.info("User {} not found in Manzana: ", UserUtil.maskString(user.getUsername()));
            }
        } else {
            log.info("User with Manzana ID: {} exist id DB", manzanaId);
        }
        String code = prepareOneTimePassword(user);
        userRepository.updateOTP(user);
        sendSms(user.getUsername(), code);
    }

    private String generateCode(int length) {
        return SecretGenerator.getInstance().randomString(length, SecretGenerator.DIGITS);
    }

    private String prepareOneTimePassword(UserModel user) {
        int length = smsConfig.getCodeLenght();
        int ttl = smsConfig.getTtl();
        String code = generateCode(length);
        Map<String, String> attributes = new HashMap<>();
        attributes.put(Constants.UserAttributes.SMS_CODE, code);
        attributes.put(Constants.UserAttributes.EXPIRY_DATE,
                Long.toString(System.currentTimeMillis() + (ttl * 1000L)));
        UserUtil.saveAttributes(attributes, user);
        return code;
    }

    private void sendSms(String mobileNumber, String code) throws SmsGatewayException {
        String message = smsConfig.getMessage() + code;
        log.info("Generated message: {}", message);
        smsService.send(mobileNumber, message);
    }

    private ManzanaUser searchManzanaUser(String mobilePnone) throws ManzanaGatewayException {
        log.info("Searching manzana user with mobilePnone: {}", UserUtil.maskString(mobilePnone));
        return manzanaService.getUser(mobilePnone);
    }

    private String getSessionId(String mobilePnone) throws ManzanaGatewayException {
        String sessionId = manzanaService.getSessionId(mobilePnone);
        log.info("SessionId {} was got for user: {}", sessionId, UserUtil.maskString(mobilePnone));
        return sessionId;
    }

    private Map<String, String> getAttrsFromManzanaUser(ManzanaUser manzanaUser) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(Constants.UserAttributes.FIRST_NAME, manzanaUser.getFirstName());
        attributes.put(Constants.UserAttributes.LAST_NAME, manzanaUser.getLastName());
        attributes.put(Constants.UserAttributes.EMAIL, manzanaUser.getEmail());
        attributes.put(Constants.UserAttributes.BIRTHDAY, manzanaUser.getBirthDate());
        attributes.put(Constants.UserAttributes.MANZANA_ID, manzanaUser.getId());
        attributes.put(Constants.UserAttributes.SESSION_ID, manzanaUser.getSessionId());
        return attributes;
    }

    public AuthProvider(AuthenticatorConfigModel config, UserModel user, UserRepository userRepository) {
        this.user = user;
        this.userRepository = userRepository;
        this.smsConfig = new SmsConfiguration(config);
        this.smsService = SmsServiceFactory.get(smsConfig);
        this.manzanaService = ManzanaServiceFactory.get(new ManzanaConfiguration(config));
    }

}
