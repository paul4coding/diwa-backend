package Tg.OSEOR.DIWA.Backend.dto.OptionVehiculeDTO;

import java.time.LocalDateTime;

public class OptionVehiculeDTOResponse {
    private Long id;
    private String uuid;
    private String nom;
    private String type;
    private Double prixSupplement;
    private Long vehiculeId;
    
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public OptionVehiculeDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getPrixSupplement() { return prixSupplement; }
    public void setPrixSupplement(Double prixSupplement) { this.prixSupplement = prixSupplement; }

    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
}
