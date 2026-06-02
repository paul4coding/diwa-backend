package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.CategoriePieceDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.CategoriePieceDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.CategoriePiece;
import Tg.OSEOR.DIWA.Backend.mappers.CategoriePieceMapper;
import Tg.OSEOR.DIWA.Backend.repository.CategoriePieceRepository;

@Service
@Transactional
public class CategoriePieceServiceImpl implements CategoriePieceService {

    private final CategoriePieceRepository repo;
    private final CategoriePieceMapper mapper;

    public CategoriePieceServiceImpl(CategoriePieceRepository repo, CategoriePieceMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public CategoriePieceDTOResponse create(CategoriePieceDTORequest request) {
        CategoriePiece entity = mapper.toEntity(request);
        return mapper.toResponse(repo.save(entity));
    }

    @Override
    public CategoriePieceDTOResponse update(Long id, CategoriePieceDTORequest request) {
        CategoriePiece entity = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Catégorie non trouvée: " + id));
            
        entity.setLibelle(request.getLibelle());
        return mapper.toResponse(repo.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Catégorie non trouvée: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriePieceDTOResponse> list() {
        return repo.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }
}
