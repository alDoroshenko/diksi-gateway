package ru.neoflex.keycloak.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExteranalUser {

    private String id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate bithDate;

}
