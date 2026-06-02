package Tg.OSEOR.DIWA.Backend.dto.OptionVehiculeDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import Tg.OSEOR.DIWA.Backend.entity.enums.TypeOption;

public class OptionVehiculeDTORequest {
    @NotBlank(message = "Le nom de l'option est obligatoire")
    private String nom;
    
    @NotNull(message = "Le type d'option est obligatoire")
    private TypeOption type;
    
    @NotNull(message = "Le prix supplémentaire est obligatoire")
    private Double prixSupplement;
    
    @NotNull(message = "L'ID du véhicule associé est obligatoire")
    private Long vehiculeId;

    public OptionVehiculeDTORequest() {}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public TypeOption getType() { return type; }
    public void setType(TypeOption type) { this.type = type; }

    public Double getPrixSupplement() { return prixSupplement; }
    public void setPrixSupplement(Double prixSupplement) { this.prixSupplement = prixSupplement; }

    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }
}
