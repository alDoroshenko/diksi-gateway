package ru.neoflex.keycloak.gateway.sms;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.neoflex.keycloak.SmsConfiguration;
import ru.neoflex.keycloak.dto.sms.Data;
import ru.neoflex.keycloak.dto.sms.Message;
import ru.neoflex.keycloak.dto.sms.SMSGatewayDTO;
import ru.neoflex.keycloak.exception.SmsGatewayException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class SmsServiceImpl implements SmsService {

    private final String senderId;
    private final String uri;
    private final String login;
    private final String password;
    private static final String SMS_TYPE = "SMS";
    private final HttpClient httpClient;

    private final int TTL_MIN=10;


    @Override
    public void send(String phoneNumber, String message) throws SmsGatewayException {
        String smsGatewayJson = prepareSMSGatewayDTO(phoneNumber, message);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(smsGatewayJson))
                    .build();
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());
            log.info("Status code : {}", response.statusCode());
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new SmsGatewayException("Bad response from sms gateway");
            }
            log.info("Response body: {}", response.body());
        } catch (IOException | InterruptedException e) {
            throw new SmsGatewayException("Failed to send sms");
        }
    }

    private String prepareSMSGatewayDTO(String phoneNumber, String message) throws SmsGatewayException {
        SMSGatewayDTO smsGatewayDTO = new SMSGatewayDTO(login
                , password
                , phoneNumber
                , new Message(SMS_TYPE,
                new Data(message, senderId, TTL_MIN)));
        ObjectMapper objectMapper = new ObjectMapper();

        String smsGatewayJson;
        try {
            smsGatewayJson = objectMapper.writeValueAsString(smsGatewayDTO);
        } catch (JsonProcessingException e) {
            throw new SmsGatewayException("Can't parse DTO to JSON");
        }
        log.info("smsGatewayJson: {}", smsGatewayJson);
        return smsGatewayJson;
    }

    public SmsServiceImpl(SmsConfiguration config, HttpClient httpClient) {
        senderId = config.getSenderId();
        uri = config.getUri();
        login = config.getLogin();
        password = config.getPassword();
        this.httpClient = httpClient;
    }
}
