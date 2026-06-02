package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.OptionVehiculeDTO.OptionVehiculeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.OptionVehiculeDTO.OptionVehiculeDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.OptionVehicule;
import Tg.OSEOR.DIWA.Backend.entity.Vehicule;
import Tg.OSEOR.DIWA.Backend.mappers.OptionVehiculeMapper;
import Tg.OSEOR.DIWA.Backend.repository.OptionVehiculeRepository;
import Tg.OSEOR.DIWA.Backend.repository.VehiculeRepository;

@Service
@Transactional
public class OptionVehiculeServiceImpl implements OptionVehiculeService {

    private final OptionVehiculeRepository repo;
    private final VehiculeRepository vehiculeRepo;
    private final OptionVehiculeMapper mapper;

    public OptionVehiculeServiceImpl(OptionVehiculeRepository repo, VehiculeRepository vehiculeRepo, OptionVehiculeMapper mapper) {
        this.repo = repo;
        this.vehiculeRepo = vehiculeRepo;
        this.mapper = mapper;
    }

    @Override
    public OptionVehiculeDTOResponse create(OptionVehiculeDTORequest request) {
        OptionVehicule option = mapper.toEntity(request);
        
        Vehicule vehicule = vehiculeRepo.findById(request.getVehiculeId())
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé: " + request.getVehiculeId()));
        option.setVehicule(vehicule);
        
        return mapper.toResponse(repo.save(option));
    }

    @Override
    public OptionVehiculeDTOResponse update(Long id, OptionVehiculeDTORequest request) {
        OptionVehicule option = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Option non trouvée: " + id));
            
        mapper.updateEntityFromRequest(request, option);
        
        if (request.getVehiculeId() != null) {
            Vehicule vehicule = vehiculeRepo.findById(request.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé: " + request.getVehiculeId()));
            option.setVehicule(vehicule);
        }
        
        return mapper.toResponse(repo.save(option));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Option non trouvée: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public List<OptionVehiculeDTOResponse> list() {
        return repo.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public OptionVehiculeDTOResponse getById(Long id) {
        return repo.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Option non trouvée: " + id));
    }
    
    @Override
    public List<OptionVehiculeDTOResponse> getByVehiculeId(Long vehiculeId) {
        return repo.findAll().stream()
            .filter(o -> o.getVehicule() != null && o.getVehicule().getId().equals(vehiculeId))
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }
}
