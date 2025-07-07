package ru.neoflex.keycloak.gateway.manzana;

import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.exception.ManzanaGatewayException;
import ru.neoflex.keycloak.model.ManzanaUser;

public interface ManzanaService {

    ManzanaUser getUser( String mobilePhone) throws ManzanaGatewayException;
    String register( UserModel user) throws ManzanaGatewayException;
    String getSessionId( String mobilePhone) throws ManzanaGatewayException;


}
