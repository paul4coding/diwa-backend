package Tg.OSEOR.DIWA.Backend.mappers;

import org.springframework.stereotype.Component;
import Tg.OSEOR.DIWA.Backend.dto.OptionVehiculeDTO.OptionVehiculeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.OptionVehiculeDTO.OptionVehiculeDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.OptionVehicule;

@Component
public class OptionVehiculeMapper {

    public OptionVehiculeDTOResponse toResponse(OptionVehicule entity) {
        if (entity == null) return null;
        
        OptionVehiculeDTOResponse dto = new OptionVehiculeDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setNom(entity.getNom());
        
        if (entity.getType() != null) {
            dto.setType(entity.getType().name());
        }
        
        dto.setPrixSupplement(entity.getPrixSupplement());
        
        if (entity.getVehicule() != null) {
            dto.setVehiculeId(entity.getVehicule().getId());
        }
        
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        
        return dto;
    }

    public OptionVehicule toEntity(OptionVehiculeDTORequest dto) {
        if (dto == null) return null;
        
        OptionVehicule entity = new OptionVehicule();
        entity.setNom(dto.getNom());
        entity.setType(dto.getType());
        entity.setPrixSupplement(dto.getPrixSupplement());
        
        return entity;
    }

    public void updateEntityFromRequest(OptionVehiculeDTORequest dto, OptionVehicule entity) {
        if (dto == null) return;
        
        if (dto.getNom() != null) entity.setNom(dto.getNom());
        if (dto.getType() != null) entity.setType(dto.getType());
        if (dto.getPrixSupplement() != null) entity.setPrixSupplement(dto.getPrixSupplement());
    }
}
