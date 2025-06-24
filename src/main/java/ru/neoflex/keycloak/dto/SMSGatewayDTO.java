package ru.neoflex.keycloak.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class SMSGatewayDTO {
    String login;
    String password;
    String destAddr;
    Message message;
}

