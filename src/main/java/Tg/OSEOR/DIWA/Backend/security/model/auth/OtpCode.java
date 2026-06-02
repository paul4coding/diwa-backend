package Tg.OSEOR.DIWA.Backend.security.model.auth;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_codes", indexes = {
    @Index(name = "idx_otp_user_id", columnList = "user_id"),
    @Index(name = "idx_otp_code", columnList = "code")
})
public class OtpCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 10)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean used = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OtpType type;

    public enum OtpType {
        LOGIN_2FA,
        PASSWORD_RESET
    }

    public OtpCode() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Boolean getUsed() { return used; }
    public void setUsed(Boolean used) { this.used = used; }

    public OtpType getType() { return type; }
    public void setType(OtpType type) { this.type = type; }
}
