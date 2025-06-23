package ru.neoflex.keycloak.gateway;

import lombok.extern.slf4j.Slf4j;
import ru.neoflex.keycloak.util.Constants;

import java.util.Map;


@Slf4j
public class SmsServiceFactory {

	public static SmsService get(Map<String, String> config) {
		if (Boolean.parseBoolean(config.getOrDefault(Constants.SmsConstants.SIMULATION_MODE, "false"))) {
			return new SmsService() {
                @Override
                public void send(String phoneNumber, String message) {
                    log.warn(String.format("***** SIMULATION MODE ***** Would send SMS to %s with text: %s", phoneNumber, message));
                }
            };
		} else {
			return new SmsServiceImpl(config);
		}
	}

}
