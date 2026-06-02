package Tg.OSEOR.DIWA.Backend.dto.MotorisationDTO;

import jakarta.validation.constraints.NotNull;
import Tg.OSEOR.DIWA.Backend.entity.enums.TypeMoteur;

public class MotorisationDTORequest {
    
    @NotNull(message = "Le type de moteur est obligatoire")
    private TypeMoteur type;
    
    @NotNull(message = "La puissance est obligatoire")
    private Integer puissance;
    
    @NotNull(message = "Le couple est obligatoire")
    private Integer couple;
    
    private String moteur;
    
    @NotNull(message = "Le prix est obligatoire")
    private Double prix;
    
    @NotNull(message = "L'ID du véhicule associé est obligatoire")
    private Long vehiculeId;

    public MotorisationDTORequest() {}

    public TypeMoteur getType() { return type; }
    public void setType(TypeMoteur type) { this.type = type; }

    public Integer getPuissance() { return puissance; }
    public void setPuissance(Integer puissance) { this.puissance = puissance; }

    public Integer getCouple() { return couple; }
    public void setCouple(Integer couple) { this.couple = couple; }

    public String getMoteur() { return moteur; }
    public void setMoteur(String moteur) { this.moteur = moteur; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }
}
