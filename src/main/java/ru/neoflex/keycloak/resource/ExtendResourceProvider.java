package ru.neoflex.keycloak.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager.AuthResult;
import org.keycloak.services.resource.RealmResourceProvider;
import ru.neoflex.keycloak.dto.authservice.AuthServiceRequestDTO;
import ru.neoflex.keycloak.dto.authservice.AuthServiceResponseDTO;
import ru.neoflex.keycloak.dto.authservice.ErrorResponse;
import ru.neoflex.keycloak.exception.ManzanaGatewayException;
import ru.neoflex.keycloak.provider.ManzanaProvider;
import ru.neoflex.keycloak.util.Constants;
import ru.neoflex.keycloak.util.SessionUtil;
import ru.neoflex.keycloak.util.UserUtil;

@Slf4j
@RequiredArgsConstructor
public class ExtendResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {
    }

    @POST
    @Path("manzana-register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(AuthServiceRequestDTO request) {
        log.info("Registering user {}", UserUtil.maskString(request.getUsername()));
        checkAuth();
        try {
            return Response.ok(processRequest(request)).build();
        } catch (ManzanaGatewayException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    private AuthServiceResponseDTO processRequest(AuthServiceRequestDTO request) throws ManzanaGatewayException {
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserByUsername(realm, request.getUsername());
        AuthenticatorConfigModel config = SessionUtil.getAuthenticatorConfig(realm,
                Constants.KeycloakConfiguration.SMS_AUTHENTICATOR_ID,
                Constants.KeycloakConfiguration.CUSTOM_DIRECT_GRANT_FLOW);
        ManzanaProvider manzanaProvider = new ManzanaProvider(config, user);


        manzanaProvider.execute();
        AuthServiceResponseDTO response = AuthServiceResponseDTO.builder()
                .username(user.getUsername())
                .manzanaId(user.getFirstAttribute(Constants.UserAttributes.MANZANA_ID))
                .sessionId(user.getFirstAttribute(Constants.UserAttributes.SESSION_ID))
                .pushEnabled(Boolean.parseBoolean(user.getFirstAttribute(Constants.UserAttributes.PUSH_ENABLE)))
                .build();
        return response;
    }



private AuthResult checkAuth() {
    AuthResult auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
    if (auth == null) {
        throw new NotAuthorizedException("Bearer");
    }
    return auth;
}


}
