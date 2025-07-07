package ru.neoflex.keycloak.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ExteranalUser {

    private String username;
    private String password;
    private String email;
    private String birthDate;
    private String firstName;
    private String lastName;
    private String smsCode;
    private String expiryDate;
    private String sessionId;
    private String manzanaId;
    private boolean enabled;

}
