package Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RendezVousDTOResponse {
    private Long id;
    private String uuid;
    private LocalDate date;
    private String statut;
    
    private Long userId;
    private String userName;
    
    private Long technicienId;
    private String technicienNom;
    
    private Long serviceId;
    private String serviceLibelle;
    
    private Long vehiculeId;
    private String vehiculeMarque;
    private String vehiculeModele;
    
    private Long creneauId;
    private String creneauHeures; // ex: "08:00 - 09:00"
    
    private String immatriculation;
    private String description;
    
    private String vin;
    private Integer kilometrage;
    private String urgence;
    private String detailsSpecifiques;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public RendezVousDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getTechnicienId() { return technicienId; }
    public void setTechnicienId(Long technicienId) { this.technicienId = technicienId; }

    public String getTechnicienNom() { return technicienNom; }
    public void setTechnicienNom(String technicienNom) { this.technicienNom = technicienNom; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getServiceLibelle() { return serviceLibelle; }
    public void setServiceLibelle(String serviceLibelle) { this.serviceLibelle = serviceLibelle; }

    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }

    public String getVehiculeMarque() { return vehiculeMarque; }
    public void setVehiculeMarque(String vehiculeMarque) { this.vehiculeMarque = vehiculeMarque; }

    public String getVehiculeModele() { return vehiculeModele; }
    public void setVehiculeModele(String vehiculeModele) { this.vehiculeModele = vehiculeModele; }

    public Long getCreneauId() { return creneauId; }
    public void setCreneauId(Long creneauId) { this.creneauId = creneauId; }

    public String getCreneauHeures() { return creneauHeures; }
    public void setCreneauHeures(String creneauHeures) { this.creneauHeures = creneauHeures; }

    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public Integer getKilometrage() { return kilometrage; }
    public void setKilometrage(Integer kilometrage) { this.kilometrage = kilometrage; }
    public String getUrgence() { return urgence; }
    public void setUrgence(String urgence) { this.urgence = urgence; }
    public String getDetailsSpecifiques() { return detailsSpecifiques; }
    public void setDetailsSpecifiques(String detailsSpecifiques) { this.detailsSpecifiques = detailsSpecifiques; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
}
