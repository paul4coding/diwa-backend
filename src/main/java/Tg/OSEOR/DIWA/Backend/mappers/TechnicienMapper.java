package Tg.OSEOR.DIWA.Backend.mappers;

import org.springframework.stereotype.Component;
import Tg.OSEOR.DIWA.Backend.dto.TechnicienDTO.TechnicienDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.TechnicienDTO.TechnicienDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.Technicien;

@Component
public class TechnicienMapper {

    public TechnicienDTOResponse toResponse(Technicien entity) {
        if (entity == null) return null;
        
        TechnicienDTOResponse dto = new TechnicienDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setNom(entity.getNom());
        dto.setSpecialite(entity.getSpecialite());
        dto.setGrade(entity.getGrade());
        dto.setChargeTravailMax(entity.getChargeTravailMax());
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        
        return dto;
    }

    public Technicien toEntity(TechnicienDTORequest dto) {
        if (dto == null) return null;
        
        Technicien entity = new Technicien();
        entity.setNom(dto.getNom());
        entity.setSpecialite(dto.getSpecialite());
        entity.setGrade(dto.getGrade());
        entity.setChargeTravailMax(dto.getChargeTravailMax());
        
        return entity;
    }

    public void updateEntityFromRequest(TechnicienDTORequest dto, Technicien entity) {
        if (dto == null) return;
        
        if (dto.getNom() != null) entity.setNom(dto.getNom());
        if (dto.getSpecialite() != null) entity.setSpecialite(dto.getSpecialite());
        if (dto.getGrade() != null) entity.setGrade(dto.getGrade());
        if (dto.getChargeTravailMax() != null) entity.setChargeTravailMax(dto.getChargeTravailMax());
    }
}
