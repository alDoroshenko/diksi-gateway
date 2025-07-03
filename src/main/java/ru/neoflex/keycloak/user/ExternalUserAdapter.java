package ru.neoflex.keycloak.user;

import jakarta.ws.rs.core.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import ru.neoflex.keycloak.util.Constants;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class ExternalUserAdapter extends AbstractUserAdapterFederatedStorage {

    protected ExteranalUser entity;
    protected String keycloakId;
    public ExternalUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, ExteranalUser exteranalUser) {
        super(session, realm, model);
        this.entity = exteranalUser;
        keycloakId = StorageId.keycloakId(model, entity.getId());
    }

    @Override
    public String getId() {
        return keycloakId;
    }
    @Override
    public String getUsername() {
        return entity.getUsername();
    }

    @Override
    public void setUsername(String username) {
        entity.setUsername(username);

    }

    @Override
    public void setEmail(String email) {
        entity.setEmail(email);
    }

    @Override
    public String getEmail() {
        return entity.getEmail();
    }

  /*  @Override
    public String getId() {
        return keycloakId;
    }*/

    @Override
    public void setFirstName(String firstName) {
        entity.setFirstName(firstName);
    }

    @Override
    public String getFirstName() {
        return entity.getFirstName();
    }

    @Override
    public String getLastName() {
        return entity.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        entity.setLastName(lastName);
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        if (name.equals(Constants.UserAttributes.BIRTHDAY)) {
            entity.setBithDate(LocalDate.parse(value));
        } else {
            super.setSingleAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        if (name.equals(Constants.UserAttributes.BIRTHDAY)) {
            entity.setBithDate(null);
        } else {
            super.removeAttribute(name);
        }
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        if (name.equals(Constants.UserAttributes.BIRTHDAY)) {
            entity.setBithDate(LocalDate.parse(values.get(0)));
        } else {
            super.setAttribute(name, values);
        }
    }

    @Override
    public String getFirstAttribute(String name) {
        if (name.equals(Constants.UserAttributes.BIRTHDAY)) {
            return entity.getBithDate().toString();
        } else {
            return super.getFirstAttribute(name);
        }
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attrs = super.getAttributes();
        MultivaluedHashMap<String, String> all = new MultivaluedHashMap<>();
        all.putAll(attrs);
        all.add(Constants.UserAttributes.BIRTHDAY, entity.getBithDate().toString());
        return all;
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        if (name.equals(Constants.UserAttributes.BIRTHDAY)) {
            List<String> birthDay = new LinkedList<>();
            birthDay.add(entity.getBithDate().toString());
            return birthDay.stream();
        } else {
            return super.getAttributeStream(name);
        }
    }



}
