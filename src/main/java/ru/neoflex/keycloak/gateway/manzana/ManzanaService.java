package ru.neoflex.keycloak.gateway.manzana;

import ru.neoflex.keycloak.exceptions.ManzanaGatewayException;
import ru.neoflex.keycloak.model.ManzanaUser;

import java.util.UUID;

public interface ManzanaService {
    //UUID identify();
    ManzanaUser getUser( String phone) throws ManzanaGatewayException;
    ManzanaUser register( ManzanaUser user);


}
