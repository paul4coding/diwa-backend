package Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.FinitionDTO.FinitionDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.MotorisationDTO.MotorisationDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.OptionVehiculeDTO.OptionVehiculeDTOResponse;

public class VehiculeConfigurationDTOResponse {
    private VehiculeDTOResponse vehicule;
    private List<FinitionDTOResponse> finitions;
    private List<MotorisationDTOResponse> motorisations;
    private List<OptionVehiculeDTOResponse> options;

    public VehiculeConfigurationDTOResponse() {}

    public VehiculeDTOResponse getVehicule() { return vehicule; }
    public void setVehicule(VehiculeDTOResponse vehicule) { this.vehicule = vehicule; }

    public List<FinitionDTOResponse> getFinitions() { return finitions; }
    public void setFinitions(List<FinitionDTOResponse> finitions) { this.finitions = finitions; }

    public List<MotorisationDTOResponse> getMotorisations() { return motorisations; }
    public void setMotorisations(List<MotorisationDTOResponse> motorisations) { this.motorisations = motorisations; }

    public List<OptionVehiculeDTOResponse> getOptions() { return options; }
    public void setOptions(List<OptionVehiculeDTOResponse> options) { this.options = options; }
}
