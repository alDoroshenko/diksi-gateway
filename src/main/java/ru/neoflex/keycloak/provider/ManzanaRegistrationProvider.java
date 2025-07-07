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
public class ManzanaRegistrationProvider {
    private final ManzanaService manzanaService;
    private final UserModel user;
    private final UserRepository userRepository;

    public void execute() throws ManzanaGatewayException {
        String manzanaId = registerManzanaUser();
        Map<String, String> attributes = new HashMap<>();
        attributes.put(Constants.UserAttributes.MANZANA_ID, manzanaId);
        UserUtil.saveAttributes(attributes, user);
        userRepository.updateEntity(user);
    }


    private String registerManzanaUser() throws ManzanaGatewayException {
        String manzanaId = manzanaService.register(user);
        log.info("ManzanaId {} was got for user: {}", manzanaId, UserUtil.maskString(user.getUsername()));
        return manzanaId;
    }

    public ManzanaRegistrationProvider(AuthenticatorConfigModel config, UserModel user, UserRepository userRepository) {
        this.user = user;
        this.userRepository = userRepository;
        this.manzanaService = ManzanaServiceFactory.get(new ManzanaConfiguration(config));
    }
}
