package ru.neoflex.keycloak.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.jpa.entities.ComponentConfigEntity;
import org.keycloak.models.jpa.entities.ComponentEntity;
import org.keycloak.models.utils.KeycloakModelUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Allows you to setup global configuration for your component in any realm
 */
public final class GlobalComponentConfig {
    private final EntityManager entityManager;
    private final String providerType;
    private final String providerId;
    private final Set<String> globalConfigKeys;

    private MultivaluedHashMap<String, String> globalConfig;

    private Consumer<MultivaluedHashMap<String, String>> updateHandler;

    public GlobalComponentConfig(
            EntityManager entityManager,
            String providerType,
            String providerId,
            Set<String> globalConfigKeys
    ) {
        Objects.requireNonNull(entityManager);
        Objects.requireNonNull(providerType);
        Objects.requireNonNull(providerId);
        Objects.requireNonNull(globalConfigKeys);
        this.entityManager = entityManager;
        this.providerType = providerType;
        this.providerId = providerId;
        this.globalConfigKeys = globalConfigKeys;
    }

    public void tryUpdateFromDatabase() {
        List<ComponentEntity> componentEntities = select().setFirstResult(0).setMaxResults(1).getResultList();
        if (!componentEntities.isEmpty()) {
            update(entityToModel(componentEntities.get(0)));
        }
    }

    public void update(ComponentModel component) {
        globalConfig = filterGlobalConfig(component.getConfig());
        updateEntities(component);
        handleUpdate();
    }

    public MultivaluedHashMap<String, String> getGlobalConfig() {
        return globalConfig;
    }

    public GlobalComponentConfig setUpdateHandler(Consumer<MultivaluedHashMap<String, String>> updateHandler) {
        this.updateHandler = updateHandler;
        return this;
    }

    private void updateEntities(ComponentModel component) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            for (ComponentEntity componentEntity : selectWhereIdNot(component.getId()).getResultList()) {
                globalConfig.forEach((name, values) -> {
                    Set<ComponentConfigEntity> componentConfigs = componentEntity.getComponentConfigs();
                    componentConfigs.removeIf(it -> Objects.equals(it.getName(), name));
                    for (String val : values) {
                        ComponentConfigEntity config = new ComponentConfigEntity();
                        config.setId(KeycloakModelUtils.generateId());
                        config.setName(name);
                        config.setValue(val);
                        config.setComponent(componentEntity);
                        componentConfigs.add(config);
                    }
                });
            }
            transaction.commit();
        } catch (Throwable throwable) {
            transaction.rollback();
            throw throwable;
        }
    }

    private TypedQuery<ComponentEntity> select() {
        return entityManager.createQuery(
                        "SELECT entity FROM ComponentEntity entity"
                                + " WHERE entity.providerType = :providerType"
                                + " AND entity.providerId = :providerId",
                        ComponentEntity.class
                ).setParameter("providerType", providerType)
                .setParameter("providerId", providerId);
    }

    private TypedQuery<ComponentEntity> selectWhereIdNot(String id) {
        return entityManager.createQuery(
                        "SELECT entity FROM ComponentEntity entity"
                                + " WHERE entity.providerType = :providerType"
                                + " AND entity.providerId = :providerId"
                                + " AND entity.id <> :id",
                        ComponentEntity.class
                ).setParameter("providerType", providerType)
                .setParameter("providerId", providerId)
                .setParameter("id", id);
    }

    private ComponentModel entityToModel(ComponentEntity entity) {
        ComponentModel model = new ComponentModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setProviderType(entity.getProviderType());
        model.setProviderId(entity.getProviderId());
        model.setSubType(entity.getSubType());
        model.setParentId(entity.getParentId());
        MultivaluedHashMap<String, String> config = new MultivaluedHashMap<>();
        for (ComponentConfigEntity configEntity : entity.getComponentConfigs()) {
            config.add(configEntity.getName(), configEntity.getValue());
        }
        model.setConfig(config);
        return model;
    }

    private MultivaluedHashMap<String, String> filterGlobalConfig(MultivaluedHashMap<String, String> config) {
        return new MultivaluedHashMap<>(config.entrySet().stream()
                .filter(it -> globalConfigKeys.contains(it.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private void handleUpdate() {
        if (updateHandler != null) {
            updateHandler.accept(globalConfig);
        }
    }
}
