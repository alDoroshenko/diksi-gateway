package ru.neoflex.keycloak.storage;

import com.google.auto.service.AutoService;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.storage.UserStorageProviderFactory;
import ru.neoflex.keycloak.jpa.repository.UserJPARepository;
import ru.neoflex.keycloak.jpa.repository.provider.spi.JpaRepositoryProvider;

@AutoService(UserStorageProviderFactory.class)
public class ExteranalUserStorageProviderFactory implements UserStorageProviderFactory<ExternalUserStorageProvider> {
    private static final String PROVIDER_ID = "exteral-user-storage-provider";
    private KeycloakSession session;


    @Override
    public void postInit(KeycloakSessionFactory factory) {
        this.session = factory.create();
    }
    @Override
    public ExternalUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        JpaRepositoryProvider jpaRepositoryProvider = session.getProvider(JpaRepositoryProvider.class, "ofr");
        UserJPARepository userJPARepository = jpaRepositoryProvider
                .getJpaRepository(UserJPARepository.class);
        return new ExternalUserStorageProvider(session, model,userJPARepository);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public void close() {
        if (session != null) {
            session.close();
        }
    }

    public interface Properties {
        String ClearIncompleteRegistrationsJobEnabled = "ClearIncompleteRegistrationsJobEnabled";
        String ClearIncompleteRegistrationsJobCronExpression = "ClearIncompleteRegistrationsJobCronExpression";
        String ClearIncompleteRegistrationsJobRequiredProperties = "ClearIncompleteRegistrationsJobRequiredProperties";
        String SaltLength = "SaltLength";
        String HashLength = "HashLength";
        String Parallelism = "Parallelism";
        String Memory = "Memory";
        String Iterations = "Iterations";
    }
}
