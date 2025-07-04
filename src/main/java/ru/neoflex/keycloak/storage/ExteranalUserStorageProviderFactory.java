package ru.neoflex.keycloak.storage;

import com.google.auto.service.AutoService;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.keycloak.utils.StringUtil;
import ru.neoflex.keycloak.util.Constants;

import java.util.List;

@AutoService(UserStorageProviderFactory.class)
public class ExteranalUserStorageProviderFactory implements UserStorageProviderFactory<ExternalUserStorageProvider> {
    private static final String PROVIDER_ID = "exteral-user-storage-provider";

    @Override
    public ExternalUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new ExternalUserStorageProvider(session, model);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property(Constants.UserStorage.URL, Constants.UserStorage.URL, "external DB URL", ProviderConfigProperty.STRING_TYPE, "jdbc:postgresql://postgres:5432/postgres", null)
                .property(Constants.UserStorage.USERNAME, Constants.UserStorage.USERNAME, "external DB username", ProviderConfigProperty.STRING_TYPE, "postgres", null)
                .property(Constants.UserStorage.PASSWORD, Constants.UserStorage.PASSWORD, "external DB password", ProviderConfigProperty.PASSWORD, "postgres", null)
                .build();
    }


    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        if (config.getId() == null) {
            config.setId(KeycloakModelUtils.generateShortId());
        }

        if (StringUtil.isBlank(config.get(Constants.UserStorage.URL))
                || StringUtil.isBlank(config.get(Constants.UserStorage.USERNAME))
                || StringUtil.isBlank(config.get(Constants.UserStorage.PASSWORD))) {
            throw new ComponentValidationException("Required properties weren't filled");
        }
    }
}
