package ru.neoflex.keycloak.provider;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.ManzanaConfiguration;
import ru.neoflex.keycloak.exception.ManzanaGatewayException;
import ru.neoflex.keycloak.gateway.manzana.ManzanaService;
import ru.neoflex.keycloak.gateway.manzana.ManzanaServiceFactory;
import ru.neoflex.keycloak.storage.UserRepository;
import ru.neoflex.keycloak.util.Constants;
import ru.neoflex.keycloak.util.UserUtil;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class ManzanaProvider {
    private final ManzanaService manzanaService;
    private final UserModel user;
    private final UserRepository userRepository;

    public void execute() throws ManzanaGatewayException {
        if (user.getFirstAttribute(Constants.UserAttributes.MANZANA_ID) == null) {
            String manzanaId = registerManzanaUser();
            String sessionId = getSessionId(user.getUsername());
            Map<String, String> attributes = new HashMap<>();
            attributes.put(Constants.UserAttributes.MANZANA_ID, manzanaId);
            attributes.put(Constants.UserAttributes.SESSION_ID, sessionId);
            UserUtil.saveAttributes(attributes, user);
        }
        else {
            log.info("user already exists in manzana");
        }
        userRepository.updateEntity(user);
    }


    private String registerManzanaUser() throws ManzanaGatewayException {
        String manzanaId = manzanaService.register(user);
        log.info("ManzanaId {} was got for user: {}", manzanaId, UserUtil.maskString(user.getUsername()));
        return manzanaId;
    }

    private String getSessionId(String mobilePnone) throws ManzanaGatewayException {
        String sessionId = manzanaService.getSessionId(mobilePnone);
        log.info("SessionId {} was got for user: {}", sessionId, UserUtil.maskString(mobilePnone));
        return sessionId;
    }

    public ManzanaProvider(AuthenticatorConfigModel config, UserModel user, UserRepository userRepository) {
        this.user = user;
        this.userRepository = userRepository;
        this.manzanaService = ManzanaServiceFactory.get(new ManzanaConfiguration(config));
    }

}
