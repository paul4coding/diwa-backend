package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;

import Tg.OSEOR.DIWA.Backend.entity.CommentaireTicket;
import Tg.OSEOR.DIWA.Backend.entity.TicketSAV;

public interface TicketSAVService {

    TicketSAV create(String vehiculeImmat, String vehiculeMarque, String description, Long userId);

    // Méthodes sécurisées — utilisent le username du JWT, pas un param externe
    TicketSAV createForUsername(String vehiculeImmat, String vehiculeMarque, String description, String username);

    List<TicketSAV> getByUsername(String username);

    TicketSAV updateStatut(Long ticketId, String statut);

    TicketSAV assignTechnicien(Long ticketId, Long technicienId);

    CommentaireTicket addCommentaire(Long ticketId, String auteur, String message);

    List<TicketSAV> getByUser(Long userId);

    List<TicketSAV> getAll();

    TicketSAV getById(Long id);
}
