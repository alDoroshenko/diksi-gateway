package ru.neoflex.keycloak.jpa.repository.provider.spi;

import org.keycloak.provider.Provider;
import ru.neoflex.keycloak.jpa.repository.JpaRepository;


public interface JpaRepositoryProvider extends Provider {
    <R extends JpaRepository<?, ?>> R getJpaRepository(Class<R> repositoryClass);
}
