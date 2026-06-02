package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.FinitionDTO.FinitionDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.FinitionDTO.FinitionDTOResponse;

public interface FinitionService {
    FinitionDTOResponse create(FinitionDTORequest request);
    FinitionDTOResponse update(Long id, FinitionDTORequest request);
    void delete(Long id);
    List<FinitionDTOResponse> list();
    FinitionDTOResponse getById(Long id);
    List<FinitionDTOResponse> getByVehiculeId(Long vehiculeId);
}
