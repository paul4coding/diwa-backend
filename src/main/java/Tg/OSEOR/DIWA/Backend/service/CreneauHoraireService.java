package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.CreneauHoraireDTO.CreneauHoraireDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.CreneauHoraireDTO.CreneauHoraireDTOResponse;

public interface CreneauHoraireService {
    CreneauHoraireDTOResponse create(CreneauHoraireDTORequest request);
    CreneauHoraireDTOResponse update(Long id, CreneauHoraireDTORequest request);
    void delete(Long id);
    List<CreneauHoraireDTOResponse> list();
    List<CreneauHoraireDTOResponse> listLibres();
    CreneauHoraireDTOResponse getById(Long id);
}
