package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "historique_statuts_demande")
public class HistoriqueStatutDemande extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id", nullable = false)
    private DemandeIntervention demande;

    @Enumerated(EnumType.STRING)
    @Column(length = 35)
    private DemandeIntervention.StatutDemande ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 35)
    private DemandeIntervention.StatutDemande nouveauStatut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id")
    private User auteur;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    public HistoriqueStatutDemande() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DemandeIntervention getDemande() { return demande; }
    public void setDemande(DemandeIntervention d) { this.demande = d; }

    public DemandeIntervention.StatutDemande getAncienStatut() { return ancienStatut; }
    public void setAncienStatut(DemandeIntervention.StatutDemande s) { this.ancienStatut = s; }

    public DemandeIntervention.StatutDemande getNouveauStatut() { return nouveauStatut; }
    public void setNouveauStatut(DemandeIntervention.StatutDemande s) { this.nouveauStatut = s; }

    public User getAuteur() { return auteur; }
    public void setAuteur(User u) { this.auteur = u; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String c) { this.commentaire = c; }
}
