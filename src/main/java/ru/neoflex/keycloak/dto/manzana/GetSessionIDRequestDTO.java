package ru.neoflex.keycloak.dto.manzana;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class GetSessionIDRequestDTO {
    String phoneOrEmail;
    String password;
    UUID partnerId;
}
