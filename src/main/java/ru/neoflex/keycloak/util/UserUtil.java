package ru.neoflex.keycloak.util;

import lombok.experimental.UtilityClass;
import org.keycloak.models.UserModel;

import java.util.Map;


@UtilityClass
public class UserUtil {
    public static void saveAttributes(Map<String, String> attributes, UserModel user) {
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            user.setSingleAttribute(entry.getKey(), entry.getValue());
        }
    }

}
