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
public class ManzanaRegisterResponseDTO {
    UUID id;
    String lastName;
    String login;
    String firstName;
    String middleName;
    String fullName;
    int genderCode;
    LocalDate birthDate;
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
    LocalDate registrationDate;
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
    UUID defaultCardId;
    String defaultCardNumber;
    UUID stateId;
    String stateName;
    UUID cityId;
    String cityName;
    String street;
    BigDecimal building;
    String requestedForUpdateEmailAddress;
    boolean agreeToTerms;
    UUID levelId;
    String levelName;
    String levelExternalId;
    UUID citizenship;
    String passportSeries;
    String passportNumber;
    String birthPlace;
    int documentType;
    String passportUnitCode;
    String passportIssuedBy;
    LocalDate passportIssuedOn;
    int isAddress2EqualsAddress1;
    int address1PostalCode;
    UUID address1Country;
    UUID address1Region;
    UUID address1City;
    String address1Line1;
    String address1Line2;
    String address1Line3;
    String address1Flat;
    String address2PostalCode;
    UUID address2Country;
    UUID address2Region;
    UUID address2City;
    String address2Line1;
    String address2Line2;
    String address2Line3;
    String address2Flat;
    String webSiteUrl;
    int educationCode;
    String workPlace;
    int criminalLiability;
    String criminalArticle;
    int englishLanguage;
    int frenchLanguage;
    int spanishLanguage;
    String otherLanguage;
    int leadSource;
    String snils;
    UUID accountId;
    int validity;
    String externalId;
    String emailAddress2;
    String flat;
    String floor;
    int source;
    int contactType;
    BigDecimal statusBalance;
    BigDecimal statusActiveBalance;
    BigDecimal statusDebet;
    BigDecimal statusCredit;
    boolean allowReceiveChequeByEmail;
    int limitPCVElected;
    int amountPCVElected;
    String virtualCardNumber;
    boolean isWallet;
    LocalDate walletBindDate;
    LocalDate walletUnbindDate;
    String nfc;
    String imageUrl;
    int stateCode;
    int statusCode;
    int activeCardQuantity;
    String countryName;
    LocalDate modifiedOn;
    int participantStatus;
    String virtualCardId;


}
