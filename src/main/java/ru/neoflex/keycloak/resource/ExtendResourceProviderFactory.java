package ru.neoflex.keycloak.resource;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;


@AutoService(RealmResourceProviderFactory.class)
public class ExtendResourceProviderFactory implements RealmResourceProviderFactory {

	public static final String PROVIDER_ID = "extend-rest-resource";

	@Override
	public RealmResourceProvider create(KeycloakSession keycloakSession) {
		return new ExtendResourceProvider(keycloakSession);
	}

	@Override
	public void init(Config.Scope scope) {
	}

	@Override
	public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
	}

	@Override
	public void close() {
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}
}
