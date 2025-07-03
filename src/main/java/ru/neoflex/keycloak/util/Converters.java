package ru.neoflex.keycloak.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.neoflex.keycloak.storage.ExteranalUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@UtilityClass
@Slf4j
public class Converters {
    public static  <T> T getDTOFromResponse(String body, ObjectMapper objectMapper, Class<T> dtoClass)  {
        try {
            return objectMapper.readValue(body, dtoClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert DTO :  " + e.getMessage());
        }
    }

    public static ExteranalUser mapToCustomUser (ResultSet rs) throws SQLException {
        ExteranalUser exteranalUser = new ExteranalUser();
        if ( rs==null ){
            log.info("mapToCustomUser: null result");
            return null;
        }
        exteranalUser.setUsername(rs.getString(Constants.dbColumn.USERMAME));
        exteranalUser.setPassword(rs.getString(Constants.dbColumn.PASSWORD));
        exteranalUser.setEmail(rs.getString(Constants.dbColumn.EMAIL));
        exteranalUser.setBirthDate(rs.getString(Constants.dbColumn.BIRTHDAY));
        exteranalUser.setFirstName(rs.getString(Constants.dbColumn.FIRST_NAME));
        exteranalUser.setLastName(rs.getString(Constants.dbColumn.LAST_NAME));
        exteranalUser.setSmsCode(rs.getString(Constants.dbColumn.SMS_CODE));
        exteranalUser.setExpiryDate(rs.getString(Constants.dbColumn.EXPIRY_DATE));
        exteranalUser.setSessionId(rs.getString(Constants.dbColumn.SESSION_ID));
        exteranalUser.setManzanaId(rs.getString(Constants.dbColumn.MANZANA_ID));
        exteranalUser.setEnabled(rs.getBoolean(Constants.dbColumn.ENABLED));
        return exteranalUser;
    }

}
