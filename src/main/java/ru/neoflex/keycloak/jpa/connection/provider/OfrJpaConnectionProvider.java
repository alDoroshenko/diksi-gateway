package ru.neoflex.keycloak.jpa.connection.provider;

import jakarta.persistence.EntityManager;
import org.keycloak.connections.jpa.JpaConnectionProvider;

import java.util.Objects;

public class OfrJpaConnectionProvider implements JpaConnectionProvider {
    private final EntityManager entityManager;

    public OfrJpaConnectionProvider(EntityManager entityManager) {
        Objects.requireNonNull(entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void close() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
