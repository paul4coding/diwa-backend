package Tg.OSEOR.DIWA.Backend.dto.ConfigurationVehiculeDTO;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ConfigurationVehiculeDTORequest {
    @NotBlank(message = "Le nom de la configuration est obligatoire")
    private String nomConfig;

    @NotNull(message = "L'ID du véhicule de base est obligatoire")
    private Long vehiculeId;

    private Long finitionId;
    private Long motorisationId;
    private List<Long> optionsIds;

    public ConfigurationVehiculeDTORequest() {}

    public String getNomConfig() { return nomConfig; }
    public void setNomConfig(String nomConfig) { this.nomConfig = nomConfig; }

    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }

    public Long getFinitionId() { return finitionId; }
    public void setFinitionId(Long finitionId) { this.finitionId = finitionId; }

    public Long getMotorisationId() { return motorisationId; }
    public void setMotorisationId(Long motorisationId) { this.motorisationId = motorisationId; }

    public List<Long> getOptionsIds() { return optionsIds; }
    public void setOptionsIds(List<Long> optionsIds) { this.optionsIds = optionsIds; }
}
