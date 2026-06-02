package Tg.OSEOR.DIWA.Backend.dto.FinitionDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FinitionDTORequest {
    @NotBlank(message = "Le nom de la finition est obligatoire")
    private String nom;
    
    @NotNull(message = "Le prix supplémentaire est obligatoire")
    private Double prixSupplement;
    
    private String image;
    
    @NotNull(message = "L'ID du véhicule associé est obligatoire")
    private Long vehiculeId;

    public FinitionDTORequest() {}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Double getPrixSupplement() { return prixSupplement; }
    public void setPrixSupplement(Double prixSupplement) { this.prixSupplement = prixSupplement; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }
}
