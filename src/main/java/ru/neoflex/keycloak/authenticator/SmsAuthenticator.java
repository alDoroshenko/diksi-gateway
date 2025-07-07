package ru.neoflex.keycloak.authenticator;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.exception.ManzanaGatewayException;
import ru.neoflex.keycloak.exception.SmsGatewayException;
import ru.neoflex.keycloak.storage.UserRepository;
import ru.neoflex.keycloak.provider.AuthProvider;
import ru.neoflex.keycloak.util.Constants;
import ru.neoflex.keycloak.util.UserUtil;


@Slf4j
@RequiredArgsConstructor
public class SmsAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        UserModel user = context.getUser();
        ComponentModel model = getComponentModel(context, user);
        if (model == null) {
            log.error("Can't get ComponentModel");
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR,
                    context.form().setError("internalErrorDB")
                            .createErrorPage(Response.Status.INTERNAL_SERVER_ERROR));
            return;
        }
        UserRepository userRepository = new UserRepository(model);
        String username = context.getHttpRequest().getDecodedFormParameters().getFirst(
                Constants.RequestConstants.USERNAME);
        String enteredCode = context.getHttpRequest().getDecodedFormParameters().getFirst(
                Constants.RequestConstants.SMS_CODE);
        if (enteredCode == null) {
            context.failureChallenge(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,
                    context.form().setError("smsAuthSmsCodeAbsent")
                            .createErrorPage(Response.Status.BAD_REQUEST));
            return;
        } else if (enteredCode.isEmpty()) {
            try {
               AuthProvider authProvider = new AuthProvider(config,user,userRepository);
               authProvider.execute();
            } catch (SmsGatewayException e) {
                context.failureChallenge(AuthenticationFlowError.ACCESS_DENIED,
                        context.form().setError("smsAuthSmsBadResponse")
                                .createErrorPage(Response.Status.SEE_OTHER));
                return;
            } catch (ManzanaGatewayException e) {
                context.failureChallenge(AuthenticationFlowError.ACCESS_DENIED,
                        context.form().setError("manzanaBadResponse")
                                .createErrorPage(Response.Status.SEE_OTHER));
                return;
            }
            context.failureChallenge(AuthenticationFlowError.ACCESS_DENIED,
                    context.form().setError("smsAuthSmsCodeEmpty")
                            .createErrorPage(Response.Status.FOUND));
            return;
        }
        log.info("User: {} tried to login with code: {}", UserUtil.maskString(username), enteredCode);

        if (!validateCode(user, enteredCode)) {
            context.failureChallenge(AuthenticationFlowError.ACCESS_DENIED,
                    context.form().setError("smsAuthSmsCodeWrong")
                            .createErrorPage(Response.Status.BAD_REQUEST));
            return;
        }
        log.info("User: {} succsesfuly login", UserUtil.maskString(username));
        context.success();
    }


    private boolean validateCode(UserModel user, String enteredCode) {
        String expectedCode = user.getFirstAttribute(Constants.UserAttributes.SMS_CODE);
        long expiryDate;
        try {
            expiryDate = Long.parseLong(user.getFirstAttribute(Constants.UserAttributes.EXPIRY_DATE));
        } catch (NumberFormatException e) {
            log.error("Invalid expiry date: {}", e.getMessage());
            return false;
        }
        if (expectedCode.isBlank() || expiryDate <= 0) {
            log.error("Invalid expiry date or expectedCode");
            return false;
        }
        long currentDate = System.currentTimeMillis();
        log.info("Expiry date: {};  current date: {}", expiryDate, currentDate);

        if (!expectedCode.equals(enteredCode)) {
            log.info("Code not valid");
            return false;
        }
        if (currentDate > expiryDate) {
            log.info("Code is expired");
            return false;
        }
        return true;

    }


    @Override
    public void action(AuthenticationFlowContext context) {
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
    }

    @Override
    public void close() {
    }

    private ComponentModel getComponentModel(AuthenticationFlowContext context, UserModel user) {
        String federationLink = user.getFederationLink();
        if (federationLink != null) {
            RealmModel realm = context.getRealm();
            return realm.getComponent(federationLink);
        }
        return null;
    }
}
