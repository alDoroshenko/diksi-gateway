package ru.neoflex.keycloak.user;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;
import java.util.stream.Stream;

@Slf4j

public class ExternalUserStorageProvider implements
        UserStorageProvider
        , UserLookupProvider
        , CredentialInputValidator
        , UserQueryProvider {
    private final KeycloakSession session;
    private final ComponentModel model;
    private final UserRepository userRepository;

    public ExternalUserStorageProvider(KeycloakSession session, ComponentModel model) {
        log.debug("Creating new PropertyFileUserStorageProvider instance");
        this.session = session;
        this.model = model;
        this.userRepository = new UserRepository("jdbc:postgresql://postgres:5432/postgres", "postgres", "postgres");
        //this.userRepository = userRepository;
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        log.info("getUserByid: {}", id);
        String externalId = StorageId.externalId(id);
        log.info("externalId: {}", externalId);
       ExteranalUser exteranalUser = userRepository.getUserById(externalId);
        if (exteranalUser != null) {
            return new ExternalUserAdapter(session, realm, model, exteranalUser);
        } else {
            log.error("Failed to get user by id");
        }
        log.info("could not find  by id: {}", externalId);
        return null;
        //return getUserByUsername(realm, externalId);
    }


    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        log.info("getUserByUsername: {}", username);

        ExteranalUser exteranalUser = userRepository.getUserByUsername(username);
        if (exteranalUser != null) {
            return new ExternalUserAdapter(session, realm, model, exteranalUser);
        } else {
            log.error("Failed to get user by username");
        }
        log.info("could not find getUserByUsername: {}", username);
        return null;
    }


    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        log.info("getUserByEmail: {}", email);
        ExteranalUser exteranalUser = userRepository.getUserByEmail(email);
        if (exteranalUser != null) {
            return new ExternalUserAdapter(session, realm, model, exteranalUser);
        } else {
            log.error("Failed to get user by email");
        }
        log.info("could not find by email: {}", email);
        return null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        log.debug("Checking support for credential type: {}", credentialType);
        boolean supported = PasswordCredentialModel.TYPE.equals(credentialType);
        log.debug("Credential type {} supported: {}", credentialType, supported);
        return supported;
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        log.debug("Checking if credential type {} is configured for user: {}", credentialType, user.getUsername());

        boolean configured = supportsCredentialType(credentialType) &&
                userRepository.getUserByUsername(user.getUsername()) != null;
        log.debug("Credential type {} configured for user {}: {}", credentialType, user.getUsername(), configured);
        return configured;

    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        log.debug("Validating credential for user: {}", user.getUsername());

        if (!supportsCredentialType(input.getType())) {
            log.debug("Credential type not supported: {}", input.getType());
            return false;
        }

        ExteranalUser exteranalUser = userRepository.getUserByUsername(user.getUsername());
        if (exteranalUser == null) {
            log.debug("User entity not found for username: {}", user.getUsername());
            return false;
        }

        // Получаем хэш пароля из внешней системы
        String storedPasswordHash = exteranalUser.getPassword();

        String inputPassword = input.getChallengeResponse();

        return BCrypt.checkpw(inputPassword, storedPasswordHash);
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        log.info("searchForUserStream: {}", params);
        String search = params.get(UserModel.SEARCH);
        String lower = search != null ? search.toLowerCase() : "";

        log.debug("Searching for users with search term: {}", lower);

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
}
