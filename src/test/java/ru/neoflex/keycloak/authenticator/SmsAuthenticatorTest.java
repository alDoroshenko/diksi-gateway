/*
package ru.neoflex.keycloak.authenticator;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;


import org.keycloak.forms.login.LoginFormsProvider;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ru.neoflex.keycloak.exceptions.SmsGatewayException;
import ru.neoflex.keycloak.mock.KeycloakMockDto;
import ru.neoflex.keycloak.provider.AuthProvider;
import ru.neoflex.keycloak.util.Constants;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class SmsAuthenticatorTest {
    private final SmsAuthenticator authenticator = new SmsAuthenticator();

    private static final String USERNAME = "89162005555";
    private static final String EXPECTED_CODE = "123456";


    @Test
    void authenticateShouldFailWhenSmsCodeIsNull() {
        final KeycloakMockDto mock = new KeycloakMockDto();
        mock.addHttpRequestParam(Constants.RequestConstants.USERNAME, USERNAME);
        authenticator.authenticate(mock.getAuthenticationFlowContext());
        verify(mock.getAuthenticationFlowContext()).failureChallenge(
                eq(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR),
                any());
        verify(mock.getAuthenticationFlowContext().form()).setError("smsAuthSmsCodeAbsent");
    }

    @Test
    void authenticateShouldFailAndSendSmsWnenCodeIsNull() {
        final KeycloakMockDto mock = new KeycloakMockDto();
        mock.addHttpRequestParam(Constants.RequestConstants.USERNAME, USERNAME);
        mock.addHttpRequestParam(Constants.RequestConstants.SMS_CODE, "");

        try (MockedStatic<AuthProvider> mockedAuthProvider =
                     mockStatic(AuthProvider.class)) {
            authenticator.authenticate(mock.getAuthenticationFlowContext());
            verify(mock.getAuthenticationFlowContext()).failureChallenge(
                    eq(AuthenticationFlowError.ACCESS_DENIED),
                    any());
            verify(mock.getAuthenticationFlowContext().form())
                    .setError("smsAuthSmsCodeEmpty");

            mockedAuthProvider.verify(() ->
                    AuthProvider.execute(any(), any()));
        }
    }
    @Test
    void authenticateShouldFailBecauseSmsGatewayNotAvailable() {
        final KeycloakMockDto mock = new KeycloakMockDto();
        AuthenticationFlowContext context = mock.getAuthenticationFlowContext();
        mock.addHttpRequestParam(Constants.RequestConstants.USERNAME, USERNAME);
        mock.addHttpRequestParam(Constants.RequestConstants.SMS_CODE, "");

        try (MockedStatic<AuthProvider> mockedAuthProvider =
                     mockStatic(AuthProvider.class)) {
            mockedAuthProvider.when(() -> AuthProvider.execute(any(), any()))
                    .thenThrow(new SmsGatewayException("Bad response from sms gateway"));
            authenticator.authenticate(context);
            verify(context).failureChallenge(
                    eq(AuthenticationFlowError.ACCESS_DENIED),
                    any());
            verify(context.form())
                    .setError("smsAuthSmsBadResponse");

        }
    }

    @Test
    void authenticateShouldFailWhenCodeIsInvalid() {
        final KeycloakMockDto mock = new KeycloakMockDto();
        String enteredCode = "654321";
        long expiryDate =  System.currentTimeMillis() + 300000;
        AuthenticationFlowContext context = mock.getAuthenticationFlowContext();
        mock.addHttpRequestParam(Constants.RequestConstants.USERNAME, USERNAME);
        mock.addHttpRequestParam(Constants.RequestConstants.SMS_CODE, enteredCode);
        when(mock.getUserModel().getFirstAttribute(Constants.UserAttributes.SMS_CODE)).thenReturn(EXPECTED_CODE);
        when(mock.getUserModel().getFirstAttribute(Constants.UserAttributes.EXPIRY_DATE)).thenReturn(String.valueOf(expiryDate));

        authenticator.authenticate(context);

        verify(context).failureChallenge(
                eq(AuthenticationFlowError.ACCESS_DENIED),
                any()
        );
        verify(context.form()).setError("smsAuthSmsCodeWrong");
    }

    @Test
    void authenticateShouldFailWhenCodeIsExpired() {
        final KeycloakMockDto mock = new KeycloakMockDto();
        String enteredCode = "123456";
        long expiryDate =  System.currentTimeMillis() - 300000;
        AuthenticationFlowContext context = mock.getAuthenticationFlowContext();
        mock.addHttpRequestParam(Constants.RequestConstants.USERNAME, USERNAME);
        mock.addHttpRequestParam(Constants.RequestConstants.SMS_CODE, enteredCode);
        when(mock.getUserModel().getFirstAttribute(Constants.UserAttributes.SMS_CODE)).thenReturn(EXPECTED_CODE);
        when(mock.getUserModel().getFirstAttribute(Constants.UserAttributes.EXPIRY_DATE)).thenReturn(String.valueOf(expiryDate));

        authenticator.authenticate(context);

        verify(context).failureChallenge(
                eq(AuthenticationFlowError.ACCESS_DENIED),
                any()
        );
        verify(context.form()).setError("smsAuthSmsCodeWrong");
    }
    @Test
    void authenticateShouldSucceedWhenCodeIsValid() {
        final KeycloakMockDto mock = new KeycloakMockDto();
        String enteredCode = "123456";
        long expiryDate =  System.currentTimeMillis() + 300000;
        AuthenticationFlowContext context = mock.getAuthenticationFlowContext();
        mock.addHttpRequestParam(Constants.RequestConstants.USERNAME, USERNAME);
        mock.addHttpRequestParam(Constants.RequestConstants.SMS_CODE, enteredCode);
        when(mock.getUserModel().getFirstAttribute(Constants.UserAttributes.SMS_CODE)).thenReturn(EXPECTED_CODE);
        when(mock.getUserModel().getFirstAttribute(Constants.UserAttributes.EXPIRY_DATE)).thenReturn(String.valueOf(expiryDate));

        authenticator.authenticate(context);

        verify(context).success();
    }


}
*/
