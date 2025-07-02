package ru.neoflex.keycloak.gateway.manzana;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import ru.neoflex.keycloak.ManzanaConfiguration;
import ru.neoflex.keycloak.dto.manzana.GetContactRequestDTO;
import ru.neoflex.keycloak.dto.manzana.GetContactResponseDTO;
import ru.neoflex.keycloak.exceptions.ManzanaGatewayException;
import ru.neoflex.keycloak.model.ManzanaUser;
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


    @Override
    public ManzanaUser getUser(ManzanaUser user) throws ManzanaGatewayException {
        ObjectMapper objectMapper = new ObjectMapper();
        // String getManzanaUserJson = prepareGetManzanaUserJson(user, objectMapper);
        // String fullURI = getFullURIforGetUser (user);

        try {
            URI fullUri = getURIForGetUser(user);
            log.info(fullUri.toString());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(getURIForGetUser(user))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());
            log.info("Status code: {}", response.statusCode());
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new ManzanaGatewayException("Bad response from manzana gateway");
            }
            log.info("Response body: {}", response.body());
            GetContactResponseDTO contactResponseDTO = Converters.getDTOFromResponse(response.body(), objectMapper, GetContactResponseDTO.class);
            ManzanaUser manzanaUser = new ManzanaUser(contactResponseDTO.getMobilePhone()
                    , contactResponseDTO.getEmailAddress()
                    , contactResponseDTO.getFirstName()
                    , contactResponseDTO.getLastName()
                    , contactResponseDTO.getMiddleName()
                    , contactResponseDTO.getBirthDate()
                    //  ,null
                    , contactResponseDTO.getGenderCode()
                    , contactResponseDTO.isAllowSms()
                    , contactResponseDTO.getId().toString()
            );
            return manzanaUser;
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new ManzanaGatewayException("Failed to get user from manzana");
        }
    }

    @Override
    public ManzanaUser register(ManzanaUser user) {
        return null;
    }

    public ManzanaServiceImpl(ManzanaConfiguration config, HttpClient httpClient) {
        partnerId = config.getPartnerId();
        sessionId = config.getSessionId();
        virtualCardTypeId = config.getVirtualCardTypeId();
        uri = config.getUri();
        this.httpClient = httpClient;
    }

    private URI getURIForGetUser(ManzanaUser user) throws URISyntaxException {
        /*String fullUrl = String.format(
                "%s?sessionId=%s&mobilePhone=%s&emailAddress=%s",
                uri+GET_USER_ENDPOINT,
                URLEncoder.encode(sessionId.toString(), StandardCharsets.UTF_8),
                URLEncoder.encode(user.getMobilePhone(), StandardCharsets.UTF_8),
                URLEncoder.encode("", StandardCharsets.UTF_8)
        );
        return fullUrl;*/
        return new URIBuilder(uri + GET_USER_ENDPOINT)
                .addParameter("sessionid", sessionId.toString())
                .addParameter("mobilePhone", user.getMobilePhone())
                .addParameter("emailAddress", "")
                .build();
    }


    private String prepareGetManzanaUserJson(ManzanaUser user, ObjectMapper objectMapper) throws ManzanaGatewayException {
        GetContactRequestDTO getContactRequestDTO = new GetContactRequestDTO(sessionId,
                user.getMobilePhone(),
                user.getEmail());
        String manzanaUserJson;
        try {
            manzanaUserJson = objectMapper.writeValueAsString(getContactRequestDTO);

        } catch (JsonProcessingException e) {
            throw new ManzanaGatewayException("Can't parse DTO to JSON");
        }
        log.info("ManzanaUserJson: {}", manzanaUserJson);
        return manzanaUserJson;
    }

}
