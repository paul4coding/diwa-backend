package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.TechnicienDTO.TechnicienDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.TechnicienDTO.TechnicienDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.Technicien;
import Tg.OSEOR.DIWA.Backend.mappers.TechnicienMapper;
import Tg.OSEOR.DIWA.Backend.repository.TechnicienRepository;

@Service
@Transactional
public class TechnicienServiceImpl implements TechnicienService {

    private final TechnicienRepository repo;
    private final TechnicienMapper mapper;

    public TechnicienServiceImpl(TechnicienRepository repo, TechnicienMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public TechnicienDTOResponse create(TechnicienDTORequest request) {
        Technicien technicien = mapper.toEntity(request);
        return mapper.toResponse(repo.save(technicien));
    }

    @Override
    public TechnicienDTOResponse update(Long id, TechnicienDTORequest request) {
        Technicien technicien = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Technicien non trouvé: " + id));
        mapper.updateEntityFromRequest(request, technicien);
        return mapper.toResponse(repo.save(technicien));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Technicien non trouvé: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public List<TechnicienDTOResponse> list() {
        return repo.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public TechnicienDTOResponse getById(Long id) {
        return repo.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Technicien non trouvé: " + id));
    }

    @Override
    public void toggleActif(Long id) {
        Technicien tech = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Technicien non trouvé: " + id));
        tech.setActif(!tech.isActif());
        repo.save(tech);
    }
}
