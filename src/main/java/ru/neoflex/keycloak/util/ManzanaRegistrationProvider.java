package ru.neoflex.keycloak.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.gateway.manzana.ManzanaService;
import ru.neoflex.keycloak.gateway.manzana.ManzanaServiceFactory;
import ru.neoflex.keycloak.model.ManzanaUser;

@UtilityClass
@Slf4j
public class ManzanaRegistrationProvider {
    public static void execute(AuthenticatorConfigModel config, UserModel user) {
        ManzanaUser manzanaUser = new ManzanaUser(user);
        registerManzanaUser(config, manzanaUser);
    }


    private ManzanaUser registerManzanaUser(AuthenticatorConfigModel config, ManzanaUser manzanaUser) {
        ManzanaService manzanaService = ManzanaServiceFactory.get(config.getConfig());
        //  UUID sessionId = manzanaService.identify();
        return manzanaService.register(manzanaUser);
    }
}
