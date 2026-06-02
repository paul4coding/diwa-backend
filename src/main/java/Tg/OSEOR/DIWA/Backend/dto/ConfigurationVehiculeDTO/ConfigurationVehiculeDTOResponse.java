package Tg.OSEOR.DIWA.Backend.dto.ConfigurationVehiculeDTO;

import java.time.LocalDateTime;
import java.util.List;

public class ConfigurationVehiculeDTOResponse {
    private Long id;
    private String uuid;
    private String nomConfig;
    private Double prixTotal;
    
    private Long userId;
    
    // Simplifications of associated choices to show in responses
    private Long vehiculeId;
    private String vehiculeMarqueModele;
    
    private Long finitionId;
    private String finitionNom;
    
    private Long motorisationId;
    private String motorisationNom;
    
    private List<Long> optionsIds;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public ConfigurationVehiculeDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getNomConfig() { return nomConfig; }
    public void setNomConfig(String nomConfig) { this.nomConfig = nomConfig; }

    public Double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(Double prixTotal) { this.prixTotal = prixTotal; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }

    public String getVehiculeMarqueModele() { return vehiculeMarqueModele; }
    public void setVehiculeMarqueModele(String vehiculeMarqueModele) { this.vehiculeMarqueModele = vehiculeMarqueModele; }

    public Long getFinitionId() { return finitionId; }
    public void setFinitionId(Long finitionId) { this.finitionId = finitionId; }

    public String getFinitionNom() { return finitionNom; }
    public void setFinitionNom(String finitionNom) { this.finitionNom = finitionNom; }

    public Long getMotorisationId() { return motorisationId; }
    public void setMotorisationId(Long motorisationId) { this.motorisationId = motorisationId; }

    public String getMotorisationNom() { return motorisationNom; }
    public void setMotorisationNom(String motorisationNom) { this.motorisationNom = motorisationNom; }

    public List<Long> getOptionsIds() { return optionsIds; }
    public void setOptionsIds(List<Long> optionsIds) { this.optionsIds = optionsIds; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
}
