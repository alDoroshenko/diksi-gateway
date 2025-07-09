package ru.neoflex.keycloak.jpa.repository;



import ru.neoflex.keycloak.jpa.entity.JpaEntity;

import java.util.Collection;
import java.util.function.Supplier;

public interface JpaRepository<E extends JpaEntity<ID>, ID> {

    void transactional(Runnable block);

    <R> R transactional(Supplier<R> block);

    int count();

    E save(E entity);

    Collection<E> saveAll(Collection<E> entities);

    E findById(ID id);

    Collection<E> findAllByIds(Collection<ID> ids);

    Collection<E> findAll();

    void delete(E entity);

    void deleteById(ID id);

    void deleteAllByIds(Collection<ID> ids);

    void deleteAll();

    void close();
}
