package art.mehdiragani.mehdiragani.auth.models;

import java.time.OffsetDateTime;
import java.util.UUID;

import art.mehdiragani.mehdiragani.auth.models.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false, unique = true, length = 100)
    @Email
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false)
    private UserRole role;

    // Profile fields:
    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;



    // More info for admin
    @Column(name = "date_created", nullable = false, updatable = false)
    private OffsetDateTime dateCreated = OffsetDateTime.now();

    @Column(name = "last_modified")
    private OffsetDateTime lastModified = OffsetDateTime.now();

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;



    // Password reset related
    @Column(name = "password_reset_token", length = 100)
    private String passwordResetToken;

    @Column(name = "password_reset_expiry")
    private OffsetDateTime passwordResetExpiry;



    // Spring Security Related
    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false)
    private boolean credentialsNonExpired = true;

    @Column(name = "account_non_expired", nullable = false)
    private boolean accountNonExpired = true;
}
