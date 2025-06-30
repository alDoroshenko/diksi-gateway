package ru.neoflex.keycloak.gateway.manzana;

import lombok.extern.slf4j.Slf4j;
import ru.neoflex.keycloak.ManzanaConfiguration;
import ru.neoflex.keycloak.model.ManzanaUser;
import ru.neoflex.keycloak.util.Constants;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ManzanaServiceImpl implements ManzanaService {
    private final String uri;
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

    public ManzanaServiceImpl(ManzanaConfiguration config, HttpClient httpClient) {
        partnerId = config.getPartnerId();
        sessionId = config.getSessionId();
        virtualCardTypeId = config.getVirtualCardTypeId();
        uri = config.getUri();
        this.httpClient = httpClient;
    }
}
