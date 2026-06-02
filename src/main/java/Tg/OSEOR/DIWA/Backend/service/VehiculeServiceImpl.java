package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeConfigurationDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.Vehicule;
import Tg.OSEOR.DIWA.Backend.entity.AssetVisuel;
import Tg.OSEOR.DIWA.Backend.entity.enums.MarqueEnum;
import Tg.OSEOR.DIWA.Backend.mappers.VehiculeMapper;
import Tg.OSEOR.DIWA.Backend.repository.VehiculeRepository;
import Tg.OSEOR.DIWA.Backend.service.media.FileStorageService;
import java.util.ArrayList;

@Service
@Transactional
public class VehiculeServiceImpl implements VehiculeService {

    private final VehiculeRepository repo;
    private final VehiculeMapper mapper;
    private final FileStorageService fileStorageService;

    public VehiculeServiceImpl(VehiculeRepository repo, VehiculeMapper mapper, FileStorageService fileStorageService) {
        this.repo = repo;
        this.mapper = mapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public VehiculeDTOResponse create(VehiculeDTORequest request) {
        Vehicule vehicule = mapper.toEntity(request);
        
        // Gérer la galerie
        if (request.getImagesGalerie() != null) {
            for (VehiculeDTORequest.GalleryImageDTO galleryDTO : request.getImagesGalerie()) {
                AssetVisuel asset = new AssetVisuel();
                asset.setUrlCloudinary(galleryDTO.getUrl());
                asset.setVue(galleryDTO.getVue());
                asset.setVehicule(vehicule);
                vehicule.getImagesGalerie().add(asset);
            }
        }
        
        return mapper.toResponse(repo.save(vehicule));
    }

    @Override
    public VehiculeDTOResponse update(Long id, VehiculeDTORequest request) {
        Vehicule vehicule = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé: " + id));
            
        mapper.updateEntityFromRequest(request, vehicule);
        
        // Synchroniser la galerie
        if (request.getImagesGalerie() != null) {
            // Pour simplifier, on remplace tout. Une approche plus fine (diff) serait préférable en prod.
            vehicule.getImagesGalerie().clear();
            for (VehiculeDTORequest.GalleryImageDTO galleryDTO : request.getImagesGalerie()) {
                AssetVisuel asset = new AssetVisuel();
                asset.setUrlCloudinary(galleryDTO.getUrl());
                asset.setVue(galleryDTO.getVue());
                asset.setVehicule(vehicule);
                vehicule.getImagesGalerie().add(asset);
            }
        }
        
        return mapper.toResponse(repo.save(vehicule));
    }

    @Override
    public void delete(Long id) {
        Vehicule vehicule = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé: " + id));
        
        // Supprimer les fichiers physiques
        if (vehicule.getFichierGlb() != null) fileStorageService.deleteFile(vehicule.getFichierGlb());
        if (vehicule.getImagePrincipale() != null) fileStorageService.deleteFile(vehicule.getImagePrincipale());
        if (vehicule.getFicheTechnique() != null) fileStorageService.deleteFile(vehicule.getFicheTechnique());
        
        // Supprimer les fichiers de la galerie
        if (vehicule.getImagesGalerie() != null) {
            for (AssetVisuel asset : vehicule.getImagesGalerie()) {
                if (asset.getUrlCloudinary() != null) {
                    fileStorageService.deleteFile(asset.getUrlCloudinary());
                }
            }
        }
        
        repo.delete(vehicule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeDTOResponse> list() {
        return repo.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeDTOResponse> findByMarque(String marque) {
        try {
            MarqueEnum marqueEnum = MarqueEnum.valueOf(marque.toUpperCase());
            return repo.findByMarque(marqueEnum).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Marque invalide: " + marque);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeDTOResponse getById(Long id) {
        return repo.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeDTOResponse getByUuid(String uuid) {
        return repo.findByUuid(uuid)
            .map(mapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'UUID: " + uuid));
    }
    
    @Override
    @Transactional(readOnly = true)
    public VehiculeConfigurationDTOResponse getConfigurationData(Long id) {
        Vehicule vehicule = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé: " + id));
            
        // Forcer le chargement de la collection si lazy loading strict (Hibernate)
        if(vehicule.getFinitions() != null) vehicule.getFinitions().size();
        if(vehicule.getMotorisations() != null) vehicule.getMotorisations().size();
        if(vehicule.getOptions() != null) vehicule.getOptions().size();
        
        return mapper.toConfigurationResponse(vehicule);
    }

    @Override
    public VehiculeDTOResponse updateGlb(Long id, String glbPath) {
        Vehicule vehicule = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé: " + id));
            
        if (vehicule.getFichierGlb() != null) {
            fileStorageService.deleteFile(vehicule.getFichierGlb());
        }
        
        vehicule.setFichierGlb(glbPath);
        return mapper.toResponse(repo.save(vehicule));
    }

    @Override
    public VehiculeDTOResponse updateImage(Long id, String imagePath) {
        Vehicule vehicule = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé: " + id));
            
        if (vehicule.getImagePrincipale() != null) {
            fileStorageService.deleteFile(vehicule.getImagePrincipale());
        }
        
        vehicule.setImagePrincipale(imagePath);
        return mapper.toResponse(repo.save(vehicule));
    }
}
