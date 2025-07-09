package ru.neoflex.keycloak.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.jpa.entity.ExteranalUser;

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

}
