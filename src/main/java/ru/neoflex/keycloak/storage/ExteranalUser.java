package ru.neoflex.keycloak.storage;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;

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
