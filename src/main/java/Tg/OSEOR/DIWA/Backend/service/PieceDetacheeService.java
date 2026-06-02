package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.PieceDetacheeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.PieceDetacheeDTOResponse;

public interface PieceDetacheeService {
    PieceDetacheeDTOResponse create(PieceDetacheeDTORequest request);
    PieceDetacheeDTOResponse update(Long id, PieceDetacheeDTORequest request);
    PieceDetacheeDTOResponse getById(Long id);
    void delete(Long id);
    List<PieceDetacheeDTOResponse> listAll();
    List<PieceDetacheeDTOResponse> listByCategory(Long categoryId);
    List<PieceDetacheeDTOResponse> search(String keyword);
    PieceDetacheeDTOResponse adjustStock(Long id, Integer delta);
}
