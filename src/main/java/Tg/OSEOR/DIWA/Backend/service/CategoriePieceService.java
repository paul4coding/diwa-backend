package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.CategoriePieceDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.CategoriePieceDTOResponse;

public interface CategoriePieceService {
    CategoriePieceDTOResponse create(CategoriePieceDTORequest request);
    CategoriePieceDTOResponse update(Long id, CategoriePieceDTORequest request);
    void delete(Long id);
    List<CategoriePieceDTOResponse> list();
}
