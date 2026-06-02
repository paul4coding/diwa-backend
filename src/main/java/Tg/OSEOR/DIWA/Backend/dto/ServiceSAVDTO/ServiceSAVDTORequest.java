package Tg.OSEOR.DIWA.Backend.dto.ServiceSAVDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ServiceSAVDTORequest {
    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;
    
    @NotNull(message = "La durée estimée est obligatoire")
    private Integer dureeEstimee;

    public ServiceSAVDTORequest() {}

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public Integer getDureeEstimee() { return dureeEstimee; }
    public void setDureeEstimee(Integer dureeEstimee) { this.dureeEstimee = dureeEstimee; }
}
