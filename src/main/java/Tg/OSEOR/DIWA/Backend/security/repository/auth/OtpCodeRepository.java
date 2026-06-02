package Tg.OSEOR.DIWA.Backend.security.repository.auth;

import Tg.OSEOR.DIWA.Backend.security.model.auth.OtpCode;
import Tg.OSEOR.DIWA.Backend.security.model.auth.OtpCode.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    @Query("SELECT o FROM OtpCode o WHERE o.user.id = :userId AND o.code = :code AND o.type = :type AND o.used = false AND o.expiresAt > :now")
    Optional<OtpCode> findValidOtp(
        @Param("userId") Long userId,
        @Param("code") String code,
        @Param("type") OtpType type,
        @Param("now") LocalDateTime now
    );

    @Modifying
    @Query("UPDATE OtpCode o SET o.used = true WHERE o.user.id = :userId AND o.type = :type")
    void invalidateAllForUser(@Param("userId") Long userId, @Param("type") OtpType type);

    @Modifying
    @Query("DELETE FROM OtpCode o WHERE o.expiresAt < :now OR o.used = true")
    void deleteExpiredOtps(@Param("now") LocalDateTime now);
}
