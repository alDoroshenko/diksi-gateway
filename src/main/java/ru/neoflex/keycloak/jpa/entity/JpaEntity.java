package ru.neoflex.keycloak.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface JpaEntity<ID> {
    ID getId();

    default boolean isNew() {
        return getId() == null;
    }

    static String getEntityName(Class<? extends JpaEntity<?>> entityClass) {
        Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
        if (entityAnnotation == null || Objects.equals(entityAnnotation.name(), "")) {
            return entityClass.getSimpleName();
        } else {
            return entityAnnotation.name();
        }
    }

    static Set<String> getPropertyNames(Class<? extends JpaEntity<?>> entityClass) {
        Set<String> result = new HashSet<>();
        for (Field field : Arrays.stream(entityClass.getDeclaredFields())
                .filter(it -> !Modifier.isTransient(it.getModifiers()) && !it.isAnnotationPresent(Transient.class))
                .collect(Collectors.toList())) {
            result.add(field.getName());
        }
        return result;
    }
}
