package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.FinitionDTO.FinitionDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.FinitionDTO.FinitionDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.Finition;
import Tg.OSEOR.DIWA.Backend.entity.Vehicule;
import Tg.OSEOR.DIWA.Backend.mappers.FinitionMapper;
import Tg.OSEOR.DIWA.Backend.repository.FinitionRepository;
import Tg.OSEOR.DIWA.Backend.repository.VehiculeRepository;

@Service
@Transactional
public class FinitionServiceImpl implements FinitionService {

    private final FinitionRepository repo;
    private final VehiculeRepository vehiculeRepo;
    private final FinitionMapper mapper;

    public FinitionServiceImpl(FinitionRepository repo, VehiculeRepository vehiculeRepo, FinitionMapper mapper) {
        this.repo = repo;
        this.vehiculeRepo = vehiculeRepo;
        this.mapper = mapper;
    }

    @Override
    public FinitionDTOResponse create(FinitionDTORequest request) {
        Finition finition = mapper.toEntity(request);
        
        Vehicule vehicule = vehiculeRepo.findById(request.getVehiculeId())
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé: " + request.getVehiculeId()));
        finition.setVehicule(vehicule);
        
        return mapper.toResponse(repo.save(finition));
    }

    @Override
    public FinitionDTOResponse update(Long id, FinitionDTORequest request) {
        Finition finition = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Finition non trouvée: " + id));
            
        mapper.updateEntityFromRequest(request, finition);
        
        if (request.getVehiculeId() != null) {
            Vehicule vehicule = vehiculeRepo.findById(request.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé: " + request.getVehiculeId()));
            finition.setVehicule(vehicule);
        }
        
        return mapper.toResponse(repo.save(finition));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Finition non trouvée: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public List<FinitionDTOResponse> list() {
        return repo.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public FinitionDTOResponse getById(Long id) {
        return repo.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Finition non trouvée: " + id));
    }
    
    @Override
    public List<FinitionDTOResponse> getByVehiculeId(Long vehiculeId) {
        return repo.findAll().stream()
            .filter(f -> f.getVehicule() != null && f.getVehicule().getId().equals(vehiculeId))
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }
}
