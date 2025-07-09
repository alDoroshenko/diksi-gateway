package ru.neoflex.keycloak.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@Table(name ="users")
public class ExteranalUser implements JpaEntity<String>{
    @Id
    @Column(name="username")
    private String username;

    @Column(name="password",nullable = false)
    private String password;

    @Column(name="email")
    private String email;

    @Column(name="birthday")
    private String birthDate;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "sms_code")
    private String smsCode;

    @Column(name = "expiry_date")
    private String expiryDate;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "manzana_id")
    private String manzanaId;

    @Column(name = "enabled")
    private boolean enabled;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @Override
    public String getId() {
        return username;
    }

    @Override
    public boolean isNew() {
        return createdAt == null || updatedAt == null;
    }


}
