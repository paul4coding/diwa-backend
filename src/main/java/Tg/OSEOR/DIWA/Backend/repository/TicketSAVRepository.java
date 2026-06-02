package Tg.OSEOR.DIWA.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Tg.OSEOR.DIWA.Backend.entity.TicketSAV;
import Tg.OSEOR.DIWA.Backend.entity.enums.StatutTicket;

@Repository
public interface TicketSAVRepository extends JpaRepository<TicketSAV, Long> {

    List<TicketSAV> findByUserId(Long userId);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"user", "technicien", "commentaires"})
    List<TicketSAV> findAll();

    List<TicketSAV> findByTechnicienId(Long technicienId);

    long countByStatut(StatutTicket statut);

    List<TicketSAV> findByStatut(StatutTicket statut);
}
