package ru.neoflex.keycloak.dto.manzana;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ManzanaRegisterRequestDTO {
    UUID sessionId;
    UUID partnerId;
    UUID virtualCardTypeId;
    String mobilePhone;
    String emailAddress;
    String firstName;
    String lastName;
    String middleName;
    String password;
    String birthDate;
    int genderCode;
    boolean allowNotification;
    boolean allowEmail;
    boolean allowSms;
    boolean agreeToTerms;
    int communicationMethod;
    int contactType;
    UUID address1Region;
    String referralCode;
    int source;
    int subjectId;
}
