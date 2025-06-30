package ru.neoflex.keycloak.gateway.sms;

import lombok.extern.slf4j.Slf4j;
import ru.neoflex.keycloak.SmsConfiguration;

import java.net.http.HttpClient;
import java.time.Duration;


@Slf4j
public class SmsServiceFactory {
	private static final HttpClient httpClient = HttpClient
			.newBuilder()
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	public static SmsService get(SmsConfiguration config) {
		if (config.isSimulationMode()) {
			return (phoneNumber, message) ->
					log.info("***** SIMULATION MODE ***** Would send SMS to {} with text: {}",
							phoneNumber, message);
		} else {
			return new SmsServiceImpl(config, httpClient);
		}
	}

}
