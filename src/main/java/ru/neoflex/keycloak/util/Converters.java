package ru.neoflex.keycloak.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.model.ExteranalUser;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    public static ExteranalUser mapToExternalUser(ResultSet rs) throws SQLException {
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

    public static ExteranalUser mapToExternalUser(UserModel userModel)  {
        ExteranalUser exteranalUser = new ExteranalUser();
        exteranalUser.setUsername(userModel.getUsername());
        exteranalUser.setEmail(userModel.getFirstAttribute(Constants.UserAttributes.EMAIL));
        exteranalUser.setBirthDate(userModel.getFirstAttribute(Constants.UserAttributes.BIRTHDAY));
        exteranalUser.setFirstName(userModel.getFirstAttribute(Constants.UserAttributes.FIRST_NAME));
        exteranalUser.setLastName(userModel.getFirstAttribute(Constants.UserAttributes.LAST_NAME));
        exteranalUser.setSmsCode(userModel.getFirstAttribute(Constants.UserAttributes.SMS_CODE));
        exteranalUser.setExpiryDate(userModel.getFirstAttribute(Constants.UserAttributes.EXPIRY_DATE));
        exteranalUser.setSessionId(userModel.getFirstAttribute(Constants.UserAttributes.SESSION_ID));
        exteranalUser.setManzanaId(userModel.getFirstAttribute(Constants.UserAttributes.MANZANA_ID));
        exteranalUser.setEnabled(userModel.isEnabled());
        return exteranalUser;
    }

}
