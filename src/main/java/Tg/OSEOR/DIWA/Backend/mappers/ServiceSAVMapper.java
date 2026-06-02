package Tg.OSEOR.DIWA.Backend.mappers;

import org.springframework.stereotype.Component;
import Tg.OSEOR.DIWA.Backend.dto.ServiceSAVDTO.ServiceSAVDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.ServiceSAVDTO.ServiceSAVDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.ServiceSAV;

@Component
public class ServiceSAVMapper {

    public ServiceSAVDTOResponse toResponse(ServiceSAV entity) {
        if (entity == null) return null;
        
        ServiceSAVDTOResponse dto = new ServiceSAVDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setLibelle(entity.getLibelle());
        dto.setDureeEstimee(entity.getDureeEstimee());
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        
        return dto;
    }

    public ServiceSAV toEntity(ServiceSAVDTORequest dto) {
        if (dto == null) return null;
        
        ServiceSAV entity = new ServiceSAV();
        entity.setLibelle(dto.getLibelle());
        entity.setDureeEstimee(dto.getDureeEstimee());
        
        return entity;
    }

    public void updateEntityFromRequest(ServiceSAVDTORequest dto, ServiceSAV entity) {
        if (dto == null) return;
        
        if (dto.getLibelle() != null) entity.setLibelle(dto.getLibelle());
        if (dto.getDureeEstimee() != null) entity.setDureeEstimee(dto.getDureeEstimee());
    }
}
