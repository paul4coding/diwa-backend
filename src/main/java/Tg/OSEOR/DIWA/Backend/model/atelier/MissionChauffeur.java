package Tg.OSEOR.DIWA.Backend.model.atelier;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "missions_chauffeur", indexes = {
    @Index(name = "idx_mission_chauffeur", columnList = "chauffeur_id"),
    @Index(name = "idx_mission_demande", columnList = "demande_id"),
    @Index(name = "idx_mission_statut", columnList = "statut")
})
public class MissionChauffeur extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chauffeur_id", nullable = false)
    private User chauffeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id", nullable = false)
    private DemandeIntervention demande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeMission type; // RECUPERATION ou LIVRAISON

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creneau_id")
    private Tg.OSEOR.DIWA.Backend.entity.PlageHoraireDIWA creneau;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutMission statut = StatutMission.ASSIGNEE;

    @Column
    private LocalDateTime heureDepart;

    @Column
    private LocalDateTime heureArriveeClient;

    @Column
    private LocalDateTime heureApprobationClient;

    @Column
    private LocalDateTime heureArriveeDiwa;
    
    @Column
    private LocalDateTime heureQuitterClient;

    @Column(length = 300)
    private String checkingPhotoAvant;

    @Column(length = 300)
    private String checkingPhotoArriere;

    @Column(length = 300)
    private String checkingPhotoGauche;

    @Column(length = 300)
    private String checkingPhotoDroit;

    @Column(length = 300)
    private String checkingVideoUrl;

    @Column(columnDefinition = "TEXT")
    private String checkingObservations;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean checkingComplet = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean clientApprouve = false;

    @Column(columnDefinition = "TEXT")
    private String motifRefusClient;

    @Column(length = 300)
    private String receptionPhotoAvant;

    @Column(length = 300)
    private String receptionPhotoArriere;

    @Column(length = 300)
    private String receptionPhotoGauche;

    @Column(length = 300)
    private String receptionPhotoDroit;

    @Column(columnDefinition = "TEXT")
    private String receptionObservations;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean receptionComplete = false;

    @Column
    private Double distanceKm;

    public enum TypeMission {
        RECUPERATION, LIVRAISON
    }

    public enum StatutMission {
        ASSIGNEE,
        EN_ROUTE_VERS_CLIENT,
        ARRIVE_CHEZ_CLIENT,
        CHECKING_SOUMIS,
        APPROUVE_PAR_CLIENT,
        REFUSE_PAR_CLIENT,
        EN_ROUTE_VERS_DIWA,
        ARRIVE_A_DIWA,
        TERMINEE
    }

    public MissionChauffeur() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getChauffeur() { return chauffeur; }
    public void setChauffeur(User chauffeur) { this.chauffeur = chauffeur; }

    public DemandeIntervention getDemande() { return demande; }
    public void setDemande(DemandeIntervention demande) { this.demande = demande; }

    public TypeMission getType() { return type; }
    public void setType(TypeMission type) { this.type = type; }

    public StatutMission getStatut() { return statut; }
    public void setStatut(StatutMission statut) { this.statut = statut; }

    public LocalDateTime getHeureDepart() { return heureDepart; }
    public void setHeureDepart(LocalDateTime heureDepart) { this.heureDepart = heureDepart; }

    public LocalDateTime getHeureArriveeClient() { return heureArriveeClient; }
    public void setHeureArriveeClient(LocalDateTime h) { this.heureArriveeClient = h; }

    public LocalDateTime getHeureApprobationClient() { return heureApprobationClient; }
    public void setHeureApprobationClient(LocalDateTime h) { this.heureApprobationClient = h; }

    public LocalDateTime getHeureArriveeDiwa() { return heureArriveeDiwa; }
    public void setHeureArriveeDiwa(LocalDateTime h) { this.heureArriveeDiwa = h; }

    public LocalDateTime getHeureQuitterClient() { return heureQuitterClient; }
    public void setHeureQuitterClient(LocalDateTime h) { this.heureQuitterClient = h; }

    public String getCheckingPhotoAvant() { return checkingPhotoAvant; }
    public void setCheckingPhotoAvant(String s) { this.checkingPhotoAvant = s; }

    public String getCheckingPhotoArriere() { return checkingPhotoArriere; }
    public void setCheckingPhotoArriere(String s) { this.checkingPhotoArriere = s; }

    public String getCheckingPhotoGauche() { return checkingPhotoGauche; }
    public void setCheckingPhotoGauche(String s) { this.checkingPhotoGauche = s; }

    public String getCheckingPhotoDroit() { return checkingPhotoDroit; }
    public void setCheckingPhotoDroit(String s) { this.checkingPhotoDroit = s; }

    public String getCheckingVideoUrl() { return checkingVideoUrl; }
    public void setCheckingVideoUrl(String s) { this.checkingVideoUrl = s; }

    public String getCheckingObservations() { return checkingObservations; }
    public void setCheckingObservations(String s) { this.checkingObservations = s; }

    public Boolean getCheckingComplet() { return checkingComplet; }
    public void setCheckingComplet(Boolean b) { this.checkingComplet = b; }

    public Boolean getClientApprouve() { return clientApprouve; }
    public void setClientApprouve(Boolean b) { this.clientApprouve = b; }

    public String getMotifRefusClient() { return motifRefusClient; }
    public void setMotifRefusClient(String s) { this.motifRefusClient = s; }

    public String getReceptionPhotoAvant() { return receptionPhotoAvant; }
    public void setReceptionPhotoAvant(String s) { this.receptionPhotoAvant = s; }

    public String getReceptionPhotoArriere() { return receptionPhotoArriere; }
    public void setReceptionPhotoArriere(String s) { this.receptionPhotoArriere = s; }

    public String getReceptionPhotoGauche() { return receptionPhotoGauche; }
    public void setReceptionPhotoGauche(String s) { this.receptionPhotoGauche = s; }

    public String getReceptionPhotoDroit() { return receptionPhotoDroit; }
    public void setReceptionPhotoDroit(String s) { this.receptionPhotoDroit = s; }

    public String getReceptionObservations() { return receptionObservations; }
    public void setReceptionObservations(String s) { this.receptionObservations = s; }

    public Boolean getReceptionComplete() { return receptionComplete; }
    public void setReceptionComplete(Boolean b) { this.receptionComplete = b; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double d) { this.distanceKm = d; }

    public Tg.OSEOR.DIWA.Backend.entity.PlageHoraireDIWA getCreneau() { return creneau; }
    public void setCreneau(Tg.OSEOR.DIWA.Backend.entity.PlageHoraireDIWA c) { this.creneau = c; }
}
