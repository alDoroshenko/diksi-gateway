package ru.neoflex.keycloak.gateway.sms;


import ru.neoflex.keycloak.exceptions.SmsGatewayException;

public interface SmsService {

	void send(String phoneNumber, String message) throws SmsGatewayException;

}
