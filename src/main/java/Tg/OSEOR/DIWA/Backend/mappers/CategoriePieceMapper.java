package Tg.OSEOR.DIWA.Backend.mappers;

import org.springframework.stereotype.Component;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.CategoriePieceDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.CategoriePieceDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.CategoriePiece;

@Component
public class CategoriePieceMapper {
    public CategoriePieceDTOResponse toResponse(CategoriePiece entity) {
        if (entity == null) return null;
        CategoriePieceDTOResponse dto = new CategoriePieceDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setLibelle(entity.getLibelle());
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        return dto;
    }

    public CategoriePiece toEntity(CategoriePieceDTORequest request) {
        if (request == null) return null;
        CategoriePiece entity = new CategoriePiece();
        entity.setLibelle(request.getLibelle());
        return entity;
    }
}
