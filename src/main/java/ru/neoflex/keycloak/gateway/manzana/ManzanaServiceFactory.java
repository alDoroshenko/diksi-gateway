package ru.neoflex.keycloak.gateway.manzana;

import lombok.extern.slf4j.Slf4j;
import ru.neoflex.keycloak.model.ManzanaUser;
import ru.neoflex.keycloak.util.Constants;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ManzanaServiceFactory {
    public static ManzanaService get(Map<String, String> config) {
        if (Boolean.parseBoolean(config.getOrDefault(Constants.SmsAuthConstants.SIMULATION_MODE,
                "false"))) {
            return new ManzanaService() {
                @Override
                public UUID identify() {
                    UUID uuid = UUID.randomUUID();
                    log.info("***** SIMULATION MODE *****,identify method was called," +
                            " session Id:{} generated", uuid);
                    return uuid;
                }

                @Override
                public ManzanaUser getUser(UUID sessionId, ManzanaUser user) {
                    log.info("***** SIMULATION MODE *****,getUser method was called," +
                            "get user from manzana : {}", user);
                    return user;
                }

                @Override
                public ManzanaUser register(UUID sessionId, ManzanaUser user) {
                    log.info("***** SIMULATION MODE *****,register method was called," +
                            "register in manzana user: {}", user);
                    return user;
                }
            };
        }
        else return new ManzanaServiceImpl(config,
                HttpClient.newHttpClient());
    }
}
