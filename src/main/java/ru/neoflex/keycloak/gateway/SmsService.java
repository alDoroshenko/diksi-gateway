package ru.neoflex.keycloak.gateway;


public interface SmsService {

	void send(String phoneNumber, String message);

}
