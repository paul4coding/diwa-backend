package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "messages_chat", indexes = {
    @Index(name = "idx_chat_demande", columnList = "demande_id")
})
public class MessageChat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id", nullable = false)
    private DemandeIntervention demande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private User auteur;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoleAuteurChat roleAuteur;

    @Column(nullable = false)
    private Boolean lu = false;

    // Pièce jointe optionnelle
    @Column(length = 300)
    private String pieceJointeUrl;

    public enum RoleAuteurChat {
        CLIENT, RECEPTIONNISTE, CHEF_TECHNICIEN, ADMIN
    }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DemandeIntervention getDemande() { return demande; }
    public void setDemande(DemandeIntervention d) { this.demande = d; }

    public User getAuteur() { return auteur; }
    public void setAuteur(User u) { this.auteur = u; }

    public String getContenu() { return contenu; }
    public void setContenu(String c) { this.contenu = c; }

    public RoleAuteurChat getRoleAuteur() { return roleAuteur; }
    public void setRoleAuteur(RoleAuteurChat r) { this.roleAuteur = r; }

    public Boolean getLu() { return lu; }
    public void setLu(Boolean l) { this.lu = l; }

    public String getPieceJointeUrl() { return pieceJointeUrl; }
    public void setPieceJointeUrl(String p) { this.pieceJointeUrl = p; }
}
