package Tg.OSEOR.DIWA.Backend.repository;

import Tg.OSEOR.DIWA.Backend.entity.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findByUserIdOrderByCreateDateDesc(Long userId);
    List<AppNotification> findByUserIdAndIsReadFalseOrderByCreateDateDesc(Long userId);
    void deleteByExpiresAtBefore(java.time.LocalDateTime now);

    /** Marque toutes les notifications non-lues d'un utilisateur comme lues en une seule requête. */
    @Modifying
    @Query("UPDATE AppNotification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllReadByUserId(@Param("userId") Long userId);
}
