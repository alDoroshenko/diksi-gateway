package ru.neoflex.keycloak.authenticator;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.util.AuthProvider;
import ru.neoflex.keycloak.util.Constants;


@Slf4j
@RequiredArgsConstructor
public class SmsAuthenticator implements Authenticator {


    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        KeycloakSession session = context.getSession();
        UserModel user = context.getUser();
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
          /*    String code = AuthProviderUtil.prepareOneTimePassword(config, user);
          //  String code = userAction.prepareOneTimePassword(config, user);
            sendSms(config, username, code);*/
            AuthProvider.execute(config, user);
            context.failureChallenge(AuthenticationFlowError.ACCESS_DENIED,
                    context.form().setError("smsAuthSmsCodeEmpty")
                            .createErrorPage(Response.Status.FOUND));
            return;
        }
        log.info("User: {} tried to login with code: {}", username, enteredCode);

        if (!validateCode(user, enteredCode)) {
            context.failureChallenge(AuthenticationFlowError.ACCESS_DENIED,
                    context.form().setError("smsAuthSmsCodeWrong")
                            .createErrorPage(Response.Status.BAD_REQUEST));
            return;
        }
        context.success();
    }


    private boolean validateCode(UserModel user, String enteredCode) {
        String expectedCode = user.getFirstAttribute(Constants.UserAttributes.SMS_CODE);
        Long expiryDate = Long.parseLong(user.getFirstAttribute(Constants.UserAttributes.EXPIRY_DATE));
        Long currentDate = System.currentTimeMillis();
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
}
