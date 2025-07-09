package ru.neoflex.keycloak.jpa.repository.provider.spi;

import com.google.auto.service.AutoService;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class JpaRepositorySpi implements Spi {

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return "repositoriesJpa";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return JpaRepositoryProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory<JpaRepositoryProvider>> getProviderFactoryClass() {
        return JpaRepositoryProviderFactory.class;
    }
}
