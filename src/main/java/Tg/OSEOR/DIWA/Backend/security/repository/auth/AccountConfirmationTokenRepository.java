package Tg.OSEOR.DIWA.Backend.security.repository.auth;

import Tg.OSEOR.DIWA.Backend.security.model.auth.AccountConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AccountConfirmationTokenRepository extends JpaRepository<AccountConfirmationToken, Long> {

    Optional<AccountConfirmationToken> findByToken(String token);

    @Query("SELECT t FROM AccountConfirmationToken t WHERE t.token = :token AND t.confirmed = false AND t.expiresAt > :now")
    Optional<AccountConfirmationToken> findValidToken(
        @Param("token") String token,
        @Param("now") LocalDateTime now
    );

    Optional<AccountConfirmationToken> findByUserId(Long userId);
}
