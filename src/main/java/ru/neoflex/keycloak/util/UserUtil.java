package ru.neoflex.keycloak.util;

import lombok.experimental.UtilityClass;
import org.keycloak.models.UserModel;

import java.util.Map;


@UtilityClass
public class UserUtil {
    private final static String MASK_SYMBOL = "*";
    public static void saveAttributes(Map<String, String> attributes, UserModel user) {
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            user.setSingleAttribute(entry.getKey(), entry.getValue());
        }
    }

    public static String maskString(String inputString) {
        int roundUp = (int) Math.round(inputString.length() / 2.0);
        return MASK_SYMBOL.repeat(roundUp) + inputString.substring(roundUp);
    }
}
