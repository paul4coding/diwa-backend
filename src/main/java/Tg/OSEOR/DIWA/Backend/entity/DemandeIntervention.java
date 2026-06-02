package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "demandes_intervention", indexes = {
    @Index(name = "idx_demande_client", columnList = "client_id"),
    @Index(name = "idx_demande_statut", columnList = "statut"),
    @Index(name = "idx_demande_reference", columnList = "reference")
})
public class DemandeIntervention extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Client propriétaire ───────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = true)
    private User client;

    // ── Infos véhicule ────────────────────────────────────────
    @Column(length = 20)
    private String vehiculeImmatriculation;

    @Column(length = 30)
    private String vehiculeNumeroChassis;

    @Column(length = 50)
    private String vehiculeMarque;

    @Column(length = 50)
    private String vehiculeModele;

    @Column
    private Integer vehiculeAnnee;

    @Column
    private Integer vehiculeKilometrage;

    @Column(length = 50)
    private String vehiculeCouleur;

    @Column(length = 30)
    private String vehiculeCarburant;

    @Column(length = 30)
    private String vehiculeBoiteVitesse;

    // Photo carte grise et plaque (uploadées à l'enregistrement)
    @Column(length = 300)
    private String photoCarteGriseUrl;

    @Column(length = 300)
    private String photoPlaqueUrl;

    // ── Description du problème ───────────────────────────────
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descriptionProbleme;

    // Photos jointes par le client (URLs séparées par |)
    @Column(columnDefinition = "TEXT")
    private String photosUrlsClient;

    // ── Urgence ───────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private UrgenceDemande urgence = UrgenceDemande.NORMALE;

    // ── Récupération / Livraison à domicile ──────────────────
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean demandeRecuperation = false;

    @Column(columnDefinition = "TEXT")
    private String adresseRecuperation;

    @Column(length = 20)
    private String contactRecuperation;

    @Column(precision = 12, scale = 2)
    private BigDecimal fraisRecuperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chauffeur_recuperation_id")
    private User chauffeurRecuperation;

    private java.time.LocalDate dateRecuperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creneau_souhaite_id")
    private PlageHoraireDIWA creneauSouhaite;

    @Column(columnDefinition = "TEXT")
    private String noteDisponibiliteClient;

    // ── Expert à domicile / Visite ─────────────────────────────
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean demandeVisite = false;

    @Column(columnDefinition = "TEXT")
    private String adresseVisite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creneau_visite_id")
    private RendezVous creneauVisite;

    // ── Livraison après réparation ────────────────────────────
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean demandeLivraison = false;

    @Column(columnDefinition = "TEXT")
    private String adresseLivraison;

    @Column(precision = 12, scale = 2)
    private BigDecimal fraisLivraison;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chauffeur_livraison_id")
    private User chauffeurLivraison;

    // ── Réception physique ────────────────────────────────────
    @Column
    private LocalDateTime dateHeureReception;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receptionniste_id")
    private User receptionniste;

    // ── Sortie du véhicule ────────────────────────────────────
    @Column
    private LocalDateTime dateHeureSortie;

    // ── Statut du dossier ─────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 35)
    private StatutDemande statut = StatutDemande.SOUMISE;

    // ── Référence lisible (DEM-2025-00042) ───────────────────
    @Column(unique = true, length = 30)
    private String reference;

    // ── Vérification véhicule DIWA ────────────────────────────
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean vehiculeDiwa = false;

    @Column
    private Integer kilometrageAchat;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean diagnosticGratuit = false;

    // ── Observations du garagiste à l'arrivée ────────────────
    @Column(columnDefinition = "TEXT")
    private String observationsArrivee;

    // ── Personnel assigné ─────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chef_technicien_id")
    private User chefTechnicien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technicien_id")
    private User technicien;

    // ── Relations ─────────────────────────────────────────────
    @OneToOne(mappedBy = "demande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProForma proForma;

    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("createDate ASC")
    private List<HistoriqueStatutDemande> historique = new ArrayList<>();

    public enum StatutDemande {
        SOUMISE,
        CHAUFFEUR_ASSIGNE,
        CHAUFFEUR_EN_ROUTE,
        CHAUFFEUR_ARRIVE_CHEZ_CLIENT,
        VEHICULE_EN_TRANSIT,
        VEHICULE_RECU,
        EN_ENREGISTREMENT,
        EN_RECEPTION,
        EN_DIAGNOSTIC,
        PROFORMA_V1,
        PROFORMA_V2,
        EN_ATTENTE_CLIENT,
        SELECTION_RECUE,
        EN_ATTENTE_CONFIRMATION_FINALE,
        PROFORMA_VALIDE,
        CONFIRME,
        EN_COURS_REPARATION,
        PRET,
        EN_LIVRAISON,
        CLOTURE
    }

    public enum UrgenceDemande {
        NORMALE, URGENTE, TRES_URGENTE
    }

    public DemandeIntervention() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }

    public String getVehiculeImmatriculation() { return vehiculeImmatriculation; }
    public void setVehiculeImmatriculation(String v) { this.vehiculeImmatriculation = v; }

    public String getVehiculeNumeroChassis() { return vehiculeNumeroChassis; }
    public void setVehiculeNumeroChassis(String v) { this.vehiculeNumeroChassis = v; }

    public String getVehiculeMarque() { return vehiculeMarque; }
    public void setVehiculeMarque(String v) { this.vehiculeMarque = v; }

    public String getVehiculeModele() { return vehiculeModele; }
    public void setVehiculeModele(String v) { this.vehiculeModele = v; }

    public Integer getVehiculeAnnee() { return vehiculeAnnee; }
    public void setVehiculeAnnee(Integer v) { this.vehiculeAnnee = v; }

    public Integer getVehiculeKilometrage() { return vehiculeKilometrage; }
    public void setVehiculeKilometrage(Integer v) { this.vehiculeKilometrage = v; }

    public String getVehiculeCouleur() { return vehiculeCouleur; }
    public void setVehiculeCouleur(String v) { this.vehiculeCouleur = v; }

    public String getVehiculeCarburant() { return vehiculeCarburant; }
    public void setVehiculeCarburant(String v) { this.vehiculeCarburant = v; }

    public String getVehiculeBoiteVitesse() { return vehiculeBoiteVitesse; }
    public void setVehiculeBoiteVitesse(String v) { this.vehiculeBoiteVitesse = v; }

    public String getPhotoCarteGriseUrl() { return photoCarteGriseUrl; }
    public void setPhotoCarteGriseUrl(String p) { this.photoCarteGriseUrl = p; }

    public String getPhotoPlaqueUrl() { return photoPlaqueUrl; }
    public void setPhotoPlaqueUrl(String p) { this.photoPlaqueUrl = p; }

    public String getDescriptionProbleme() { return descriptionProbleme; }
    public void setDescriptionProbleme(String d) { this.descriptionProbleme = d; }

    public String getPhotosUrlsClient() { return photosUrlsClient; }
    public void setPhotosUrlsClient(String p) { this.photosUrlsClient = p; }

    public UrgenceDemande getUrgence() { return urgence; }
    public void setUrgence(UrgenceDemande u) { this.urgence = u; }

    public Boolean getDemandeRecuperation() { return demandeRecuperation; }
    public void setDemandeRecuperation(Boolean d) { this.demandeRecuperation = d; }

    public String getAdresseRecuperation() { return adresseRecuperation; }
    public void setAdresseRecuperation(String a) { this.adresseRecuperation = a; }

    public String getContactRecuperation() { return contactRecuperation; }
    public void setContactRecuperation(String c) { this.contactRecuperation = c; }

    public BigDecimal getFraisRecuperation() { return fraisRecuperation; }
    public void setFraisRecuperation(BigDecimal f) { this.fraisRecuperation = f; }

    public User getChauffeurRecuperation() { return chauffeurRecuperation; }
    public void setChauffeurRecuperation(User c) { this.chauffeurRecuperation = c; }

    public java.time.LocalDate getDateRecuperation() { return dateRecuperation; }
    public void setDateRecuperation(java.time.LocalDate d) { this.dateRecuperation = d; }

    public PlageHoraireDIWA getCreneauSouhaite() { return creneauSouhaite; }
    public void setCreneauSouhaite(PlageHoraireDIWA c) { this.creneauSouhaite = c; }

    public String getNoteDisponibiliteClient() { return noteDisponibiliteClient; }
    public void setNoteDisponibiliteClient(String n) { this.noteDisponibiliteClient = n; }

    public Boolean getDemandeVisite() { return demandeVisite; }
    public void setDemandeVisite(Boolean d) { this.demandeVisite = d; }

    public String getAdresseVisite() { return adresseVisite; }
    public void setAdresseVisite(String a) { this.adresseVisite = a; }

    public RendezVous getCreneauVisite() { return creneauVisite; }
    public void setCreneauVisite(RendezVous c) { this.creneauVisite = c; }

    public Boolean getDemandeLivraison() { return demandeLivraison; }
    public void setDemandeLivraison(Boolean d) { this.demandeLivraison = d; }

    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String a) { this.adresseLivraison = a; }

    public BigDecimal getFraisLivraison() { return fraisLivraison; }
    public void setFraisLivraison(BigDecimal f) { this.fraisLivraison = f; }

    public User getChauffeurLivraison() { return chauffeurLivraison; }
    public void setChauffeurLivraison(User c) { this.chauffeurLivraison = c; }

    public LocalDateTime getDateHeureReception() { return dateHeureReception; }
    public void setDateHeureReception(LocalDateTime d) { this.dateHeureReception = d; }

    public User getReceptionniste() { return receptionniste; }
    public void setReceptionniste(User r) { this.receptionniste = r; }

    public LocalDateTime getDateHeureSortie() { return dateHeureSortie; }
    public void setDateHeureSortie(LocalDateTime d) { this.dateHeureSortie = d; }

    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande s) { this.statut = s; }

    public String getReference() { return reference; }
    public void setReference(String r) { this.reference = r; }

    public Boolean getVehiculeDiwa() { return vehiculeDiwa; }
    public void setVehiculeDiwa(Boolean v) { this.vehiculeDiwa = v; }

    public Integer getKilometrageAchat() { return kilometrageAchat; }
    public void setKilometrageAchat(Integer k) { this.kilometrageAchat = k; }

    public Boolean getDiagnosticGratuit() { return diagnosticGratuit; }
    public void setDiagnosticGratuit(Boolean d) { this.diagnosticGratuit = d; }

    public String getObservationsArrivee() { return observationsArrivee; }
    public void setObservationsArrivee(String o) { this.observationsArrivee = o; }

    public User getChefTechnicien() { return chefTechnicien; }
    public void setChefTechnicien(User c) { this.chefTechnicien = c; }

    public User getTechnicien() { return technicien; }
    public void setTechnicien(User t) { this.technicien = t; }

    public ProForma getProForma() { return proForma; }
    public void setProForma(ProForma p) { this.proForma = p; }

    public List<HistoriqueStatutDemande> getHistorique() { return historique; }
    public void setHistorique(List<HistoriqueStatutDemande> h) { this.historique = h; }

    // Helper method for legacy code calling getClientNom()
    public String getClientNom() {
        return client != null ? (client.getPrenom() + " " + client.getNom()) : "Client DIWA";
    }
}
