package ru.neoflex.keycloak.jpa.repository.provider;


import ru.neoflex.keycloak.jpa.repository.JpaRepository;
import ru.neoflex.keycloak.jpa.repository.provider.spi.JpaRepositoryProvider;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class OfrJpaRepositoryProvider implements JpaRepositoryProvider {
    private final Set<JpaRepository<?, ?>> jpaRepositories;

    public OfrJpaRepositoryProvider(Set<JpaRepository<?, ?>> jpaRepositories) {
        Objects.requireNonNull(jpaRepositories);
        this.jpaRepositories = jpaRepositories;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends JpaRepository<?, ?>> R getJpaRepository(Class<R> repositoryClass) {
        Objects.requireNonNull(repositoryClass);
        return (R) jpaRepositories.stream()
                .filter(it -> it.getClass() == repositoryClass)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No such JpaRepository"));
    }

    @Override
    public void close() {
        jpaRepositories.forEach(JpaRepository::close);
    }
}
