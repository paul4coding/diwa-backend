package Tg.OSEOR.DIWA.Backend.dto.FinitionDTO;

import java.time.LocalDateTime;

public class FinitionDTOResponse {
    private Long id;
    private String uuid;
    private String nom;
    private Double prixSupplement;
    private String image;
    private Long vehiculeId;
    
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public FinitionDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Double getPrixSupplement() { return prixSupplement; }
    public void setPrixSupplement(Double prixSupplement) { this.prixSupplement = prixSupplement; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
}
