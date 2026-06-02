package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.MotorisationDTO.MotorisationDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.MotorisationDTO.MotorisationDTOResponse;

public interface MotorisationService {
    MotorisationDTOResponse create(MotorisationDTORequest request);
    MotorisationDTOResponse update(Long id, MotorisationDTORequest request);
    void delete(Long id);
    List<MotorisationDTOResponse> list();
    MotorisationDTOResponse getById(Long id);
    List<MotorisationDTOResponse> getByVehiculeId(Long vehiculeId);
}
