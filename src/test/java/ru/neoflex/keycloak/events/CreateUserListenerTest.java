package ru.neoflex.keycloak.events;

import org.junit.jupiter.api.Test;

import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.neoflex.keycloak.mock.KeycloakMockDto;
import ru.neoflex.keycloak.util.AuthProvider;
import ru.neoflex.keycloak.util.Constants;
import ru.neoflex.keycloak.util.SessionUtil;


import java.util.stream.Stream;

import static org.mockito.Mockito.*;


public class CreateUserListenerTest {

    private static final String USERNAME = "89162005555";
    final KeycloakMockDto mock = new KeycloakMockDto();
    private final CreateUserListener listener = new CreateUserListener(mock.getKeycloakSession());
    private final RealmProvider realmProvider = Mockito.mock(RealmProvider.class);
    private final UserProvider userProvider= Mockito.mock(UserProvider.class);
    private final AuthenticationFlowModel flow = Mockito.mock(AuthenticationFlowModel.class);
    private final AuthenticationExecutionModel execution = Mockito.mock(AuthenticationExecutionModel.class);
    @Test
    void shouldProcessUserCreationEvent() {
        RealmModel realm = mock.getRealmModel();
        UserModel user = mock.getUserModel();
        AuthenticatorConfigModel config = mock.getConfig();
        AdminEvent event = createAdminEvent(OperationType.CREATE,
                ResourceType.USER, "users/123");

        KeycloakSession session = mock.getKeycloakSession();
        when(session.realms()).thenReturn(realmProvider);
        when(realmProvider.getRealm("realmId")).thenReturn(realm);
        when(session.users()).thenReturn(userProvider);
        when(userProvider.getUserById(realm, "123")).thenReturn(user);
        when(user.getUsername()).thenReturn(USERNAME);

        when(realm.getFlowByAlias(Constants.KeycloakConfiguration.CUSTOM_DIRECT_GRANT_FLOW))
                .thenReturn(flow);
        when(realm.getAuthenticationExecutionsStream(flow.getId()))
                .thenReturn(Stream.of(execution));
        when(execution.getAuthenticator())
                .thenReturn(Constants.KeycloakConfiguration.SMS_AUTHENTICATOR_ID);
        when(execution.getAuthenticatorConfig())
                .thenReturn("config-id");
        when(realm.getAuthenticatorConfigById("config-id"))
                .thenReturn(config);

        try (var mockedAuthProvider = mockStatic(AuthProvider.class)) {
            listener.onEvent(event, false);
            mockedAuthProvider.verify(() -> AuthProvider.execute(config, user));
        }

    }
    @Test
    void shouldIgnoreNonUserEvents() {

        AdminEvent event = createAdminEvent(
                OperationType.CREATE,
                ResourceType.GROUP,
                "groups/456");
        listener.onEvent(event, false);

        verifyNoInteractions(mock.getUserModel(), mock.getConfig());
    }

    @Test
    void shouldIgnoreNonCreateOperations() {

        AdminEvent event = createAdminEvent(
                OperationType.UPDATE,
                ResourceType.USER,
                "users/123");

        listener.onEvent(event, false);
        verifyNoInteractions(mock.getUserModel(),
                mock.getConfig());
    }



    private AdminEvent createAdminEvent(OperationType operationType,
                                        ResourceType resourceType,
                                        String resourcePath) {
        AdminEvent event = mock(AdminEvent.class);
        when(event.getOperationType()).thenReturn(operationType);
        when(event.getResourceType()).thenReturn(resourceType);
        when(event.getResourcePath()).thenReturn(resourcePath);
        when(event.getRealmId()).thenReturn("realmId");
        return event;
    }

}
