package Tg.OSEOR.DIWA.Backend.entity;

import java.util.ArrayList;
import java.util.List;

import Tg.OSEOR.DIWA.Backend.entity.enums.StatutTicket;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "ticket_sav")
public class TicketSAV extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehiculeImmat;

    @Column(nullable = false)
    private String vehiculeMarque;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTicket statut = StatutTicket.OUVERT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technicien_id", nullable = true)
    private Technicien technicien;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentaireTicket> commentaires = new ArrayList<>();

    public TicketSAV() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVehiculeImmat() { return vehiculeImmat; }
    public void setVehiculeImmat(String vehiculeImmat) { this.vehiculeImmat = vehiculeImmat; }

    public String getVehiculeMarque() { return vehiculeMarque; }
    public void setVehiculeMarque(String vehiculeMarque) { this.vehiculeMarque = vehiculeMarque; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public StatutTicket getStatut() { return statut; }
    public void setStatut(StatutTicket statut) { this.statut = statut; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Technicien getTechnicien() { return technicien; }
    public void setTechnicien(Technicien technicien) { this.technicien = technicien; }

    public List<CommentaireTicket> getCommentaires() { return commentaires; }
    public void setCommentaires(List<CommentaireTicket> commentaires) { this.commentaires = commentaires; }
}
