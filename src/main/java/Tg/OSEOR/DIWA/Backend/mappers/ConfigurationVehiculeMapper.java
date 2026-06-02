package Tg.OSEOR.DIWA.Backend.mappers;

import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
import Tg.OSEOR.DIWA.Backend.dto.ConfigurationVehiculeDTO.ConfigurationVehiculeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.ConfigurationVehiculeDTO.ConfigurationVehiculeDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.ConfigurationVehicule;
import Tg.OSEOR.DIWA.Backend.entity.OptionVehicule;

@Component
public class ConfigurationVehiculeMapper {

    public ConfigurationVehiculeDTOResponse toResponse(ConfigurationVehicule entity) {
        if (entity == null) return null;
        
        ConfigurationVehiculeDTOResponse dto = new ConfigurationVehiculeDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setNomConfig(entity.getNomConfig());
        dto.setPrixTotal(entity.getPrixTotal());
        
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }
        
        if (entity.getVehicule() != null) {
            dto.setVehiculeId(entity.getVehicule().getId());
            String marque = entity.getVehicule().getMarque() != null ? entity.getVehicule().getMarque().name() : "INCONNUE";
            dto.setVehiculeMarqueModele(marque + " " + entity.getVehicule().getModele());
        }
        
        if (entity.getFinition() != null) {
            dto.setFinitionId(entity.getFinition().getId());
            dto.setFinitionNom(entity.getFinition().getNom());
        }
        
        if (entity.getMotorisation() != null) {
            dto.setMotorisationId(entity.getMotorisation().getId());
            if(entity.getMotorisation().getType() != null) {
                dto.setMotorisationNom(entity.getMotorisation().getType().name());
            }
        }
        
        if (entity.getOptionsChoisies() != null) {
            dto.setOptionsIds(entity.getOptionsChoisies().stream()
                .map(OptionVehicule::getId)
                .collect(Collectors.toList()));
        }
        
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        
        return dto;
    }

    public ConfigurationVehicule toEntity(ConfigurationVehiculeDTORequest dto) {
        if (dto == null) return null;
        
        ConfigurationVehicule entity = new ConfigurationVehicule();
        entity.setNomConfig(dto.getNomConfig());
        
        return entity;
    }
}
