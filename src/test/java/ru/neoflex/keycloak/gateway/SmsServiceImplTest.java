/*
package ru.neoflex.keycloak.gateway;

import org.junit.jupiter.api.Test;
import org.keycloak.models.AuthenticatorConfigModel;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.neoflex.keycloak.SmsConfiguration;
import ru.neoflex.keycloak.exceptions.SmsGatewayException;
import ru.neoflex.keycloak.gateway.sms.SmsServiceImpl;
import ru.neoflex.keycloak.util.Constants;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static io.smallrye.common.constraint.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SmsServiceImplTest {
    private final Map<String, String> config = Map.of(
            Constants.SmsAuthConstants.SENDER_ID, "TEST_SENDER",
            Constants.SmsAuthConstants.SMS_URI, "http://test-sms-gateway.com",
            Constants.SmsAuthConstants.LOGIN, "test_login",
            Constants.SmsAuthConstants.PASSWORD, "test_password"
    );
    private final AuthenticatorConfigModel configModel = Mockito.mock(AuthenticatorConfigModel.class);
    private final HttpClient mockHttpClient = mock(HttpClient.class);
    private final SmsServiceImpl smsService = new SmsServiceImpl(new SmsConfiguration(configModel), mockHttpClient);
    private final HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
    private static final String PHONE_NUMBER = "+7123456789";
    private static final String TEXT_MESSAGE = "Test message";

    @Test
    void successfullySendSmsWhenGatewayReturns200() throws Exception {

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        assertDoesNotThrow(() -> smsService.send(PHONE_NUMBER, TEXT_MESSAGE));

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any());

        HttpRequest sentRequest = requestCaptor.getValue();
        assertEquals("POST", sentRequest.method());
        assertEquals("http://test-sms-gateway.com", sentRequest.uri().toString());
        assertTrue(sentRequest.headers().map().containsKey("Content-Type"));
    }

    @Test
    void checkThrowSmsGatewayExceptionWhenResponseNon200() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(404);
        assertThrows(SmsGatewayException.class,
                () -> smsService.send(PHONE_NUMBER, TEXT_MESSAGE));
    }

}
*/
