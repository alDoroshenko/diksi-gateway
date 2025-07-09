package ru.neoflex.keycloak.jpa.unit;

import jakarta.activation.DataSource;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import lombok.Builder;
import lombok.Getter;

import java.net.URL;
import java.util.List;
import java.util.Properties;

@Getter
@Builder
public class PersistenceUnitInfoData implements PersistenceUnitInfo {
    private String persistenceUnitName;
    private String persistenceProviderClassName;
    private PersistenceUnitTransactionType transactionType;
    private DataSource jtaDataSource;
    private DataSource nonJtaDataSource;
    private List<String> mappingFileNames;
    private List<URL> jarFileUrls;
    private URL persistenceUnitRootUrl;
    private List<String> managedClassNames;
    private boolean excludeUnlistedClasses;
    private SharedCacheMode sharedCacheMode;
    private ValidationMode validationMode;
    private Properties properties;
    private String persistenceXMLSchemaVersion;
    private ClassLoader classLoader;
    private ClassLoader newTempClassLoader;

    @Override
    public boolean excludeUnlistedClasses() {
        return excludeUnlistedClasses;
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public javax.sql.DataSource getJtaDataSource() {
        return null;
    }

    @Override
    public javax.sql.DataSource getNonJtaDataSource() {
        return null;
    }
}
