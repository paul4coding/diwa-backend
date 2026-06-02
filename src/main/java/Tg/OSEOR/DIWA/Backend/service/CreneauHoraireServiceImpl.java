package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.CreneauHoraireDTO.CreneauHoraireDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.CreneauHoraireDTO.CreneauHoraireDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.CreneauHoraire;
import Tg.OSEOR.DIWA.Backend.entity.Technicien;
import Tg.OSEOR.DIWA.Backend.mappers.CreneauHoraireMapper;
import Tg.OSEOR.DIWA.Backend.repository.CreneauHoraireRepository;
import Tg.OSEOR.DIWA.Backend.repository.TechnicienRepository;

@Service
@Transactional
public class CreneauHoraireServiceImpl implements CreneauHoraireService {

    private final CreneauHoraireRepository repo;
    private final TechnicienRepository technicienRepo;
    private final CreneauHoraireMapper mapper;

    public CreneauHoraireServiceImpl(CreneauHoraireRepository repo, 
                                     TechnicienRepository technicienRepo, 
                                     CreneauHoraireMapper mapper) {
        this.repo = repo;
        this.technicienRepo = technicienRepo;
        this.mapper = mapper;
    }

    @Override
    public CreneauHoraireDTOResponse create(CreneauHoraireDTORequest request) {
        CreneauHoraire creneau = mapper.toEntity(request);
        
        Technicien technicien = technicienRepo.findById(request.getTechnicienId())
            .orElseThrow(() -> new RuntimeException("Technicien non trouvé: " + request.getTechnicienId()));
        creneau.setTechnicien(technicien);
        
        return mapper.toResponse(repo.save(creneau));
    }

    @Override
    public CreneauHoraireDTOResponse update(Long id, CreneauHoraireDTORequest request) {
        CreneauHoraire creneau = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Créneau horaire non trouvé: " + id));
            
        mapper.updateEntityFromRequest(request, creneau);
        
        if (request.getTechnicienId() != null) {
            Technicien technicien = technicienRepo.findById(request.getTechnicienId())
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé: " + request.getTechnicienId()));
            creneau.setTechnicien(technicien);
        }
        
        return mapper.toResponse(repo.save(creneau));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Créneau horaire non trouvé: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public List<CreneauHoraireDTOResponse> list() {
        return repo.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<CreneauHoraireDTOResponse> listLibres() {
        // Filtre de base en mémoire. On pourra optimiser avec une Query native @Query("WHERE estLibre = true") plus tard
        return repo.findAll().stream()
            .filter(c -> c.getEstLibre() != null && c.getEstLibre())
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public CreneauHoraireDTOResponse getById(Long id) {
        return repo.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Créneau horaire non trouvé: " + id));
    }
}
