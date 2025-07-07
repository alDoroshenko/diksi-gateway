package ru.neoflex.keycloak.storage;

import jakarta.ws.rs.core.MultivaluedHashMap;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import ru.neoflex.keycloak.model.ExteranalUser;
import ru.neoflex.keycloak.util.Constants;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class ExternalUserAdapter extends AbstractUserAdapterFederatedStorage {

    protected ExteranalUser entity;
    protected String keycloakId;

    public ExternalUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, ExteranalUser exteranalUser) {

        super(session, realm, model);
        this.entity = exteranalUser;
        keycloakId = StorageId.keycloakId(model, entity.getUsername());
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
    public boolean isEnabled() {
        return entity.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        entity.setEnabled(enabled);
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        switch (name) {
            case Constants.UserAttributes.LAST_NAME -> setLastName(value);
            case Constants.UserAttributes.FIRST_NAME -> setFirstName(value);
            case Constants.UserAttributes.EMAIL -> setEmail(value);
            case Constants.UserAttributes.BIRTHDAY -> entity.setBirthDate(value);
            case Constants.UserAttributes.SMS_CODE -> entity.setSmsCode((value));
            case Constants.UserAttributes.EXPIRY_DATE -> entity.setExpiryDate(value);
            case Constants.UserAttributes.SESSION_ID -> entity.setSessionId((value));
            case Constants.UserAttributes.MANZANA_ID -> entity.setManzanaId((value));
            default -> super.setSingleAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        switch (name) {
            case Constants.UserAttributes.LAST_NAME -> setLastName(null);
            case Constants.UserAttributes.FIRST_NAME -> setFirstName(null);
            case Constants.UserAttributes.EMAIL -> setEmail(null);
            case Constants.UserAttributes.BIRTHDAY -> entity.setBirthDate(null);
            case Constants.UserAttributes.SMS_CODE -> entity.setSmsCode(null);
            case Constants.UserAttributes.EXPIRY_DATE -> entity.setExpiryDate(null);
            case Constants.UserAttributes.SESSION_ID -> entity.setSessionId(null);
            case Constants.UserAttributes.MANZANA_ID -> entity.setManzanaId(null);
            default -> super.removeAttribute(name);
        }
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        String value = values != null && !values.isEmpty() ? values.get(0) : null;
        switch (name) {
            case Constants.UserAttributes.LAST_NAME -> setLastName(value);
            case Constants.UserAttributes.FIRST_NAME -> setFirstName(value);
            case Constants.UserAttributes.EMAIL -> setEmail(value);
            case Constants.UserAttributes.BIRTHDAY -> entity.setBirthDate(value);
            case Constants.UserAttributes.SMS_CODE -> entity.setSmsCode(value);
            case Constants.UserAttributes.EXPIRY_DATE -> entity.setExpiryDate(value);
            case Constants.UserAttributes.SESSION_ID -> entity.setSessionId(value);
            case Constants.UserAttributes.MANZANA_ID -> entity.setManzanaId(value);
            default -> super.setAttribute(name, values);
        }
    }

    @Override
    public String getFirstAttribute(String name) {
        return switch (name) {
            case Constants.UserAttributes.LAST_NAME -> getLastName();
            case Constants.UserAttributes.FIRST_NAME -> getFirstName();
            case Constants.UserAttributes.EMAIL -> getEmail();
            case Constants.UserAttributes.BIRTHDAY -> entity.getBirthDate();
            case Constants.UserAttributes.SMS_CODE -> entity.getSmsCode();
            case Constants.UserAttributes.EXPIRY_DATE -> entity.getExpiryDate();
            case Constants.UserAttributes.SESSION_ID -> entity.getSessionId();
            case Constants.UserAttributes.MANZANA_ID -> entity.getManzanaId();
            default -> super.getFirstAttribute(name);
        };
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attrs = super.getAttributes();
        MultivaluedHashMap<String, String> all = new MultivaluedHashMap<>();
        all.putAll(attrs);
        all.add(Constants.UserAttributes.LAST_NAME, getLastName());
        all.add(Constants.UserAttributes.FIRST_NAME, getFirstName());
        all.add(Constants.UserAttributes.EMAIL, getEmail());
        all.add(Constants.UserAttributes.BIRTHDAY, entity.getBirthDate());
        all.add(Constants.UserAttributes.SMS_CODE, entity.getSmsCode());
        all.add(Constants.UserAttributes.EXPIRY_DATE, entity.getExpiryDate());
        all.add(Constants.UserAttributes.SESSION_ID, entity.getSessionId());
        all.add(Constants.UserAttributes.MANZANA_ID, entity.getManzanaId());
        return all;
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        return switch (name) {
            case UserModel.USERNAME -> Stream.of(getUsername());
            case Constants.UserAttributes.LAST_NAME -> Stream.of(getLastName());
            case Constants.UserAttributes.FIRST_NAME -> Stream.of(getFirstName());
            case Constants.UserAttributes.EMAIL -> Stream.of(getEmail());
            case Constants.UserAttributes.BIRTHDAY -> Stream.of(entity.getBirthDate());
            case Constants.UserAttributes.SMS_CODE -> Stream.of(entity.getSmsCode());
            case Constants.UserAttributes.EXPIRY_DATE -> Stream.of(entity.getExpiryDate());
            case Constants.UserAttributes.SESSION_ID -> Stream.of(entity.getSessionId());
            case Constants.UserAttributes.MANZANA_ID -> Stream.of(entity.getManzanaId());
            default -> super.getAttributeStream(name);
        };
    }
}
