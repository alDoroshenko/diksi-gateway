package ru.neoflex.keycloak.storage;

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
import org.keycloak.storage.user.UserRegistrationProvider;
import ru.neoflex.keycloak.jpa.entity.ExteranalUser;
import ru.neoflex.keycloak.jpa.repository.UserJPARepository;
import ru.neoflex.keycloak.util.Constants;
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
    private final UserJPARepository userRepository;
    private Map<String, UserModel> loadedUsers = new HashMap<>();

    public ExternalUserStorageProvider(KeycloakSession session, ComponentModel model, UserJPARepository userRepository) {
        log.info("Creating new PropertyFileUserStorageProvider instance");
        this.session = session;
        this.model = model;
        this.userRepository = userRepository;
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
                user = new ExternalUserAdapter(session, realm, model, exteranalUser,userRepository);
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
                user = new ExternalUserAdapter(session, realm, model, exteranalUser,userRepository);
                loadedUsers.put(email, user);
                return user;
            }
            log.info("could not find by email: {}", email);
        }
        return user;
    }
    @Override
    public int getUsersCount(RealmModel realm) {
        return userRepository.count();
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

        Stream<UserModel> userStream = userRepository.findAll().stream()
                .filter(userEntity -> userEntity.getUsername().toLowerCase().contains(lower) ||
                        userEntity.getEmail().toLowerCase().contains(lower))
                .map(entity -> new ExternalUserAdapter(session, realm, model, entity,userRepository));
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
        exteranalUser.setPassword(Constants.KeycloakConfiguration.DEFAULT_USER_PASSWORD);
        userRepository.save(exteranalUser);
        return new ExternalUserAdapter(session, realm, model, exteranalUser,userRepository);
    }

    @Override
    public boolean removeUser(RealmModel realmModel, UserModel user) {
        log.info("removeUser: {}", UserUtil.maskString(user.getUsername()));
        final ExteranalUser exteranalUser = userRepository.getUserByUsername(user.getUsername());
        if (exteranalUser == null) {
            log.info("Tried to delete invalid user with ID " + user.getId());
            return false;
        }
        userRepository.delete(exteranalUser);
        return true;
    }
}
