package Tg.OSEOR.DIWA.Backend.mappers;

import org.springframework.stereotype.Component;
import Tg.OSEOR.DIWA.Backend.dto.CreneauHoraireDTO.CreneauHoraireDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.CreneauHoraireDTO.CreneauHoraireDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.CreneauHoraire;

@Component
public class CreneauHoraireMapper {

    public CreneauHoraireDTOResponse toResponse(CreneauHoraire entity) {
        if (entity == null) return null;
        
        CreneauHoraireDTOResponse dto = new CreneauHoraireDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setDate(entity.getDate());
        dto.setHeureDebut(entity.getHeureDebut());
        dto.setHeureFin(entity.getHeureFin());
        dto.setEstLibre(entity.getEstLibre());
        
        if (entity.getTechnicien() != null) {
            dto.setTechnicienId(entity.getTechnicien().getId());
            dto.setTechnicienNom(entity.getTechnicien().getNom());
        }
        
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        
        return dto;
    }

    public CreneauHoraire toEntity(CreneauHoraireDTORequest dto) {
        if (dto == null) return null;
        
        CreneauHoraire entity = new CreneauHoraire();
        entity.setDate(dto.getDate());
        entity.setHeureDebut(dto.getHeureDebut());
        entity.setHeureFin(dto.getHeureFin());
        entity.setEstLibre(dto.getEstLibre());
        
        return entity;
    }

    public void updateEntityFromRequest(CreneauHoraireDTORequest dto, CreneauHoraire entity) {
        if (dto == null) return;
        
        if (dto.getDate() != null) entity.setDate(dto.getDate());
        if (dto.getHeureDebut() != null) entity.setHeureDebut(dto.getHeureDebut());
        if (dto.getHeureFin() != null) entity.setHeureFin(dto.getHeureFin());
        if (dto.getEstLibre() != null) entity.setEstLibre(dto.getEstLibre());
    }
}
