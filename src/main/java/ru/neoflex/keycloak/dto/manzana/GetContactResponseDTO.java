package ru.neoflex.keycloak.dto.manzana;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class GetContactResponseDTO {
    UUID id;
    String lastName;
    String login;
    String firstName;
    String middleName;
    String fullName;
    int genderCode;
    String birthDate;
    int familyStatusCode;
    int hasChildrenCode;
    String emailAddress;
    String mobilePhone;
    boolean allowEmail;
    boolean allowSms;
    boolean allowPhone;
    BigDecimal balance;
    BigDecimal activeBalance;
    BigDecimal debet;
    BigDecimal credit;
    BigDecimal summ;
    BigDecimal summDiscounted;
    BigDecimal discountSumm;
    int quantity;
    String registrationDate;
    UUID partnerId;
    String partnerName;
    UUID orgUnitId;
    String orgUnitName;
    UUID preferredOrgUnitId;
    String preferredOrgUnitName;
    boolean mobilePhoneVerified;
    boolean emailVerified;
    int communicationMethod;
    boolean allowNotification;
    int contactType;
    BigDecimal statusBalance;
    BigDecimal statusActiveBalance;
    BigDecimal statusDebet;
    BigDecimal statusCredit;
    int activeCardQuantity;
    byte activated;

}
