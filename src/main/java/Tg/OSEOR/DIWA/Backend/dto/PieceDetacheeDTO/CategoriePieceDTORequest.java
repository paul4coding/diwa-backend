package Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO;

import jakarta.validation.constraints.NotBlank;

public class CategoriePieceDTORequest {
    @NotBlank(message = "Le libellé de la catégorie est obligatoire")
    private String libelle;

    public CategoriePieceDTORequest() {}

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
}
