package Tg.OSEOR.DIWA.Backend.repository;

import Tg.OSEOR.DIWA.Backend.entity.MessageChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageChatRepository extends JpaRepository<MessageChat, Long> {
    List<MessageChat> findByDemandeIdOrderByCreateDateAsc(Long demandeId);
    long countByDemandeIdAndLuFalseAndAuteurIdNot(Long demandeId, Long userId);

    @Modifying
    @Query("UPDATE MessageChat m SET m.lu = true WHERE m.demande.id = :demandeId AND m.auteur.id != :userId")
    void marquerLusParUtilisateur(@Param("demandeId") Long demandeId, @Param("userId") Long userId);
}
