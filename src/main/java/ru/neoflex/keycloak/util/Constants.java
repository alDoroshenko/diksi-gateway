package ru.neoflex.keycloak.util;


import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public interface SmsConstants{
         String CODE = "code";
         String CODE_LENGTH = "length";
         String CODE_TTL = "ttl";
         String SENDER_ID = "senderId";
         String SIMULATION_MODE = "simulation";
    }

    public interface RequestConstants{
        String SMS_CODE = "smsCode";
        String USERNAME = "username";
        String PASSWORD = "password";
    }

    public interface UserAttributes{
        String SMS_CODE = "smsCode";
        String EXPIRY_DATE = "expiryDate";
    }


}
