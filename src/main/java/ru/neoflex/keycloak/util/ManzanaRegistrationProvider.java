package ru.neoflex.keycloak.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.ManzanaConfiguration;
import ru.neoflex.keycloak.gateway.manzana.ManzanaService;
import ru.neoflex.keycloak.gateway.manzana.ManzanaServiceFactory;
import ru.neoflex.keycloak.model.ManzanaUser;

@UtilityClass
@Slf4j
public class ManzanaRegistrationProvider {
    public static void execute(ManzanaConfiguration config, UserModel user) {
        ManzanaUser manzanaUser = new ManzanaUser();
        registerManzanaUser(config, manzanaUser);
    }


    private ManzanaUser registerManzanaUser(ManzanaConfiguration config, ManzanaUser manzanaUser) {
        ManzanaService manzanaService = ManzanaServiceFactory.get(config);
        //  UUID sessionId = manzanaService.identify();
        return manzanaService.register(manzanaUser);
    }
}
