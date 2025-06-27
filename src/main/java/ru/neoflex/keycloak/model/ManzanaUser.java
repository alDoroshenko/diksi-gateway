package ru.neoflex.keycloak.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.keycloak.models.UserModel;

@Getter
@Setter
@ToString
public class ManzanaUser {
    String mobilePhone;
    String email;
    String firstName;
    String lastName;
    String middleName;
    String birthDate;
    int genderCode; //0-пустое, 1-М, 2-Ж
    boolean allowSms;

    public ManzanaUser(UserModel userModel){
        this.mobilePhone = userModel.getUsername();
        this.email = userModel.getEmail();
        this.firstName = userModel.getFirstName();
        this.lastName = userModel.getLastName();
    }
}



