package ru.neoflex.keycloak.gateway.manzana;

import lombok.extern.slf4j.Slf4j;
import ru.neoflex.keycloak.model.ManzanaUser;
import ru.neoflex.keycloak.util.Constants;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ManzanaServiceFactory {
    private static final HttpClient httpClient = HttpClient
            .newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static ManzanaService get(Map<String, String> config) {
        if (Boolean.parseBoolean(config.getOrDefault(Constants.SmsAuthConstants.SIMULATION_MODE,
                "false"))) {
            return new ManzanaService() {

                @Override
                public ManzanaUser getUser(ManzanaUser user) {
                    user.setEmail(UUID.randomUUID().toString().substring(0, 7) + "TestMail@com");
                    user.setFirstName("Simulator");
                    user.setLastName("SimulatorLN");
                    user.setBirthDate(new Date().toString());
                    user.setRegion(UUID.randomUUID());
                    log.info("***** SIMULATION MODE *****,getUser method was called," +
                            "get user from manzana : {}", user);
                    return user;
                }

                @Override
                public ManzanaUser register(ManzanaUser user) {
                    log.info("***** SIMULATION MODE *****,register method was called," +
                            "register in manzana user: {}", user);
                    return user;
                }
            };
        } else return new ManzanaServiceImpl(config, httpClient);
    }
}
