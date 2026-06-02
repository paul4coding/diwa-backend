package Tg.OSEOR.DIWA.Backend.dto.MissionChauffeurDTO;

import java.time.LocalDateTime;

public class MissionDTOResponse {
    private Long id;
    private String type;
    private String statut;
    private String heureDepart;
    private String heureArriveeClient;
    private String heureApprobationClient;
    private String heureArriveeDiwa;
    
    private String checkingPhotoAvant;
    private String checkingPhotoArriere;
    private String checkingPhotoGauche;
    private String checkingPhotoDroit;
    private String checkingVideoUrl;
    private String checkingObservations;
    private Boolean checkingComplet;
    
    private Boolean clientApprouve;
    private String motifRefusClient;
    
    private String receptionPhotoAvant;
    private String receptionPhotoArriere;
    private String receptionPhotoGauche;
    private String receptionPhotoDroit;
    private String receptionObservations;
    private Boolean receptionComplete;
    
    private Double distanceKm;

    private String chauffeurNom;
    private String chauffeurPrenom;
    private String chauffeurTelephone;

    private ClientInfo client;
    private VehiculeInfo vehicule;
    
    private String adresse;
    private String contact;
    private String demandeReference;
    private String creneauLibelle;
    private String dateMission;

    public MissionDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getHeureDepart() { return heureDepart; }
    public void setHeureDepart(String h) { this.heureDepart = h; }

    public String getHeureArriveeClient() { return heureArriveeClient; }
    public void setHeureArriveeClient(String h) { this.heureArriveeClient = h; }

    public String getHeureApprobationClient() { return heureApprobationClient; }
    public void setHeureApprobationClient(String h) { this.heureApprobationClient = h; }

    public String getHeureArriveeDiwa() { return heureArriveeDiwa; }
    public void setHeureArriveeDiwa(String h) { this.heureArriveeDiwa = h; }

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

    public String getChauffeurNom() { return chauffeurNom; }
    public void setChauffeurNom(String s) { this.chauffeurNom = s; }
    public String getChauffeurPrenom() { return chauffeurPrenom; }
    public void setChauffeurPrenom(String s) { this.chauffeurPrenom = s; }
    public String getChauffeurTelephone() { return chauffeurTelephone; }
    public void setChauffeurTelephone(String s) { this.chauffeurTelephone = s; }

    public ClientInfo getClient() { return client; }
    public void setClient(ClientInfo client) { this.client = client; }

    public VehiculeInfo getVehicule() { return vehicule; }
    public void setVehicule(VehiculeInfo vehicule) { this.vehicule = vehicule; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String s) { this.adresse = s; }

    public String getContact() { return contact; }
    public void setContact(String s) { this.contact = s; }

    public String getDemandeReference() { return demandeReference; }
    public void setDemandeReference(String s) { this.demandeReference = s; }

    public String getCreneauLibelle() { return creneauLibelle; }
    public void setCreneauLibelle(String l) { this.creneauLibelle = l; }

    public String getDateMission() { return dateMission; }
    public void setDateMission(String d) { this.dateMission = d; }

    public static class ClientInfo {
        private String nom;
        private String prenom;
        private String telephone;

        public ClientInfo() {}
        public String getNom() { return nom; }
        public void setNom(String n) { this.nom = n; }
        public String getPrenom() { return prenom; }
        public void setPrenom(String p) { this.prenom = p; }
        public String getTelephone() { return telephone; }
        public void setTelephone(String t) { this.telephone = t; }
    }

    public static class VehiculeInfo {
        private String marque;
        private String modele;
        private String immatriculation;
        private String couleur;

        public VehiculeInfo() {}
        public String getMarque() { return marque; }
        public void setMarque(String m) { this.marque = m; }
        public String getModele() { return modele; }
        public void setModele(String m) { this.modele = m; }
        public String getImmatriculation() { return immatriculation; }
        public void setImmatriculation(String i) { this.immatriculation = i; }
        public String getCouleur() { return couleur; }
        public void setCouleur(String c) { this.couleur = c; }
    }
}
