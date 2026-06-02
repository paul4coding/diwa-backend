package Tg.OSEOR.DIWA.Backend.mappers;

import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeConfigurationDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.Vehicule;

@Component
public class VehiculeMapper {

    private final FinitionMapper finitionMapper;
    private final MotorisationMapper motorisationMapper;
    private final OptionVehiculeMapper optionMapper;

    public VehiculeMapper(FinitionMapper finitionMapper, 
                          MotorisationMapper motorisationMapper, 
                          OptionVehiculeMapper optionMapper) {
        this.finitionMapper = finitionMapper;
        this.motorisationMapper = motorisationMapper;
        this.optionMapper = optionMapper;
    }

    public VehiculeDTOResponse toResponse(Vehicule entity) {
        if (entity == null) return null;
        
        VehiculeDTOResponse dto = new VehiculeDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setMarque(entity.getMarque());
        dto.setModele(entity.getModele());
        dto.setAnnee(entity.getAnnee());
        dto.setPrixBase(entity.getPrixBase());
        dto.setStock(entity.getStock());
        dto.setFichierGlb(entity.getFichierGlb());
        dto.setDossier360(entity.getDossier360());
        dto.setImagePrincipale(entity.getImagePrincipale());
        dto.setFicheTechnique(entity.getFicheTechnique());
        
        if (entity.getImagesGalerie() != null) {
            dto.setImagesGalerie(entity.getImagesGalerie().stream()
                .map(asset -> new VehiculeDTORequest.GalleryImageDTO(asset.getUrlCloudinary(), asset.getVue()))
                .collect(Collectors.toList()));
        }
        
        dto.setCouleursDispo(entity.getCouleursDispo());
        dto.setDescription(entity.getDescription());
        dto.setActif(entity.getActif());
        
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        
        return dto;
    }

    public Vehicule toEntity(VehiculeDTORequest dto) {
        if (dto == null) return null;
        
        Vehicule entity = new Vehicule();
        entity.setMarque(dto.getMarque());
        entity.setModele(dto.getModele());
        entity.setAnnee(dto.getAnnee());
        entity.setPrixBase(dto.getPrixBase());
        entity.setStock(dto.getStock());
        entity.setFichierGlb(dto.getFichierGlb());
        entity.setDossier360(dto.getDossier360());
        entity.setImagePrincipale(dto.getImagePrincipale());
        entity.setFicheTechnique(dto.getFicheTechnique());
        entity.setCouleursDispo(dto.getCouleursDispo());
        entity.setDescription(dto.getDescription());
        if (dto.getActif() != null) entity.setActif(dto.getActif());
        
        return entity;
    }

    public void updateEntityFromRequest(VehiculeDTORequest dto, Vehicule entity) {
        if (dto == null) return;
        
        if (dto.getMarque() != null) entity.setMarque(dto.getMarque());
        if (dto.getModele() != null) entity.setModele(dto.getModele());
        if (dto.getAnnee() != null) entity.setAnnee(dto.getAnnee());
        if (dto.getPrixBase() != null) entity.setPrixBase(dto.getPrixBase());
        if (dto.getStock() != null) entity.setStock(dto.getStock());
        if (dto.getFichierGlb() != null) entity.setFichierGlb(dto.getFichierGlb());
        if (dto.getDossier360() != null) entity.setDossier360(dto.getDossier360());
        if (dto.getImagePrincipale() != null) entity.setImagePrincipale(dto.getImagePrincipale());
        if (dto.getFicheTechnique() != null) entity.setFicheTechnique(dto.getFicheTechnique());
        if (dto.getCouleursDispo() != null) entity.setCouleursDispo(dto.getCouleursDispo());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getActif() != null) entity.setActif(dto.getActif());
    }
    
    public VehiculeConfigurationDTOResponse toConfigurationResponse(Vehicule entity) {
        if (entity == null) return null;
        
        VehiculeConfigurationDTOResponse config = new VehiculeConfigurationDTOResponse();
        config.setVehicule(this.toResponse(entity));
        
        if (entity.getFinitions() != null) {
            config.setFinitions(entity.getFinitions().stream()
                .map(finitionMapper::toResponse)
                .collect(Collectors.toList()));
        }
        
        if (entity.getMotorisations() != null) {
            config.setMotorisations(entity.getMotorisations().stream()
                .map(motorisationMapper::toResponse)
                .collect(Collectors.toList()));
        }
        
        if (entity.getOptions() != null) {
            config.setOptions(entity.getOptions().stream()
                .map(optionMapper::toResponse)
                .collect(Collectors.toList()));
        }
        
        return config;
    }
}
