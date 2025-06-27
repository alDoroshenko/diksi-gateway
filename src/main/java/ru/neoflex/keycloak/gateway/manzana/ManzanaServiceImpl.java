package ru.neoflex.keycloak.gateway.manzana;

import lombok.extern.slf4j.Slf4j;
import ru.neoflex.keycloak.model.ManzanaUser;
import ru.neoflex.keycloak.util.Constants;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ManzanaServiceImpl implements ManzanaService {
    private final UUID partnerId;
    private final HttpClient httpClient;


    @Override
    public UUID identify() {
        return UUID.randomUUID();
    }

    @Override
    public ManzanaUser getUser(UUID sessionId, ManzanaUser user) {
        return null;
    }

    @Override
    public ManzanaUser register(UUID sessionId, ManzanaUser user) {
        return null;
    }

    public ManzanaServiceImpl(Map<String, String> config, HttpClient httpClient) {
        partnerId = UUID.fromString(config.getOrDefault(Constants.SmsAuthConstants.SENDER_ID,
                UUID.randomUUID().toString()));
        this.httpClient = httpClient;
    }
}
