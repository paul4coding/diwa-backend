package Tg.OSEOR.DIWA.Backend.dto.MotorisationDTO;

import java.time.LocalDateTime;

public class MotorisationDTOResponse {
    private Long id;
    private String uuid;
    private String type;
    private Integer puissance;
    private Integer couple;
    private String moteur;
    private Double prix;
    private Long vehiculeId;
    
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public MotorisationDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

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

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
}
