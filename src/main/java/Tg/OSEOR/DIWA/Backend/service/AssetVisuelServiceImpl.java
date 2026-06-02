package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.AssetVisuelDTO.AssetVisuelDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.AssetVisuelDTO.AssetVisuelDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.AssetVisuel;
import Tg.OSEOR.DIWA.Backend.entity.OptionVehicule;
import Tg.OSEOR.DIWA.Backend.mappers.AssetVisuelMapper;
import Tg.OSEOR.DIWA.Backend.repository.AssetVisuelRepository;
import Tg.OSEOR.DIWA.Backend.repository.OptionVehiculeRepository;

@Service
@Transactional
public class AssetVisuelServiceImpl implements AssetVisuelService {

    private final AssetVisuelRepository repo;
    private final OptionVehiculeRepository optionRepo;
    private final AssetVisuelMapper mapper;

    public AssetVisuelServiceImpl(AssetVisuelRepository repo, OptionVehiculeRepository optionRepo, AssetVisuelMapper mapper) {
        this.repo = repo;
        this.optionRepo = optionRepo;
        this.mapper = mapper;
    }

    @Override
    public AssetVisuelDTOResponse create(AssetVisuelDTORequest request) {
        AssetVisuel asset = mapper.toEntity(request);
        
        OptionVehicule option = optionRepo.findById(request.getOptionId())
            .orElseThrow(() -> new RuntimeException("Option non trouvée: " + request.getOptionId()));
        asset.setOption(option);
        
        return mapper.toResponse(repo.save(asset));
    }

    @Override
    public AssetVisuelDTOResponse update(Long id, AssetVisuelDTORequest request) {
        AssetVisuel asset = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Asset visuel non trouvé: " + id));
            
        mapper.updateEntityFromRequest(request, asset);
        
        if (request.getOptionId() != null) {
            OptionVehicule option = optionRepo.findById(request.getOptionId())
                .orElseThrow(() -> new RuntimeException("Option non trouvée: " + request.getOptionId()));
            asset.setOption(option);
        }
        
        return mapper.toResponse(repo.save(asset));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Asset visuel non trouvé: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public List<AssetVisuelDTOResponse> list() {
        return repo.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public AssetVisuelDTOResponse getById(Long id) {
        return repo.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Asset visuel non trouvé: " + id));
    }
    
    @Override
    public List<AssetVisuelDTOResponse> getByOptionId(Long optionId) {
        return repo.findAll().stream()
            .filter(a -> a.getOption() != null && a.getOption().getId().equals(optionId))
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }
}
