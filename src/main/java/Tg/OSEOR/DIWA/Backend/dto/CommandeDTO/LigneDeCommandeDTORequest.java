package Tg.OSEOR.DIWA.Backend.dto.CommandeDTO;

import jakarta.validation.constraints.NotNull;
import Tg.OSEOR.DIWA.Backend.entity.enums.TypeLigneCommande;

public class LigneDeCommandeDTORequest {
    @NotNull(message = "Le type de ligne est obligatoire")
    private TypeLigneCommande type;
    
    @NotNull(message = "La quantité est obligatoire")
    private Integer quantite;
    
    @NotNull(message = "L'ID de référence (Config ou Pièce) est obligatoire")
    private Long referenceId;

    public LigneDeCommandeDTORequest() {}

    public TypeLigneCommande getType() { return type; }
    public void setType(TypeLigneCommande type) { this.type = type; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
}
