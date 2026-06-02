package Tg.OSEOR.DIWA.Backend.mappers;

import org.springframework.stereotype.Component;
import Tg.OSEOR.DIWA.Backend.dto.AssetVisuelDTO.AssetVisuelDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.AssetVisuelDTO.AssetVisuelDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.AssetVisuel;

@Component
public class AssetVisuelMapper {

    public AssetVisuelDTOResponse toResponse(AssetVisuel entity) {
        if (entity == null) return null;
        
        AssetVisuelDTOResponse dto = new AssetVisuelDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setUrlCloudinary(entity.getUrlCloudinary());
        dto.setVue(entity.getVue());
        dto.setzIndex(entity.getzIndex());
        
        if (entity.getOption() != null) {
            dto.setOptionId(entity.getOption().getId());
            dto.setOptionNom(entity.getOption().getNom());
        }
        
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        
        return dto;
    }

    public AssetVisuel toEntity(AssetVisuelDTORequest dto) {
        if (dto == null) return null;
        
        AssetVisuel entity = new AssetVisuel();
        entity.setUrlCloudinary(dto.getUrlCloudinary());
        entity.setVue(dto.getVue());
        entity.setzIndex(dto.getzIndex());
        
        return entity;
    }

    public void updateEntityFromRequest(AssetVisuelDTORequest dto, AssetVisuel entity) {
        if (dto == null) return;
        
        if (dto.getUrlCloudinary() != null) entity.setUrlCloudinary(dto.getUrlCloudinary());
        if (dto.getVue() != null) entity.setVue(dto.getVue());
        if (dto.getzIndex() != null) entity.setzIndex(dto.getzIndex());
    }
}
