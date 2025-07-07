package ru.neoflex.keycloak.gateway.manzana;

import ru.neoflex.keycloak.exceptions.ManzanaGatewayException;
import ru.neoflex.keycloak.model.ManzanaUser;

import java.util.UUID;

public interface ManzanaService {

    ManzanaUser getUser( String mobilePhone) throws ManzanaGatewayException;
    ManzanaUser register( ManzanaUser user);
    String getSessionId( String mobilePhone) throws ManzanaGatewayException;


}
