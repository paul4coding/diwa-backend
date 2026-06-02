package Tg.OSEOR.DIWA.Backend.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "commentaire_ticket")
public class CommentaireTicket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(nullable = false)
    private String auteur;

    private LocalDateTime dateCommentaire = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @JsonIgnore
    private TicketSAV ticket;

    public CommentaireTicket() {}

    public CommentaireTicket(String message, String auteur, TicketSAV ticket) {
        this.message = message;
        this.auteur = auteur;
        this.ticket = ticket;
        this.dateCommentaire = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public LocalDateTime getDateCommentaire() { return dateCommentaire; }
    public void setDateCommentaire(LocalDateTime dateCommentaire) { this.dateCommentaire = dateCommentaire; }

    public TicketSAV getTicket() { return ticket; }
    public void setTicket(TicketSAV ticket) { this.ticket = ticket; }
}
