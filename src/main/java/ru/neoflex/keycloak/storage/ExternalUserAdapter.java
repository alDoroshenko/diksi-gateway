package ru.neoflex.keycloak.storage;

import jakarta.ws.rs.core.MultivaluedHashMap;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import ru.neoflex.keycloak.jpa.entity.ExteranalUser;
import ru.neoflex.keycloak.jpa.repository.UserJPARepository;
import ru.neoflex.keycloak.util.Constants;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class ExternalUserAdapter extends AbstractUserAdapterFederatedStorage {
    private final @NonNull UserJPARepository repository;
    protected @NonNull ExteranalUser entity;
    protected String keycloakId;

    public ExternalUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model,
                               @NonNull ExteranalUser exteranalUser, @NonNull UserJPARepository repository ) {

        super(session, realm, model);
        this.entity = exteranalUser;
        this.repository = repository;
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
        update(it -> it.setUsername(username));
    }

    @Override
    public void setEmail(String email) {
        update(it -> it.setEmail(email));
    }

    @Override
    public String getEmail() {
        return entity.getEmail();
    }


    @Override
    public void setFirstName(String firstName) {
        update(it -> it.setFirstName(firstName));
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
        update(it -> it.setLastName(lastName));
    }

    public String getBirthDate() {
        return entity.getBirthDate();
    }

    public void setBirthDate(String birthDate) {
        update(it -> it.setBirthDate(birthDate));
    }

    public String getExpiryDate() {
        return entity.getExpiryDate();
    }

    public void setExpiryDate(String expiryDate) {
        update(it -> it.setExpiryDate(expiryDate));
    }

    public String getSmsCode() {
        return entity.getSmsCode();
    }

    public void setSmsCode(String smsCode) {
        update(it -> it.setSmsCode(smsCode));
    }

    public String getSessionId() {
        return entity.getSessionId();
    }

    public void setSessionId(String sessionId) {
        update(it -> it.setSessionId(sessionId));
    }

    public String getManzanaId() {
        return entity.getManzanaId();
    }

    public void setManzanaId(String manzanaId) {
        update(it -> it.setManzanaId(manzanaId));
    }

    public String getRegion() {
        return entity.getRegion();
    }

    public void setRegion(String region) {
        update(it -> it.setRegion(region));
    }

    public String getPushEnable() {
        return String.valueOf(entity.isPushEnable());
    }

    public void setPushEnable(String pushEnable) {
        update(it -> it.setPushEnable(Boolean.parseBoolean(pushEnable)));
    }

    @Override
    public boolean isEnabled() {
        return entity.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        entity.setEnabled(enabled);
        update(it -> it.setEnabled(enabled));
    }

    @Override
    public Long getCreatedTimestamp() {
        return Optional.ofNullable(entity.getCreatedAt())
                .map(Timestamp::getTime)
                .orElse(null);
    }

    @Override
    public void setCreatedTimestamp(Long timestamp) {
        update(it -> it.setCreatedAt(Timestamp.from(Instant.ofEpochMilli(timestamp))));
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        switch (name) {
            case Constants.UserAttributes.LAST_NAME -> setLastName(value);
            case Constants.UserAttributes.FIRST_NAME -> setFirstName(value);
            case Constants.UserAttributes.EMAIL -> setEmail(value);
            case Constants.UserAttributes.BIRTHDAY -> setBirthDate(value);
            case Constants.UserAttributes.SMS_CODE -> setSmsCode((value));
            case Constants.UserAttributes.EXPIRY_DATE -> setExpiryDate(value);
            case Constants.UserAttributes.SESSION_ID -> setSessionId((value));
            case Constants.UserAttributes.MANZANA_ID -> setManzanaId((value));
            case Constants.UserAttributes.REGION -> setRegion(value);
            case Constants.UserAttributes.PUSH_ENABLE -> setPushEnable(value);

            default -> super.setSingleAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        switch (name) {
            case Constants.UserAttributes.LAST_NAME -> setLastName(null);
            case Constants.UserAttributes.FIRST_NAME -> setFirstName(null);
            case Constants.UserAttributes.EMAIL -> setEmail(null);
            case Constants.UserAttributes.BIRTHDAY -> setBirthDate(null);
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
            case Constants.UserAttributes.BIRTHDAY -> setBirthDate(value);
            case Constants.UserAttributes.SMS_CODE -> setSmsCode(value);
            case Constants.UserAttributes.EXPIRY_DATE -> setExpiryDate(value);
            case Constants.UserAttributes.SESSION_ID -> setSessionId(value);
            case Constants.UserAttributes.MANZANA_ID -> setManzanaId(value);
            case Constants.UserAttributes.REGION -> setRegion(value);
            case Constants.UserAttributes.PUSH_ENABLE -> setPushEnable(value);
            default -> super.setAttribute(name, values);
        }
    }

    @Override
    public String getFirstAttribute(String name) {
        return switch (name) {
            case Constants.UserAttributes.LAST_NAME -> getLastName();
            case Constants.UserAttributes.FIRST_NAME -> getFirstName();
            case Constants.UserAttributes.EMAIL -> getEmail();
            case Constants.UserAttributes.BIRTHDAY -> getBirthDate();
            case Constants.UserAttributes.SMS_CODE -> getSmsCode();
            case Constants.UserAttributes.EXPIRY_DATE -> getExpiryDate();
            case Constants.UserAttributes.SESSION_ID -> getSessionId();
            case Constants.UserAttributes.MANZANA_ID -> getManzanaId();
            case Constants.UserAttributes.REGION -> getRegion();
            case Constants.UserAttributes.PUSH_ENABLE -> getPushEnable();

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
        all.add(Constants.UserAttributes.BIRTHDAY, getBirthDate());
        all.add(Constants.UserAttributes.SMS_CODE, getSmsCode());
        all.add(Constants.UserAttributes.EXPIRY_DATE, getExpiryDate());
        all.add(Constants.UserAttributes.SESSION_ID, getSessionId());
        all.add(Constants.UserAttributes.MANZANA_ID, getManzanaId());
        all.add(Constants.UserAttributes.REGION, getRegion());
        all.add(Constants.UserAttributes.PUSH_ENABLE, getPushEnable());
        return all;
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        return switch (name) {
            case UserModel.USERNAME -> Stream.of(getUsername());
            case Constants.UserAttributes.LAST_NAME -> Stream.of(getLastName());
            case Constants.UserAttributes.FIRST_NAME -> Stream.of(getFirstName());
            case Constants.UserAttributes.EMAIL -> Stream.of(getEmail());
            case Constants.UserAttributes.BIRTHDAY -> Stream.of(getBirthDate());
            case Constants.UserAttributes.SMS_CODE -> Stream.of(getSmsCode());
            case Constants.UserAttributes.EXPIRY_DATE -> Stream.of(getExpiryDate());
            case Constants.UserAttributes.SESSION_ID -> Stream.of(getSessionId());
            case Constants.UserAttributes.MANZANA_ID -> Stream.of(getManzanaId());
            case Constants.UserAttributes.REGION -> Stream.of(getRegion());
            case Constants.UserAttributes.PUSH_ENABLE -> Stream.of(getPushEnable());
            default -> super.getAttributeStream(name);
        };
    }

    private void update(Consumer<ExteranalUser> entityUpdater) {
        entityUpdater.accept(entity);
        entity.setUpdatedAt(Timestamp.from(Instant.now()));
        entity = repository.save(entity);
    }
}
