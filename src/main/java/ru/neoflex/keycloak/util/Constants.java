package ru.neoflex.keycloak.util;


import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public interface SmsAuthConstants {
        String CODE_LENGTH = "length";
        String CODE_TTL = "ttl";
        String SENDER_ID = "senderId";
        String LOGIN = "login";
        String SMS_URI = "smsURI";
        String PASSWORD = "password";
        String SIMULATION_MODE = "simulation";
        String TEXT = "text";
    }

    public interface ManzanaConstants {
        String MANZANA_URI = "manzanaURI";
        String SESSION_ID = "sessionId";
        String PARTNER_ID = "partnerId";
        String VIRTUAL_CARD_TYPE_ID = "virtualCardTypeId";
        String MOBILE_PHONE_PARAM = "mobilePhone";
        String EMAIL_PARAM = "emailAddress";
    }


    public interface RequestConstants {
        String SMS_CODE = "smsCode";
        String USERNAME = "username";
        String MAGNIT_AUTH = "magnitAuth";
    }

    public interface UserAttributes {
        String SMS_CODE = "smsCode";
        String EXPIRY_DATE = "expiryDate";
        String FIRST_NAME = "firstName";
        String LAST_NAME = "lastName";
        String EMAIL = "email";
        String BIRTHDAY = "birthday";
        String MANZANA_ID = "manzanaId";
        String SESSION_ID = "sessionId";

    }

    public interface KeycloakConfiguration {
        String SMS_AUTHENTICATOR_ID = "sms-authenticator";
        String CUSTOM_DIRECT_GRANT_FLOW = "direct grant with sms";
        String DEFAULT_USER_PASSWORD = "1";
    }
}
