package Tg.OSEOR.DIWA.Backend.dto.DemandeDTO;

public class DemandeCreateRequest {
    private String descriptionProbleme;
    private String vehiculeImmatriculation;
    private String vehiculeNumeroChassis;
    private String vehiculeMarque;
    private String vehiculeModele;
    private Integer vehiculeAnnee;
    private Integer vehiculeKilometrage;
    private String photosUrlsClient;
    private String urgence;
    
    private Boolean demandeRecuperation;
    private String adresseRecuperation;
    private String contactRecuperation;
    
    private String dateRecuperation; // ISO Date String
    private Long creneauId;
    private String noteDisponibiliteClient;
    private Long clientId;

    public DemandeCreateRequest() {}

    // Getters / Setters
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
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
    public String getPhotosUrlsClient() { return photosUrlsClient; }
    public void setPhotosUrlsClient(String p) { this.photosUrlsClient = p; }
    public String getUrgence() { return urgence; }
    public void setUrgence(String u) { this.urgence = u; }
    public Boolean getDemandeRecuperation() { return demandeRecuperation; }
    public void setDemandeRecuperation(Boolean d) { this.demandeRecuperation = d; }
    public String getAdresseRecuperation() { return adresseRecuperation; }
    public void setAdresseRecuperation(String a) { this.adresseRecuperation = a; }
    public String getContactRecuperation() { return contactRecuperation; }
    public void setContactRecuperation(String c) { this.contactRecuperation = c; }

    public String getDateRecuperation() { return dateRecuperation; }
    public void setDateRecuperation(String d) { this.dateRecuperation = d; }

    public Long getCreneauId() { return creneauId; }
    public void setCreneauId(Long id) { this.creneauId = id; }

    public String getNoteDisponibiliteClient() { return noteDisponibiliteClient; }
    public void setNoteDisponibiliteClient(String n) { this.noteDisponibiliteClient = n; }
}
