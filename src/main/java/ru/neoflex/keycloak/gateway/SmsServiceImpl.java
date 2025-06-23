package ru.neoflex.keycloak.gateway;


import java.util.Map;

public class SmsServiceImpl implements SmsService {
    private final String senderId;

    @Override
    public void send(String phoneNumber, String message) {

    }
    public SmsServiceImpl(Map<String, String> config) {
        senderId = config.get("senderId");
    }



}
