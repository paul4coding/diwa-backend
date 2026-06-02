package Tg.OSEOR.DIWA.Backend.dto.DemandeDTO;

public class EnregistrementVehiculeRequest {
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
    private String observationsArrivee;
    private Long technicienId;

    public EnregistrementVehiculeRequest() {}

    // Getters / Setters
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
    public String getObservationsArrivee() { return observationsArrivee; }
    public void setObservationsArrivee(String o) { this.observationsArrivee = o; }
    public Long getTechnicienId() { return technicienId; }
    public void setTechnicienId(Long id) { this.technicienId = id; }
}
