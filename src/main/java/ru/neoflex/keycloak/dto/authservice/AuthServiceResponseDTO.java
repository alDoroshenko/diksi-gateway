package ru.neoflex.keycloak.dto.authservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthServiceResponseDTO {
    String username;
    String manzanaId;
    String sessionId;
    boolean pushEnabled;
}
