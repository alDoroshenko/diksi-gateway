package ru.neoflex.keycloak.gateway.manzana;

import ru.neoflex.keycloak.model.ManzanaUser;

import java.util.UUID;

public interface ManzanaService {
    UUID identify();
    ManzanaUser getUser(UUID sessionId, ManzanaUser user);
    ManzanaUser register(UUID sessionId, ManzanaUser user);


}
