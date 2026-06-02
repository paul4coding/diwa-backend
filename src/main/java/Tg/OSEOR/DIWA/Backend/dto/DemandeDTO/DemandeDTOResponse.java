package Tg.OSEOR.DIWA.Backend.dto.DemandeDTO;

import Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO.RendezVousDTOResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DemandeDTOResponse {
    private Long id;
    private String uuid;
    private String reference;
    private String statut;
    private String urgence;
    private String descriptionProbleme;
    private String vehiculeImmatriculation;
    private String vehiculeNumeroChassis;
    private String vehiculeMarque;
    private String vehiculeModele;
    private Integer vehiculeAnnee;
    private Integer vehiculeKilometrage;
    private String vehiculeCouleur;
    private String vehiculeCarburant;
    private String vehiculeBoiteVitesse;
    private String photoCarteGriseUrl;
    private String photoPlaqueUrl;
    private String photosUrlsClient;
    
    private Boolean demandeRecuperation;
    private String adresseRecuperation;
    private String contactRecuperation;
    private BigDecimal fraisRecuperation;
    private String chauffeurRecuperationNom;
    private String chauffeurRecuperationTel;
    private String chauffeurRecuperationEmail;
    private java.time.LocalDate dateRecuperation;
    private String creneauLibelle;
    private String noteDisponibiliteClient;
    
    private Boolean demandeVisite;
    private String adresseVisite;
    private RendezVousDTOResponse creneauVisite;

    private Boolean demandeLivraison;
    private String adresseLivraison;
    private BigDecimal fraisLivraison;
    private String chauffeurLivraisonNom;
    private String chauffeurLivraisonTel;
    private String chauffeurLivraisonEmail;
    
    private LocalDateTime dateHeureReception;
    private LocalDateTime dateHeureSortie;
    private String clientNom;
    private String clientEmail;
    private String clientTel;
    private String receptionnisteNom;
    private String chefTechnicienNom;
    private String technicienNom;
    
    private Boolean vehiculeDiwa;
    private Boolean diagnosticGratuit;
    private String observationsArrivee;
    
    private Long proFormaId;
    private String proFormaStatut;
    private LocalDateTime createDate;

    // Champs Checking Mission
    private String checkingPhotoAvant;
    private String checkingPhotoArriere;
    private String checkingPhotoGauche;
    private String checkingPhotoDroit;
    private String checkingObservations;
    private String missionStatut;
    private Long missionId;
    
    // Photos Réception DIWA
    private String receptionPhotoAvant;
    private String receptionPhotoArriere;
    private String receptionPhotoGauche;
    private String receptionPhotoDroit;
    private String receptionObservations;

    public DemandeDTOResponse() {}

    // Getters / Setters
    public String getCheckingPhotoAvant() { return checkingPhotoAvant; }
    public void setCheckingPhotoAvant(String s) { this.checkingPhotoAvant = s; }
    public String getCheckingPhotoArriere() { return checkingPhotoArriere; }
    public void setCheckingPhotoArriere(String s) { this.checkingPhotoArriere = s; }
    public String getCheckingPhotoGauche() { return checkingPhotoGauche; }
    public void setCheckingPhotoGauche(String s) { this.checkingPhotoGauche = s; }
    public String getCheckingPhotoDroit() { return checkingPhotoDroit; }
    public void setCheckingPhotoDroit(String s) { this.checkingPhotoDroit = s; }
    public String getCheckingObservations() { return checkingObservations; }
    public void setCheckingObservations(String s) { this.checkingObservations = s; }
    public String getMissionStatut() { return missionStatut; }
    public void setMissionStatut(String s) { this.missionStatut = s; }
    public Long getMissionId() { return missionId; }
    public void setMissionId(Long id) { this.missionId = id; }
    
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }
    public String getReference() { return reference; }
    public void setReference(String r) { this.reference = r; }
    public String getStatut() { return statut; }
    public void setStatut(String s) { this.statut = s; }
    public String getUrgence() { return urgence; }
    public void setUrgence(String u) { this.urgence = u; }
    public String getDescriptionProbleme() { return descriptionProbleme; }
    public void setDescriptionProbleme(String d) { this.descriptionProbleme = d; }
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
    public String getPhotosUrlsClient() { return photosUrlsClient; }
    public void setPhotosUrlsClient(String p) { this.photosUrlsClient = p; }
    public Boolean getDemandeRecuperation() { return demandeRecuperation; }
    public void setDemandeRecuperation(Boolean d) { this.demandeRecuperation = d; }
    public String getAdresseRecuperation() { return adresseRecuperation; }
    public void setAdresseRecuperation(String a) { this.adresseRecuperation = a; }
    public String getContactRecuperation() { return contactRecuperation; }
    public void setContactRecuperation(String c) { this.contactRecuperation = c; }
    public BigDecimal getFraisRecuperation() { return fraisRecuperation; }
    public void setFraisRecuperation(BigDecimal f) { this.fraisRecuperation = f; }
    public String getChauffeurRecuperationNom() { return chauffeurRecuperationNom; }
    public void setChauffeurRecuperationNom(String c) { this.chauffeurRecuperationNom = c; }
    public String getChauffeurRecuperationTel() { return chauffeurRecuperationTel; }
    public void setChauffeurRecuperationTel(String t) { this.chauffeurRecuperationTel = t; }
    public String getChauffeurRecuperationEmail() { return chauffeurRecuperationEmail; }
    public void setChauffeurRecuperationEmail(String e) { this.chauffeurRecuperationEmail = e; }

    public java.time.LocalDate getDateRecuperation() { return dateRecuperation; }
    public void setDateRecuperation(java.time.LocalDate d) { this.dateRecuperation = d; }

    public String getCreneauLibelle() { return creneauLibelle; }
    public void setCreneauLibelle(String l) { this.creneauLibelle = l; }

    public String getNoteDisponibiliteClient() { return noteDisponibiliteClient; }
    public void setNoteDisponibiliteClient(String n) { this.noteDisponibiliteClient = n; }
    public Boolean getDemandeVisite() { return demandeVisite; }
    public void setDemandeVisite(Boolean d) { this.demandeVisite = d; }
    public String getAdresseVisite() { return adresseVisite; }
    public void setAdresseVisite(String a) { this.adresseVisite = a; }
    public RendezVousDTOResponse getCreneauVisite() { return creneauVisite; }
    public void setCreneauVisite(RendezVousDTOResponse c) { this.creneauVisite = c; }
    public Boolean getDemandeLivraison() { return demandeLivraison; }
    public void setDemandeLivraison(Boolean d) { this.demandeLivraison = d; }
    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String a) { this.adresseLivraison = a; }
    public BigDecimal getFraisLivraison() { return fraisLivraison; }
    public void setFraisLivraison(BigDecimal f) { this.fraisLivraison = f; }
    public String getChauffeurLivraisonNom() { return chauffeurLivraisonNom; }
    public void setChauffeurLivraisonNom(String c) { this.chauffeurLivraisonNom = c; }
    public String getChauffeurLivraisonTel() { return chauffeurLivraisonTel; }
    public void setChauffeurLivraisonTel(String t) { this.chauffeurLivraisonTel = t; }
    public String getChauffeurLivraisonEmail() { return chauffeurLivraisonEmail; }
    public void setChauffeurLivraisonEmail(String e) { this.chauffeurLivraisonEmail = e; }
    public LocalDateTime getDateHeureReception() { return dateHeureReception; }
    public void setDateHeureReception(LocalDateTime d) { this.dateHeureReception = d; }
    public LocalDateTime getDateHeureSortie() { return dateHeureSortie; }
    public void setDateHeureSortie(LocalDateTime d) { this.dateHeureSortie = d; }
    public String getClientNom() { return clientNom; }
    public void setClientNom(String c) { this.clientNom = c; }
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String c) { this.clientEmail = c; }
    public String getClientTel() { return clientTel; }
    public void setClientTel(String c) { this.clientTel = c; }
    public String getReceptionnisteNom() { return receptionnisteNom; }
    public void setReceptionnisteNom(String r) { this.receptionnisteNom = r; }
    public String getChefTechnicienNom() { return chefTechnicienNom; }
    public void setChefTechnicienNom(String c) { this.chefTechnicienNom = c; }
    public String getTechnicienNom() { return technicienNom; }
    public void setTechnicienNom(String t) { this.technicienNom = t; }
    public Boolean getVehiculeDiwa() { return vehiculeDiwa; }
    public void setVehiculeDiwa(Boolean v) { this.vehiculeDiwa = v; }
    public Boolean getDiagnosticGratuit() { return diagnosticGratuit; }
    public void setDiagnosticGratuit(Boolean d) { this.diagnosticGratuit = d; }
    public String getObservationsArrivee() { return observationsArrivee; }
    public void setObservationsArrivee(String o) { this.observationsArrivee = o; }
    public Long getProFormaId() { return proFormaId; }
    public void setProFormaId(Long p) { this.proFormaId = p; }
    public String getProFormaStatut() { return proFormaStatut; }
    public void setProFormaStatut(String p) { this.proFormaStatut = p; }
    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime c) { this.createDate = c; }
}
