package ru.neoflex.keycloak.dto.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Data {
    String text;
    String serviceNumber;
    int ttl;
}