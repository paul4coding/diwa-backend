package Tg.OSEOR.DIWA.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Tg.OSEOR.DIWA.Backend.entity.CommentaireTicket;

@Repository
public interface CommentaireTicketRepository extends JpaRepository<CommentaireTicket, Long> {

    List<CommentaireTicket> findByTicketIdOrderByDateCommentaireAsc(Long ticketId);
}
