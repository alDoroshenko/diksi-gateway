package ru.neoflex.keycloak.storage;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import ru.neoflex.keycloak.exception.ManzanaGatewayException;
import ru.neoflex.keycloak.exception.SmsGatewayException;
import ru.neoflex.keycloak.model.ExteranalUser;
import ru.neoflex.keycloak.provider.AuthProvider;
import ru.neoflex.keycloak.util.Constants;
import ru.neoflex.keycloak.util.SessionUtil;
import ru.neoflex.keycloak.util.UserUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class ExternalUserStorageProvider implements
        UserStorageProvider
        , UserLookupProvider
        , CredentialInputValidator
        , UserQueryProvider
        , UserRegistrationProvider {
    private final KeycloakSession session;
    private final ComponentModel model;
    private final UserRepository userRepository;
    private Map<String, UserModel> loadedUsers = new HashMap<>();

    public ExternalUserStorageProvider(KeycloakSession session, ComponentModel model) {
        log.info("Creating new PropertyFileUserStorageProvider instance");
        this.session = session;
        this.model = model;
        this.userRepository = new UserRepository(model);
    }

    @Override
    public void close() {
        userRepository.close();
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        String externalId = StorageId.externalId(id);
        return getUserByUsername(realm, externalId);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        UserModel user = loadedUsers.get(username);
        if (user == null) {
            ExteranalUser exteranalUser = userRepository.getUserByUsername(username);
            if (exteranalUser != null) {
                user = new ExternalUserAdapter(session, realm, model, exteranalUser);
                loadedUsers.put(username, user);
                return user;
            }
            log.info("could not find getUserByUsername: {}", UserUtil.maskString(username));
        }
        return user;
    }


    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        UserModel user = loadedUsers.get(email);
        if (user == null) {
            ExteranalUser exteranalUser = userRepository.getUserByEmail(email);
            if (exteranalUser != null) {
                user = new ExternalUserAdapter(session, realm, model, exteranalUser);
                loadedUsers.put(email, user);
                return user;
            }
            log.info("could not find by email: {}", email);
        }
        return user;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        log.info("Checking support for credential type: {}", credentialType);
        boolean supported = PasswordCredentialModel.TYPE.equals(credentialType);
        log.info("Credential type {} supported: {}", credentialType, supported);
        return supported;
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        log.info("Checking if credential type {} is configured for user: {}",
                credentialType, UserUtil.maskString(user.getUsername()));
        boolean configured = supportsCredentialType(credentialType) &&
                userRepository.getUserByUsername(user.getUsername()) != null;
        log.info("Credential type {} configured for user {}: {}",
                credentialType, UserUtil.maskString(user.getUsername()), configured);
        return configured;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        log.info("Validating credential for user: {}", UserUtil.maskString(user.getUsername()));

        if (!supportsCredentialType(input.getType())) {
            log.info("Credential type not supported: {}", input.getType());
            return false;
        }
        ExteranalUser exteranalUser = userRepository.getUserByUsername(user.getUsername());
        if (exteranalUser == null) {
            log.info("User entity not found for username: {}", UserUtil.maskString(user.getUsername()));
            return false;
        }
        String storedPassword = exteranalUser.getPassword();
        String inputPassword = input.getChallengeResponse();
        return inputPassword.equals(storedPassword);
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        String search = params.get(UserModel.SEARCH);
        String lower = search != null ? search.toLowerCase() : "";

        Stream<UserModel> userStream = userRepository.getAllUsers().stream()
                .filter(userEntity -> userEntity.getUsername().toLowerCase().contains(lower) ||
                        userEntity.getEmail().toLowerCase().contains(lower))
                .map(entity -> new ExternalUserAdapter(session, realm, model, entity));
        // Применяем постраничный вывод
        if (firstResult != null) {
            userStream = userStream.skip(firstResult);
        }
        if (maxResults != null) {
            userStream = userStream.limit(maxResults);
        }
        return userStream;
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realmModel, GroupModel groupModel, Integer integer, Integer integer1) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realmModel, String s, String s1) {
        return Stream.empty();
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        log.info("addUser: {}", UserUtil.maskString(username));
        ExteranalUser exteranalUser = new ExteranalUser();
        exteranalUser.setUsername(username);
        userRepository.save(exteranalUser);
        ExternalUserAdapter userAdapter = new ExternalUserAdapter(session, realm, model, exteranalUser);
        AuthenticatorConfigModel config = SessionUtil.getAuthenticatorConfig(realm,
                Constants.KeycloakConfiguration.SMS_AUTHENTICATOR_ID,
                Constants.KeycloakConfiguration.CUSTOM_DIRECT_GRANT_FLOW);
        try {
            AuthProvider authProvider = new AuthProvider(config, userAdapter, userRepository);
            authProvider.execute();
        } catch (SmsGatewayException e) {
            throw new RuntimeException(e.getMessage());
        } catch (ManzanaGatewayException e) {
            throw new RuntimeException(e.getMessage());
        }

        return userAdapter;
    }

    @Override
    public boolean removeUser(RealmModel realmModel, UserModel user) {
        log.info("removeUser: {}", UserUtil.maskString(user.getUsername()));
        String externalId = StorageId.externalId(user.getId());
        return userRepository.delete(externalId);
    }
}
