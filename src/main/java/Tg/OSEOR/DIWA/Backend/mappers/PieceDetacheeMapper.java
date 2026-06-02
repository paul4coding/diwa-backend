package Tg.OSEOR.DIWA.Backend.mappers;

import org.springframework.stereotype.Component;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.PieceDetacheeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.PieceDetacheeDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.PieceDetachee;

@Component
public class PieceDetacheeMapper {
    public PieceDetacheeDTOResponse toResponse(PieceDetachee entity) {
        if (entity == null) return null;
        PieceDetacheeDTOResponse dto = new PieceDetacheeDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setReference(entity.getReference());
        dto.setNom(entity.getNom());
        dto.setPrixUnitaire(entity.getPrixUnitaire());
        dto.setQuantiteStock(entity.getQuantiteStock());
        dto.setImageUrl(entity.getImageUrl());
        
        if (entity.getCategorie() != null) {
            dto.setCategorieId(entity.getCategorie().getId());
            dto.setCategorieLibelle(entity.getCategorie().getLibelle());
        }
        
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        return dto;
    }

    public PieceDetachee toEntity(PieceDetacheeDTORequest request) {
        if (request == null) return null;
        PieceDetachee entity = new PieceDetachee();
        entity.setReference(request.getReference());
        entity.setNom(request.getNom());
        entity.setPrixUnitaire(request.getPrixUnitaire());
        entity.setQuantiteStock(request.getQuantiteStock());
        entity.setImageUrl(request.getImageUrl());
        // La catégorie est affectée au niveau du service
        return entity;
    }
    
    public void updateEntityFromRequest(PieceDetacheeDTORequest request, PieceDetachee entity) {
        if (request == null || entity == null) return;
        if(request.getReference() != null) entity.setReference(request.getReference());
        if(request.getNom() != null) entity.setNom(request.getNom());
        if(request.getPrixUnitaire() != null) entity.setPrixUnitaire(request.getPrixUnitaire());
        if(request.getQuantiteStock() != null) entity.setQuantiteStock(request.getQuantiteStock());
        if(request.getImageUrl() != null) entity.setImageUrl(request.getImageUrl());
    }
}
