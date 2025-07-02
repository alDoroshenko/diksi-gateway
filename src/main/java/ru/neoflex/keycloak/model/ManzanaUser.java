package ru.neoflex.keycloak.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.util.Constants;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ManzanaUser {
    String mobilePhone;
    String email;
    String firstName;
    String lastName;
    String middleName;
    String birthDate;
   // String region;
    int genderCode; //0-пустое, 1-М, 2-Ж
    boolean allowSms;
    String id;

    public ManzanaUser(UserModel userModel) {
        this.mobilePhone = userModel.getUsername();
        this.email = userModel.getEmail();
        this.firstName = userModel.getFirstName();
        this.lastName = userModel.getLastName();
        this.birthDate = userModel.getFirstAttribute(Constants.UserAttributes.BIRTHDAY);
       // this.region = userModel.getFirstAttribute(Constants.UserAttributes.REGION);
    }
}



