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
    private final UUID sessionId;
    private final UUID virtualCardTypeId;
    private final HttpClient httpClient;



    @Override
    public ManzanaUser getUser( ManzanaUser user) {
        return null;
    }

    @Override
    public ManzanaUser register( ManzanaUser user) {
        return null;
    }

    public ManzanaServiceImpl(Map<String, String> config, HttpClient httpClient) {
        partnerId = UUID.fromString(config.getOrDefault(Constants.ManzanaConstants.PARTNER_ID,
                UUID.randomUUID().toString()));
        sessionId = UUID.fromString(config.getOrDefault(Constants.ManzanaConstants.SESSION_ID,
                UUID.randomUUID().toString()));
        virtualCardTypeId = UUID.fromString(config.getOrDefault(Constants.ManzanaConstants.VIRTUAL_CARD_TYPE_ID,
                UUID.randomUUID().toString()));
        this.httpClient = httpClient;
    }
}
