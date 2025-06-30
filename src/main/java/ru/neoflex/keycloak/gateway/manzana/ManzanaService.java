package ru.neoflex.keycloak.gateway.manzana;

import ru.neoflex.keycloak.model.ManzanaUser;

import java.util.UUID;

public interface ManzanaService {
    //UUID identify();
    ManzanaUser getUser( ManzanaUser user);
    ManzanaUser register( ManzanaUser user);


}
