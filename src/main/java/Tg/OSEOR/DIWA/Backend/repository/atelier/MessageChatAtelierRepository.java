package Tg.OSEOR.DIWA.Backend.repository.atelier;

import Tg.OSEOR.DIWA.Backend.model.atelier.MessageChatAtelier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageChatAtelierRepository extends JpaRepository<MessageChatAtelier, Long> {
    List<MessageChatAtelier> findByDemandeIdOrderByCreateDateAsc(Long demandeId);
    
    @Query("SELECT COUNT(m) FROM MessageChatAtelier m WHERE m.demande.id = :demandeId AND m.lu = false AND m.auteur.id != :userId")
    long countByDemandeIdAndLuFalseAndAuteurIdNot(@Param("demandeId") Long demandeId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE MessageChatAtelier m SET m.lu = true WHERE m.demande.id = :demandeId AND m.auteur.id != :userId")
    void marquerLusParUtilisateur(@Param("demandeId") Long demandeId, @Param("userId") Long userId);
}
