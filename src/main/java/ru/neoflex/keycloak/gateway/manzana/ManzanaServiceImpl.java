package ru.neoflex.keycloak.gateway.manzana;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.ManzanaConfiguration;
import ru.neoflex.keycloak.dto.manzana.*;
import ru.neoflex.keycloak.exception.ManzanaGatewayException;
import ru.neoflex.keycloak.model.ManzanaUser;
import ru.neoflex.keycloak.util.Constants;
import ru.neoflex.keycloak.util.Converters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Slf4j
public class ManzanaServiceImpl implements ManzanaService {
    private final String uri;
    private final UUID partnerId;
    private final UUID sessionId;
    private final UUID virtualCardTypeId;
    private final HttpClient httpClient;

    private static final String GET_USER_ENDPOINT = "/Contact/FilterByPhoneAndEmail";
    private static final String GET_SESSION_ID_ENDPOINT = "/Identity/AdvancedPhoneEmailLogin";
    private static final String REGISTER_ENDPOINT = "/Contact/RegisterWithoutConfirmation";


    @Override
    public ManzanaUser getUser(String mobilePhone) throws ManzanaGatewayException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(getURIForGetUser(mobilePhone))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());
            log.info("Status code for getUser: {}", response.statusCode());
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new ManzanaGatewayException("Bad response from manzana gateway");
            }
            log.info("Response body: {}", response.body());
            GetContactResponseDTO contactResponseDTO = Converters.getDTOFromResponse(
                    response.body(),
                    objectMapper,
                    GetContactResponseDTO.class);

            return ManzanaUser.builder()
                    .email(contactResponseDTO.getEmailAddress())
                    .firstName(contactResponseDTO.getFirstName())
                    .lastName(contactResponseDTO.getLastName())
                    .middleName(contactResponseDTO.getMiddleName())
                    .birthDate(contactResponseDTO.getBirthDate())
                    .id(contactResponseDTO.getId().toString())
                    .build();
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new ManzanaGatewayException("Failed to get user from manzana");
        }
    }

    @Override
    public String register(UserModel user) throws ManzanaGatewayException {
        ObjectMapper objectMapper = new ObjectMapper();
        String manzanaRegisterJson = prepareRegistrationJson(user, objectMapper);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri + REGISTER_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(manzanaRegisterJson))
                    .build();
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());
            log.info("Status code for register: {}", response.statusCode());
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new ManzanaGatewayException("Bad response from manzana gateway");
            }
            log.info("Response body: {}", response.body());

            ManzanaRegisterResponseDTO manzanaRegisterResponseDTO = Converters.getDTOFromResponse(
                    response.body(),
                    objectMapper,
                    ManzanaRegisterResponseDTO.class);
            return manzanaRegisterResponseDTO.getId().toString();
        } catch (IOException | InterruptedException e) {
            throw new ManzanaGatewayException("Failed to register user in manzana");
        }
    }

    @Override
    public String getSessionId(String mobilePhone) throws ManzanaGatewayException {
        ObjectMapper objectMapper = new ObjectMapper();
        String getSessionIdJson = prepareGetSessionIdJson(mobilePhone, objectMapper);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri + GET_SESSION_ID_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(getSessionIdJson))
                    .build();
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());
            log.info("Status code for getSessionId: {}", response.statusCode());
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new ManzanaGatewayException("Bad response from manzana gateway");
            }
            log.info("Response body: {}", response.body());

            GetSessionIDResponseDTO sessionIDResponseDTO = Converters.getDTOFromResponse(
                    response.body(),
                    objectMapper,
                    GetSessionIDResponseDTO.class);
            return sessionIDResponseDTO.getSessionId().toString();
        } catch (IOException | InterruptedException e) {
            throw new ManzanaGatewayException("Failed to get session id from manzana");
        }
    }

    public ManzanaServiceImpl(ManzanaConfiguration config, HttpClient httpClient) {
        partnerId = config.getPartnerId();
        sessionId = config.getSessionId();
        virtualCardTypeId = config.getVirtualCardTypeId();
        uri = config.getUri();
        this.httpClient = httpClient;
    }

    private URI getURIForGetUser(String mobilePhone) throws URISyntaxException {
        return new URIBuilder(uri + GET_USER_ENDPOINT)
                .addParameter(Constants.ManzanaConstants.SESSION_ID.toLowerCase(), sessionId.toString())
                .addParameter(Constants.ManzanaConstants.MOBILE_PHONE_PARAM, mobilePhone)
                .addParameter(Constants.ManzanaConstants.EMAIL_PARAM, "")
                .build();
    }


    private String prepareGetSessionIdJson(String pnoneNumber, ObjectMapper objectMapper) throws ManzanaGatewayException {
        GetSessionIDRequestDTO sessionIDRequestDTO = GetSessionIDRequestDTO.builder()
                .phoneOrEmail(pnoneNumber)
                .password("")
                .partnerId(partnerId)
                .build();
        String sessionIdJson;
        try {
            sessionIdJson = objectMapper.writeValueAsString(sessionIDRequestDTO);
        } catch (JsonProcessingException e) {
            throw new ManzanaGatewayException("Can't parse DTO to JSON");
        }
        log.info("sessionIdJson: {}", sessionIdJson);
        return sessionIdJson;
    }

    private String prepareRegistrationJson(UserModel user, ObjectMapper objectMapper) throws ManzanaGatewayException {
        ManzanaRegisterRequestDTO manzanaRegisterDTO = ManzanaRegisterRequestDTO.builder()
                .sessionId(sessionId)
                .partnerId(partnerId)
                .virtualCardTypeId(virtualCardTypeId)
                .mobilePhone(user.getUsername())
                .emailAddress(user.getFirstAttribute(Constants.UserAttributes.EMAIL))
                .firstName(user.getFirstAttribute(Constants.UserAttributes.FIRST_NAME))
                .lastName(user.getFirstAttribute(Constants.UserAttributes.LAST_NAME))
                .password("")
                .birthDate(user.getFirstAttribute(Constants.UserAttributes.BIRTHDAY))
                .genderCode(0)
                .allowNotification(true)
                .allowEmail(true)
                .allowSms(true)
                .agreeToTerms(true)
                .communicationMethod(1)
                .address1Region(user.getFirstAttribute(Constants.UserAttributes.REGION) != null ?
                        (UUID.fromString(user.getFirstAttribute(Constants.UserAttributes.REGION))) : null)
                .referralCode(null)
                .source(9)
                .subjectId(1)
                .build();
        String manzanaRegisterJson;
        try {
            manzanaRegisterJson = objectMapper.writeValueAsString(manzanaRegisterDTO);
        } catch (JsonProcessingException e) {
            throw new ManzanaGatewayException("Can't parse DTO to JSON");
        }
        log.info(" manzanaRegisterJson: {}", manzanaRegisterJson);
        return manzanaRegisterJson;
    }

}
