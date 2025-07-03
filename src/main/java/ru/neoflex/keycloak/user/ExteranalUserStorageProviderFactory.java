package ru.neoflex.keycloak.user;

import com.google.auto.service.AutoService;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.UserStorageProviderFactory;
import ru.neoflex.keycloak.util.Constants;

import java.util.List;

@AutoService(UserStorageProviderFactory.class)
public class ExteranalUserStorageProviderFactory implements UserStorageProviderFactory<ExternalUserStorageProvider> {
    private static final String PROVIDER_ID = "exteral-user-storage-provider";

    @Override
    public ExternalUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new ExternalUserStorageProvider(session,model);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
/*
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property(Constants.UserStorage.URL, "apiBaseUrl", "apiBaseUrlHelp", ProviderConfigProperty.STRING_TYPE, "http://localhost:8000", null)
                .property(CLIENT_ID, "apiClientId", "apiClientIdHelp", ProviderConfigProperty.CLIENT_LIST_TYPE, "", null)
                .property(EDIT_MODE, "editMode", "editModeHelp", ProviderConfigProperty.LIST_TYPE, UserStorageProvider.EditMode.READ_ONLY, List.of(UserStorageProvider.EditMode.READ_ONLY.name(), UserStorageProvider.EditMode.WRITABLE.name()))
                .property(USER_IMPORT, "importUsers", "importUsersHelp", ProviderConfigProperty.BOOLEAN_TYPE, "false", null)
                .property(USER_CREATION_ENABLED, "syncRegistrations", "syncRegistrationsHelp", ProviderConfigProperty.BOOLEAN_TYPE, "false", null)
                .property(USE_PASSWORD_POLICY, "validatePasswordPolicy", "validatePasswordPolicyHelp", ProviderConfigProperty.BOOLEAN_TYPE, "false", null)
                .build();
    }*/
}
