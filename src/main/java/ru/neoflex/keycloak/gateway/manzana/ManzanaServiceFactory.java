package ru.neoflex.keycloak.gateway.manzana;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.ManzanaConfiguration;
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

    public static ManzanaService get(ManzanaConfiguration config) {

        if (config.isSimulationMode()) {
            return new ManzanaService() {

                @Override
                public ManzanaUser getUser(String phone) {
                    ManzanaUser user = new ManzanaUser();
                    user.setMobilePhone(phone);
                    user.setEmail(UUID.randomUUID().toString().substring(0, 7) + "TestMail@com");
                    user.setFirstName("Simulator");
                    user.setLastName("SimulatorLN");
                    user.setBirthDate(new Date().toString());
                    user.setId(UUID.randomUUID().toString());

                    if (Long.parseLong(phone)%2==0){
                        log.info("***** SIMULATION MODE *****,getUser method was called," +
                                "get user from manzana : {}", user);
                        return user;
                    } else {
                        log.info("***** SIMULATION MODE *****,getUser method was called," +
                                "user not found in manzana");
                        return null;
                    }
                }

                @Override
                public String register(UserModel user) {
                    log.info("***** SIMULATION MODE *****,register method was called," +
                            "register in manzana user: {}", user.getUsername());
                   return UUID.randomUUID().toString();
                }

                @Override
                public String getSessionId(String mobilePhone) {
                    log.info("***** SIMULATION MODE *****,getSessionId method was called,");
                    return UUID.randomUUID().toString();
                }
            };
        } else return new ManzanaServiceImpl(config, httpClient);
    }
}
