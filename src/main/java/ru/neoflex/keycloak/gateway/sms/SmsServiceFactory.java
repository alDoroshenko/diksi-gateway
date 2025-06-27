package ru.neoflex.keycloak.gateway.sms;

import lombok.extern.slf4j.Slf4j;
import ru.neoflex.keycloak.util.Constants;

import java.net.http.HttpClient;
import java.util.Map;


@Slf4j
public class SmsServiceFactory {

	public static SmsService get(Map<String, String> config) {
		if (Boolean.parseBoolean(config.getOrDefault(Constants.SmsAuthConstants.SIMULATION_MODE, "false"))) {
			return (phoneNumber, message) ->
					log.info("***** SIMULATION MODE ***** Would send SMS to {} with text: {}",
							phoneNumber, message);
		} else {
			return new SmsServiceImpl(config, HttpClient.newHttpClient());
		}
	}

}
