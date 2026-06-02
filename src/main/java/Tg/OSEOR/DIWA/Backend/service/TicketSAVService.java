package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;

import Tg.OSEOR.DIWA.Backend.entity.CommentaireTicket;
import Tg.OSEOR.DIWA.Backend.entity.TicketSAV;

public interface TicketSAVService {

    TicketSAV create(String vehiculeImmat, String vehiculeMarque, String description, Long userId);

    TicketSAV updateStatut(Long ticketId, String statut);

    TicketSAV assignTechnicien(Long ticketId, Long technicienId);

    CommentaireTicket addCommentaire(Long ticketId, String auteur, String message);

    List<TicketSAV> getByUser(Long userId);

    List<TicketSAV> getAll();

    TicketSAV getById(Long id);
}
