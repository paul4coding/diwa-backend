package Tg.OSEOR.DIWA.Backend.mappers;

import org.springframework.stereotype.Component;
import Tg.OSEOR.DIWA.Backend.dto.FinitionDTO.FinitionDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.FinitionDTO.FinitionDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.Finition;

@Component
public class FinitionMapper {

    public FinitionDTOResponse toResponse(Finition entity) {
        if (entity == null) return null;
        
        FinitionDTOResponse dto = new FinitionDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setNom(entity.getNom());
        dto.setPrixSupplement(entity.getPrixSupplement());
        dto.setImage(entity.getImage());
        
        if (entity.getVehicule() != null) {
            dto.setVehiculeId(entity.getVehicule().getId());
        }
        
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        
        return dto;
    }

    public Finition toEntity(FinitionDTORequest dto) {
        if (dto == null) return null;
        
        Finition entity = new Finition();
        entity.setNom(dto.getNom());
        entity.setPrixSupplement(dto.getPrixSupplement());
        entity.setImage(dto.getImage());
        
        return entity;
    }

    public void updateEntityFromRequest(FinitionDTORequest dto, Finition entity) {
        if (dto == null) return;
        
        if (dto.getNom() != null) entity.setNom(dto.getNom());
        if (dto.getPrixSupplement() != null) entity.setPrixSupplement(dto.getPrixSupplement());
        if (dto.getImage() != null) entity.setImage(dto.getImage());
    }
}
