package ru.neoflex.keycloak.model;


import lombok.*;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.util.Constants;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManzanaUser {
    String mobilePhone;
    String email;
    String firstName;
    String lastName;
    String middleName;
    String birthDate;
    int genderCode; //0-пустое, 1-М, 2-Ж
    boolean allowSms;
    String id;
    String sessionId;
    String region;

}



