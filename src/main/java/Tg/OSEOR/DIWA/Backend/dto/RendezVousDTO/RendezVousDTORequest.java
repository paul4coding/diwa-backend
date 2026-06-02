package Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RendezVousDTORequest {
    
    private Long vehiculeId;
    
    @NotNull(message = "L'ID du service est obligatoire")
    private Long serviceId;
    
    private Long creneauId;
    private LocalDate dateRdv;

    private String immatriculation;
    private String modeleVehicule;
    private String vin;
    private Integer kilometrage;
    private String urgence; // String for Enum matching
    private String detailsSpecifiques;
    private String description;

    public RendezVousDTORequest() {}

    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public Long getCreneauId() { return creneauId; }
    public void setCreneauId(Long creneauId) { this.creneauId = creneauId; }

    public LocalDate getDateRdv() { return dateRdv; }
    public void setDateRdv(LocalDate dateRdv) { this.dateRdv = dateRdv; }

    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }

    public String getModeleVehicule() { return modeleVehicule; }
    public void setModeleVehicule(String modeleVehicule) { this.modeleVehicule = modeleVehicule; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public Integer getKilometrage() { return kilometrage; }
    public void setKilometrage(Integer kilometrage) { this.kilometrage = kilometrage; }

    public String getUrgence() { return urgence; }
    public void setUrgence(String urgence) { this.urgence = urgence; }

    public String getDetailsSpecifiques() { return detailsSpecifiques; }
    public void setDetailsSpecifiques(String detailsSpecifiques) { this.detailsSpecifiques = detailsSpecifiques; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
