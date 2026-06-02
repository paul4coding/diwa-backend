package Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO;

import java.time.LocalDateTime;

public class PieceDetacheeDTOResponse {
    private Long id;
    private String uuid;
    private String reference;
    private String nom;
    private Double prixUnitaire;
    private Integer quantiteStock;
    private String imageUrl;
    
    private Long categorieId;
    private String categorieLibelle;
    
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public PieceDetacheeDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

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

    public String getCategorieLibelle() { return categorieLibelle; }
    public void setCategorieLibelle(String categorieLibelle) { this.categorieLibelle = categorieLibelle; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
}
