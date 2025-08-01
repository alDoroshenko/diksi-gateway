package ru.neoflex.keycloak.authenticator;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import ru.neoflex.keycloak.util.Constants;

import java.util.List;


@AutoService(AuthenticatorFactory.class)
public class SmsAuthenticatorFactory implements AuthenticatorFactory {

	public static final String PROVIDER_ID = "sms-authenticator";

	private static final SmsAuthenticator SINGLETON = new SmsAuthenticator();

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public String getDisplayType() {
		return "SMS Authentication";
	}

	@Override
	public String getHelpText() {
		return "Validates an OTP sent via SMS to the users mobile phone.";
	}

	@Override
	public String getReferenceCategory() {
		return "otp";
	}

	@Override
	public boolean isConfigurable() {
		return true;
	}

	@Override
	public boolean isUserSetupAllowed() {
		return true;
	}

	@Override
	public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
		return REQUIREMENT_CHOICES;
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return List.of(
			new ProviderConfigProperty(Constants.SmsAuthConstants.CODE_LENGTH, "Code length", "The number of digits of the generated code.", ProviderConfigProperty.STRING_TYPE, 6),
			new ProviderConfigProperty(Constants.SmsAuthConstants.CODE_TTL, "Time-to-live", "The time to live in seconds for the code to be valid.", ProviderConfigProperty.STRING_TYPE, "300"),
			new ProviderConfigProperty(Constants.SmsAuthConstants.SENDER_ID, "SenderId", "The sender ID is displayed as the message sender on the receiving device.", ProviderConfigProperty.STRING_TYPE, "Дикси"),
			new ProviderConfigProperty(Constants.SmsAuthConstants.SIMULATION_MODE, "Simulation mode", "In simulation mode, the SMS won't be sent, but printed to the server logs", ProviderConfigProperty.BOOLEAN_TYPE, true),
			new ProviderConfigProperty(Constants.SmsAuthConstants.SMS_URI, "SMS URI", "URI of SMS provider", ProviderConfigProperty.STRING_TYPE, ""),
			new ProviderConfigProperty(Constants.SmsAuthConstants.LOGIN, "Login", "Login for SMS provider", ProviderConfigProperty.STRING_TYPE, ""),
			new ProviderConfigProperty(Constants.SmsAuthConstants.PASSWORD, "Password", "Password for SMS provider", ProviderConfigProperty.STRING_TYPE, ""),
		    new ProviderConfigProperty(Constants.SmsAuthConstants.TEXT, "Message", "Message text for client", ProviderConfigProperty.STRING_TYPE, ""),
			new ProviderConfigProperty(Constants.ManzanaConstants.MANZANA_URI, "Manzana URI", "URI of Manzana", ProviderConfigProperty.STRING_TYPE, ""),
			new ProviderConfigProperty(Constants.ManzanaConstants.SESSION_ID, "Session Id", "Session Id for Manzana admin(UUID)", ProviderConfigProperty.STRING_TYPE, ""),
			new ProviderConfigProperty(Constants.ManzanaConstants.PARTNER_ID, "Partner Id", "Partner Id for Manzana admin(UUID)", ProviderConfigProperty.STRING_TYPE, ""),
			new ProviderConfigProperty(Constants.ManzanaConstants.VIRTUAL_CARD_TYPE_ID, "Virtual Card Type Id", "Virtual Card Type Id for Manzana(UUID)", ProviderConfigProperty.STRING_TYPE, "")

		);
	}

	@Override
	public Authenticator create(KeycloakSession session) {
		return SINGLETON;
	}

	@Override
	public void init(Config.Scope config) {
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
	}

	@Override
	public void close() {
	}

}
