package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.TechnicienDTO.TechnicienDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.TechnicienDTO.TechnicienDTOResponse;

public interface TechnicienService {
    TechnicienDTOResponse create(TechnicienDTORequest request);
    TechnicienDTOResponse update(Long id, TechnicienDTORequest request);
    void delete(Long id);
    List<TechnicienDTOResponse> list();
    TechnicienDTOResponse getById(Long id);
    void toggleActif(Long id);
}
