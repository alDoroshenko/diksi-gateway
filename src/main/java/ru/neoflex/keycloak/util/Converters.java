package ru.neoflex.keycloak.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import ru.neoflex.keycloak.user.ExteranalUser;

import java.sql.ResultSet;
import java.sql.SQLException;

@UtilityClass
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
            return null;
        }
        exteranalUser.setId(rs.getString("id"));
        exteranalUser.setUsername(rs.getString("username"));
        exteranalUser.setFirstName(rs.getString("firstName"));
        exteranalUser.setLastName(rs.getString("lastName"));
        exteranalUser.setEmail(rs.getString("email"));
        exteranalUser.setBithDate(rs.getDate("birthDate").toLocalDate());
        return exteranalUser;
    }

    /*public static List<CustomUser> mapToUserEntityList(List<UserResponseDto> dtoList) {
        return dtoList.stream().map(UserMapper::mapToUserEntity).collect(Collectors.toUnmodifiableList());
    }*/

}
