package ru.neoflex.keycloak.gateway;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.neoflex.keycloak.dto.Data;
import ru.neoflex.keycloak.dto.Message;
import ru.neoflex.keycloak.dto.SMSGatewayDTO;
import ru.neoflex.keycloak.util.Constants;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Slf4j
public class SmsServiceImpl implements SmsService {
    private final String senderId;
    private final String login;
    private final String password;
    private static final String SMS_TYPE = "SMS";


    @Override
    public void send(String phoneNumber, String message) {
        SMSGatewayDTO smsGatewayDTO = new SMSGatewayDTO(login
                , password
                , phoneNumber
                , new Message(SMS_TYPE,
                new Data(message, senderId, 10)));
        ObjectMapper objectMapper = new ObjectMapper();

        log.info("smsGatewayDTO: {}", smsGatewayDTO);

        try {
            String smsGatewayJson = objectMapper.writeValueAsString(smsGatewayDTO);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://example.com/api/send")) // Замените на реальный URL API
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(smsGatewayJson))
                    .build();
            HttpResponse<String> response = client.send(
                    request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't serialize SMSGatewayDTO", e);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to send sms");
        }
    }

    public SmsServiceImpl(Map<String, String> config) {
        senderId = config.get(Constants.SmsAuthConstants.SENDER_ID);
        login = config.get(Constants.SmsAuthConstants.LOGIN);
        password = config.get(Constants.SmsAuthConstants.PASSWORD);
    }
}
