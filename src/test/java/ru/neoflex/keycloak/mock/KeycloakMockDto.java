package ru.neoflex.keycloak.mock;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.*;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.storage.UserStorageManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@Getter
public class KeycloakMockDto {
    private final AuthenticationFlowContext authenticationFlowContext;
    private final AuthenticationSessionModel authSession;
    private final LoginFormsProvider loginFormBuilder;
    private final KeycloakSession keycloakSession;
    private final KeycloakContext keycloakContext;
    private final AuthenticatorConfigModel config;
    private final RealmModel realmModel;
    private final UserModel userModel;
    private final KeycloakUriInfo keycloakUriInfo;
    private final UserStorageManager userStorageManager;

    private final Map<String, String> configParams = new HashMap<>();
    private final MultivaluedMap<String, String> httpRequestParams = new MultivaluedHashMap<>();

    public KeycloakMockDto() {
        config = mock(AuthenticatorConfigModel.class);
        authenticationFlowContext = mock(AuthenticationFlowContext.class);
        authSession = mock(AuthenticationSessionModel.class);
        loginFormBuilder = mock(LoginFormsProvider.class);
        keycloakSession = mock(KeycloakSession.class);
        keycloakContext = mock(KeycloakContext.class);
        realmModel = mock(RealmModel.class);
        userModel = mock(UserModel.class);
        keycloakUriInfo = mock(KeycloakUriInfo.class);
        userStorageManager = mock(UserStorageManager.class);
        final HttpRequest request = mock(HttpRequest.class);
        when(keycloakUriInfo.getPath()).thenReturn("/path");
        when(keycloakContext.getUri()).thenReturn(keycloakUriInfo);
        when(keycloakSession.getContext())
                .thenReturn(keycloakContext);
        when(keycloakContext.getRealm())
                .thenReturn(realmModel);
        when(keycloakContext.getAuthenticationSession())
                .thenReturn(authSession);
        when(authenticationFlowContext.getSession())
                .thenReturn(keycloakSession);
        when(authenticationFlowContext.form())
                .thenReturn(loginFormBuilder);
        when(authenticationFlowContext.getUser())
                .thenReturn(userModel);
        when(config.getConfig()).thenReturn(configParams);
        when(loginFormBuilder.setError(anyString(), any())).thenReturn(loginFormBuilder);
        when(loginFormBuilder.setAttribute(anyString(), any())).thenReturn(loginFormBuilder);
        when(loginFormBuilder.createForm(anyString())).thenReturn(mock(Response.class));
        when(authenticationFlowContext.getHttpRequest()).thenReturn(request);
        when(request.getDecodedFormParameters()).thenReturn(httpRequestParams);
        when(authenticationFlowContext.getAuthenticationSession()).thenReturn(authSession);
        when(authSession.getRealm()).thenReturn(realmModel);
        when(authenticationFlowContext.getRealm()).thenReturn(realmModel);
        when(authenticationFlowContext.getAuthenticatorConfig()).thenReturn(config);
    }

    public void addConfig(String name, String value) {
        configParams.put(name, value);
    }

    public void addAuthNode(String name, String value) {
        when(authSession.getAuthNote(name)).thenReturn(value);
    }

    public void addUserParam(String name, String value) {
        when(userModel.getFirstAttribute(name)).thenReturn(value);
        when(userModel.getAttributeStream(name)).then(inv -> Stream.of(value));
        when(userModel.getAttributes()).thenReturn(Map.of(name, List.of(value)));
    }

    public void addHttpRequestParam(String name, String value) {
        httpRequestParams.put(name, Collections.singletonList(value));
    }
}
