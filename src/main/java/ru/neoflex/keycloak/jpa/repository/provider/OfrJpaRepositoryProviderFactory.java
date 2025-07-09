package ru.neoflex.keycloak.jpa.repository.provider;

import com.google.auto.service.AutoService;
import jakarta.persistence.EntityManager;
import org.keycloak.Config;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import ru.neoflex.keycloak.jpa.repository.UserJPARepository;
import ru.neoflex.keycloak.jpa.repository.provider.spi.JpaRepositoryProvider;
import ru.neoflex.keycloak.jpa.repository.provider.spi.JpaRepositoryProviderFactory;

import java.util.Set;

@AutoService(JpaRepositoryProviderFactory.class)
public class OfrJpaRepositoryProviderFactory implements JpaRepositoryProviderFactory {

    @Override
    public JpaRepositoryProvider create(KeycloakSession session) {
        EntityManager entityManager = session.getProvider(JpaConnectionProvider.class, "ofr").getEntityManager();
        return new OfrJpaRepositoryProvider(
                Set.of(
                        new UserJPARepository(entityManager)
                )
        );
    }

    @Override
    public void init(Config.Scope config) { /* pass */ }

    @Override
    public void postInit(KeycloakSessionFactory factory) { /* pass */ }

    @Override
    public void close() { /* pass */ }

    @Override
    public String getId() {
        return "ofr";
    }
}
