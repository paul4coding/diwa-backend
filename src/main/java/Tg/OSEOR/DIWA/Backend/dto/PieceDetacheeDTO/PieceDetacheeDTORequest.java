package Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class PieceDetacheeDTORequest {
    @NotBlank(message = "La référence est obligatoire")
    private String reference;
    
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    
    @NotNull(message = "Le prix unitaire est obligatoire")
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    private Double prixUnitaire;
    
    @NotNull(message = "La quantité en stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private Integer quantiteStock;
    
    private String imageUrl;
    
    @NotNull(message = "L'ID de la catégorie est obligatoire")
    private Long categorieId;

    public PieceDetacheeDTORequest() {}

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public Integer getQuantiteStock() { return quantiteStock; }
    public void setQuantiteStock(Integer quantiteStock) { this.quantiteStock = quantiteStock; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Long getCategorieId() { return categorieId; }
    public void setCategorieId(Long categorieId) { this.categorieId = categorieId; }
}
