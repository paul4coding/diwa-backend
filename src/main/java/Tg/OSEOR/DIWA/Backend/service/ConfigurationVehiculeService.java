package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.ConfigurationVehiculeDTO.ConfigurationVehiculeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.ConfigurationVehiculeDTO.ConfigurationVehiculeDTOResponse;

public interface ConfigurationVehiculeService {
    ConfigurationVehiculeDTOResponse create(ConfigurationVehiculeDTORequest request, String userEmail);
    ConfigurationVehiculeDTOResponse getById(Long id);
    List<ConfigurationVehiculeDTOResponse> getMyConfigurations(String userEmail);
    void delete(Long id, String userEmail);
}
