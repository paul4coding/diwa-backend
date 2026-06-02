package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.OptionVehiculeDTO.OptionVehiculeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.OptionVehiculeDTO.OptionVehiculeDTOResponse;

public interface OptionVehiculeService {
    OptionVehiculeDTOResponse create(OptionVehiculeDTORequest request);
    OptionVehiculeDTOResponse update(Long id, OptionVehiculeDTORequest request);
    void delete(Long id);
    List<OptionVehiculeDTOResponse> list();
    OptionVehiculeDTOResponse getById(Long id);
    List<OptionVehiculeDTOResponse> getByVehiculeId(Long vehiculeId);
}
