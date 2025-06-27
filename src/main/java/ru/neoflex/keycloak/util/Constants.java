package ru.neoflex.keycloak.util;


import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public interface SmsAuthConstants {
         String CODE = "code";
         String CODE_LENGTH = "length";
         String CODE_TTL = "ttl";
         String SENDER_ID = "senderId";
         String LOGIN = "login";
         String SMS_URI = "smsURI";
         String PASSWORD = "password";
         String SIMULATION_MODE = "simulation";
         String TEXT ="text";
    }

    public interface RequestConstants{
        String SMS_CODE = "smsCode";
        String USERNAME = "username";
        String PASSWORD = "password";
    }

    public interface UserAttributes{
        String SMS_CODE = "smsCode";
        String EXPIRY_DATE = "expiryDate";
        String FIRST_NAME = "firstName";
        String LAST_NAME = "lastName";
    }

    public interface KeycloakConfiguration{
        String SMS_AUTHENTICATOR_ID = "sms-authenticator";
        String CUSTOM_DIRECT_GRANT_FLOW = "direct grant with sms";
    }


}
