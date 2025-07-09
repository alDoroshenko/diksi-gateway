package ru.neoflex.keycloak.jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import ru.neoflex.keycloak.jpa.entity.JpaEntity;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractJpaRepository<E extends JpaEntity<ID>, ID> implements JpaRepository<E, ID> {
    protected final EntityManager entityManager;

    public AbstractJpaRepository(EntityManager entityManager) {
        Objects.requireNonNull(entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public void transactional(Runnable block) {
        Objects.requireNonNull(block);
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            block.run();
            transaction.commit();
        } catch (Throwable throwable) {
            transaction.rollback();
            throw throwable;
        }
    }

    @Override
    public <R> R transactional(Supplier<R> block) {
        Objects.requireNonNull(block);
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        R result;
        try {
            result = block.get();
            transaction.commit();
        } catch (Throwable throwable) {
            transaction.rollback();
            throw throwable;
        }
        return result;
    }

    @Override
    public int count() {
        return entityManager.createQuery(
                String.format("SELECT COUNT(x) FROM %s x", getEntityName()),
                Integer.class
        ).getSingleResult();
    }

    @Override
    public E save(E entity) {
        Objects.requireNonNull(entity, "Entity must not be null");
        return transactional(() -> {
            if (entity.isNew()) {
                entityManager.persist(entity);
                return entity;
            } else {
                return entityManager.merge(entity);
            }
        });
    }

    @Override
    public Collection<E> saveAll(Collection<E> entities) {
        Objects.requireNonNull(entities, "Entities must not be null");
        return transactional(() -> {
            List<E> result = new ArrayList<>();
            for (E entity : entities) {
                Objects.requireNonNull(entity, "Entity must not be null");
                result.add(save(entity));
            }
            return result;
        });
    }

    @Override
    public E findById(ID id) {
        Objects.requireNonNull(id, "Id must not be null");
        return entityManager.find(getEntityClass(), id);
    }

    @Override
    public Collection<E> findAllByIds(Collection<ID> ids) {
        Objects.requireNonNull(ids, "Ids must not be null");
        List<E> result = new ArrayList<>();
        for (ID id : ids) {
            Objects.requireNonNull(id, "Id must not be null");
            result.add(findById(id));
        }
        return result;
    }

    @Override
    public Collection<E> findAll() {
        Class<E> entityClass = getEntityClass();
        CriteriaQuery<E> query = entityManager.getCriteriaBuilder().createQuery(entityClass);
        return entityManager.createQuery(query.select(query.from(entityClass))).getResultList();
    }

    @Override
    public void delete(E entity) {
        Objects.requireNonNull(entity, "Entity must not be null");
        transactional(() -> {
            if (!entity.isNew() && entityManager.find(getEntityClass(), entity.getId()) != null) {
                entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
            }
        });
    }

    @Override
    public void deleteById(ID id) {
        Objects.requireNonNull(id, "Id must not be null");
        transactional(() -> {
            E entity = findById(id);
            if (entity != null) {
                delete(entity);
            }
        });
    }

    @Override
    public void deleteAllByIds(Collection<ID> ids) {
        Objects.requireNonNull(ids, "Ids must not be null");
        transactional(() -> {
            for (ID id : ids) {
                Objects.requireNonNull(id, "Id must not be null");
                deleteById(id);
            }
        });
    }

    @Override
    public void deleteAll() {
        transactional(() -> {
            for (E entity : findAll()) {
                delete(entity);
            }
        });
    }

    @Override
    public void close() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    protected abstract Class<E> getEntityClass();

    protected String getEntityName() {
        return JpaEntity.getEntityName(getEntityClass());
    }

    protected E getSingleResultFromQueryOrNull(TypedQuery<E> query) {
        List<E> resultList = query.getResultList();
        if (resultList.size() > 1) {
            throw new NonUniqueResultException("More than one result for query: " + query);
        }
        if (resultList.isEmpty()) {
            return null;
        }
        return resultList.get(0);
    }
}
