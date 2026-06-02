package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.MotorisationDTO.MotorisationDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.MotorisationDTO.MotorisationDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.Motorisation;
import Tg.OSEOR.DIWA.Backend.entity.Vehicule;
import Tg.OSEOR.DIWA.Backend.mappers.MotorisationMapper;
import Tg.OSEOR.DIWA.Backend.repository.MotorisationRepository;
import Tg.OSEOR.DIWA.Backend.repository.VehiculeRepository;

@Service
@Transactional
public class MotorisationServiceImpl implements MotorisationService {

    private final MotorisationRepository repo;
    private final VehiculeRepository vehiculeRepo;
    private final MotorisationMapper mapper;

    public MotorisationServiceImpl(MotorisationRepository repo, VehiculeRepository vehiculeRepo, MotorisationMapper mapper) {
        this.repo = repo;
        this.vehiculeRepo = vehiculeRepo;
        this.mapper = mapper;
    }

    @Override
    public MotorisationDTOResponse create(MotorisationDTORequest request) {
        Motorisation motorisation = mapper.toEntity(request);
        
        Vehicule vehicule = vehiculeRepo.findById(request.getVehiculeId())
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé: " + request.getVehiculeId()));
        motorisation.setVehicule(vehicule);
        
        return mapper.toResponse(repo.save(motorisation));
    }

    @Override
    public MotorisationDTOResponse update(Long id, MotorisationDTORequest request) {
        Motorisation motorisation = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Motorisation non trouvée: " + id));
            
        mapper.updateEntityFromRequest(request, motorisation);
        
        if (request.getVehiculeId() != null) {
            Vehicule vehicule = vehiculeRepo.findById(request.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé: " + request.getVehiculeId()));
            motorisation.setVehicule(vehicule);
        }
        
        return mapper.toResponse(repo.save(motorisation));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Motorisation non trouvée: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public List<MotorisationDTOResponse> list() {
        return repo.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public MotorisationDTOResponse getById(Long id) {
        return repo.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Motorisation non trouvée: " + id));
    }
    
    @Override
    public List<MotorisationDTOResponse> getByVehiculeId(Long vehiculeId) {
        return repo.findAll().stream()
            .filter(m -> m.getVehicule() != null && m.getVehicule().getId().equals(vehiculeId))
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }
}
