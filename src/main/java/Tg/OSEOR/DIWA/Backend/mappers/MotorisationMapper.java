package Tg.OSEOR.DIWA.Backend.mappers;

import org.springframework.stereotype.Component;
import Tg.OSEOR.DIWA.Backend.dto.MotorisationDTO.MotorisationDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.MotorisationDTO.MotorisationDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.Motorisation;

@Component
public class MotorisationMapper {

    public MotorisationDTOResponse toResponse(Motorisation entity) {
        if (entity == null) return null;
        
        MotorisationDTOResponse dto = new MotorisationDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        
        if (entity.getType() != null) {
            dto.setType(entity.getType().name());
        }
        
        dto.setPuissance(entity.getPuissance());
        dto.setCouple(entity.getCouple());
        dto.setMoteur(entity.getMoteur());
        dto.setPrix(entity.getPrix());
        
        if (entity.getVehicule() != null) {
            dto.setVehiculeId(entity.getVehicule().getId());
        }
        
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        
        return dto;
    }

    public Motorisation toEntity(MotorisationDTORequest dto) {
        if (dto == null) return null;
        
        Motorisation entity = new Motorisation();
        entity.setType(dto.getType());
        entity.setPuissance(dto.getPuissance());
        entity.setCouple(dto.getCouple());
        entity.setMoteur(dto.getMoteur());
        entity.setPrix(dto.getPrix());
        
        return entity;
    }

    public void updateEntityFromRequest(MotorisationDTORequest dto, Motorisation entity) {
        if (dto == null) return;
        
        if (dto.getType() != null) entity.setType(dto.getType());
        if (dto.getPuissance() != null) entity.setPuissance(dto.getPuissance());
        if (dto.getCouple() != null) entity.setCouple(dto.getCouple());
        if (dto.getMoteur() != null) entity.setMoteur(dto.getMoteur());
        if (dto.getPrix() != null) entity.setPrix(dto.getPrix());
    }
}
