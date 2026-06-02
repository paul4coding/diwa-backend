package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import Tg.OSEOR.DIWA.Backend.entity.CommentaireTicket;
import Tg.OSEOR.DIWA.Backend.entity.Technicien;
import Tg.OSEOR.DIWA.Backend.entity.TicketSAV;
import Tg.OSEOR.DIWA.Backend.entity.enums.StatutTicket;
import Tg.OSEOR.DIWA.Backend.repository.CommentaireTicketRepository;
import Tg.OSEOR.DIWA.Backend.repository.TechnicienRepository;
import Tg.OSEOR.DIWA.Backend.repository.TicketSAVRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;

@Service
@Transactional
public class TicketSAVServiceImpl implements TicketSAVService {

    @Autowired
    private TicketSAVRepository ticketRepository;

    @Autowired
    private CommentaireTicketRepository commentaireRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TechnicienRepository technicienRepository;

    @Override
    public TicketSAV create(String vehiculeImmat, String vehiculeMarque, String description, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        TicketSAV ticket = new TicketSAV();
        ticket.setVehiculeImmat(vehiculeImmat);
        ticket.setVehiculeMarque(vehiculeMarque);
        ticket.setDescription(description);
        ticket.setStatut(StatutTicket.OUVERT);
        ticket.setUser(user);

        return ticketRepository.save(ticket);
    }

    @Override
    public TicketSAV updateStatut(Long ticketId, String statut) {
        TicketSAV ticket = getById(ticketId);
        ticket.setStatut(StatutTicket.valueOf(statut.toUpperCase()));
        return ticketRepository.save(ticket);
    }

    @Override
    public TicketSAV assignTechnicien(Long ticketId, Long technicienId) {
        TicketSAV ticket = getById(ticketId);
        Technicien tech = technicienRepository.findById(technicienId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Technicien introuvable"));

        ticket.setTechnicien(tech);
        if (ticket.getStatut() == StatutTicket.OUVERT) {
            ticket.setStatut(StatutTicket.EN_COURS);
        }
        return ticketRepository.save(ticket);
    }

    @Override
    public CommentaireTicket addCommentaire(Long ticketId, String auteur, String message) {
        TicketSAV ticket = getById(ticketId);
        CommentaireTicket commentaire = new CommentaireTicket(message, auteur, ticket);
        return commentaireRepository.save(commentaire);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketSAV> getByUser(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketSAV> getAll() {
        return ticketRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TicketSAV getById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket SAV introuvable: id=" + id));
    }
}
