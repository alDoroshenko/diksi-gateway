package ru.neoflex.keycloak.dto.manzana;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@NoArgsConstructor
public class GetSessionIDResponseDTO {
    UUID sessionId;
    UUID id;
}
