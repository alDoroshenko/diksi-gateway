package ru.neoflex.keycloak.dto.manzana;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class GetContactRequestDTO {
    UUID sessionId;
    String mobilePhone;
    String emailAddress;
    int take;
    int skip;
}
