package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.PieceDetacheeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.PieceDetacheeDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.CategoriePiece;
import Tg.OSEOR.DIWA.Backend.entity.PieceDetachee;
import Tg.OSEOR.DIWA.Backend.mappers.PieceDetacheeMapper;
import Tg.OSEOR.DIWA.Backend.repository.CategoriePieceRepository;
import Tg.OSEOR.DIWA.Backend.repository.PieceDetacheeRepository;

@Service
@Transactional
public class PieceDetacheeServiceImpl implements PieceDetacheeService {

    private final PieceDetacheeRepository repo;
    private final CategoriePieceRepository categorieRepo;
    private final PieceDetacheeMapper mapper;

    public PieceDetacheeServiceImpl(PieceDetacheeRepository repo, CategoriePieceRepository categorieRepo, PieceDetacheeMapper mapper) {
        this.repo = repo;
        this.categorieRepo = categorieRepo;
        this.mapper = mapper;
    }

    @Override
    public PieceDetacheeDTOResponse create(PieceDetacheeDTORequest request) {
        CategoriePiece cat = categorieRepo.findById(request.getCategorieId())
            .orElseThrow(() -> new RuntimeException("Catégorie non trouvée: " + request.getCategorieId()));
            
        PieceDetachee entity = mapper.toEntity(request);
        entity.setCategorie(cat);
        return mapper.toResponse(repo.save(entity));
    }

    @Override
    public PieceDetacheeDTOResponse update(Long id, PieceDetacheeDTORequest request) {
        PieceDetachee entity = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Pièce non trouvée: " + id));
            
        mapper.updateEntityFromRequest(request, entity);
        
        if (request.getCategorieId() != null && (entity.getCategorie() == null || !entity.getCategorie().getId().equals(request.getCategorieId()))) {
            CategoriePiece cat = categorieRepo.findById(request.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée: " + request.getCategorieId()));
            entity.setCategorie(cat);
        }
        
        return mapper.toResponse(repo.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public PieceDetacheeDTOResponse getById(Long id) {
        return repo.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Pièce non trouvée: " + id));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Pièce non trouvée: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieceDetacheeDTOResponse> listAll() {
        return repo.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieceDetacheeDTOResponse> listByCategory(Long categoryId) {
        return repo.findByCategorieId(categoryId).stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieceDetacheeDTOResponse> search(String keyword) {
        return repo.findByNomContainingIgnoreCaseOrReferenceContainingIgnoreCase(keyword, keyword).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PieceDetacheeDTOResponse adjustStock(Long id, Integer delta) {
        PieceDetachee entity = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Pièce non trouvée: " + id));
            
        int newStock = (entity.getQuantiteStock() != null ? entity.getQuantiteStock() : 0) + delta;
        if (newStock < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Stock insuffisant pour effectuer l'opération (Stock actuel: " + entity.getQuantiteStock() + ")");
        }
        
        entity.setQuantiteStock(newStock);
        return mapper.toResponse(repo.save(entity));
    }
}
