package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeConfigurationDTOResponse;

public interface VehiculeService {
    VehiculeDTOResponse create(VehiculeDTORequest request);
    VehiculeDTOResponse update(Long id, VehiculeDTORequest request);
    void delete(Long id);
    List<VehiculeDTOResponse> list();
    List<VehiculeDTOResponse> findByMarque(String marque);
    VehiculeDTOResponse getById(Long id);
    VehiculeDTOResponse getByUuid(String uuid);
    VehiculeConfigurationDTOResponse getConfigurationData(Long id);
    VehiculeDTOResponse updateGlb(Long id, String glbPath);
    VehiculeDTOResponse updateImage(Long id, String imagePath);
}
