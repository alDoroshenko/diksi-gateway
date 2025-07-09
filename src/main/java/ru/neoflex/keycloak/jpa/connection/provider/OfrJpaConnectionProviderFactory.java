package ru.neoflex.keycloak.jpa.connection.provider;

import com.google.auto.service.AutoService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceUnitInfo;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.keycloak.Config;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.connections.jpa.JpaConnectionProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;
import org.keycloak.transaction.JtaTransactionManagerLookup;
import ru.neoflex.keycloak.jpa.entity.ExteranalUser;
import ru.neoflex.keycloak.jpa.unit.PersistenceUnitInfoData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Config under `spi-connections-jpa-ofr-`
 * <p>
 * - url: required, jdbc url
 * <p>
 * - username: required
 * <p>
 * - password: required
 * <p>
 * - driver: optional, will try to autodetect
 * <p>
 * - dialect: optional, will try to autodetect
 */
@Slf4j
@AutoService(JpaConnectionProviderFactory.class)
public class OfrJpaConnectionProviderFactory implements JpaConnectionProviderFactory, ServerInfoAwareProviderFactory {
    private static final String OFR_PERSISTENCE_UNIT_INFO = "ofr";

    private static final PersistenceUnitInfo PERSISTENCE_UNIT_INFO = PersistenceUnitInfoData.builder()
            .persistenceUnitName(OFR_PERSISTENCE_UNIT_INFO)
            .persistenceProviderClassName("org.hibernate.jpa.HibernatePersistenceProvider")
            .managedClassNames(
                    Stream.of(ExteranalUser.class
                            )
                            .map(Class::getName)
                            .collect(Collectors.toList())
            )
            .build();

    private String url;
    private String username;
    private String password;
    private String driver;
    private String dialect;
    private String schema;

    private volatile EntityManagerFactory entityManagerFactory;
    private boolean jtaEnabled;
    private JtaTransactionManagerLookup jtaLookup;

    @Override
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to OFR database", e);
        }
    }

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public JpaConnectionProvider create(KeycloakSession session) {
        lazyInitEntityManagerFactory(session);
        return new OfrJpaConnectionProvider(entityManagerFactory.createEntityManager());
    }

    @Override
    public void init(Config.Scope config) {
        this.url = config.get("url");
        Objects.requireNonNull(url, "spi-connections-jpa-ofr-url is null");
        this.username = config.get("username");
        Objects.requireNonNull(username, "spi-connections-jpa-ofr-username is null");
        this.password = config.get("password");
        Objects.requireNonNull(password, "spi-connections-jpa-ofr-password is null");
        this.driver = config.get("driver");
        if (driver == null) {
            try {
                driver = DriverManager.getDriver(url).getClass().getName();
            } catch (SQLException e) {
                throw new RuntimeException("No suitable SQL driver", e);
            }
        }
        this.dialect = config.get("dialect");
        try (Connection connection = getConnection()) {
            this.schema = connection.getSchema();
        } catch (SQLException e) {
            throw new RuntimeException("Can't connect to OFR database", e);
        }
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        checkJtaEnabled(factory);
    }

    protected void checkJtaEnabled(KeycloakSessionFactory factory) {
        this.jtaLookup = (JtaTransactionManagerLookup) factory.getProviderFactory(JtaTransactionManagerLookup.class);
        if (this.jtaLookup != null && this.jtaLookup.getTransactionManager() != null) {
            this.jtaEnabled = true;
        }

    }

    @Override
    public void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            synchronized (this) {
                if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
                    entityManagerFactory.close();
                }
            }
        }
    }

    @Override
    public String getId() {
        return "ofr";
    }

    @Override
    public Map<String, String> getOperationalInfo() {
        return Map.of(
                "url", url,
                "user", username
        );
    }

    private void lazyInitEntityManagerFactory(KeycloakSession session) {
        if (entityManagerFactory == null) {
            synchronized (this) {
                if (entityManagerFactory == null) {
                    Map<String, Object> properties = new HashMap<>();

                    properties.put(AvailableSettings.CLASSLOADERS, List.of(getClass().getClassLoader()));
                    properties.put(AvailableSettings.DRIVER, driver);
                    properties.put(AvailableSettings.URL, url);
                    properties.put(AvailableSettings.USER, username);
                    properties.put(AvailableSettings.PASS, password);
                    properties.put(AvailableSettings.DIALECT, dialect);
                    properties.put(AvailableSettings.SHOW_SQL, true);
                    properties.put(AvailableSettings.FORMAT_SQL, true);

                    entityManagerFactory = new HibernatePersistenceProvider()
                            .createContainerEntityManagerFactory(PERSISTENCE_UNIT_INFO, properties);
                }
            }
        }
    }
}
