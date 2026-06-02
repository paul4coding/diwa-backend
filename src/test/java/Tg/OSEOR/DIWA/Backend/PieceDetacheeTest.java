package Tg.OSEOR.DIWA.Backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.PieceDetacheeDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.PieceDetachee;
import Tg.OSEOR.DIWA.Backend.mappers.PieceDetacheeMapper;
import Tg.OSEOR.DIWA.Backend.repository.CategoriePieceRepository;
import Tg.OSEOR.DIWA.Backend.repository.PieceDetacheeRepository;
import Tg.OSEOR.DIWA.Backend.service.PieceDetacheeServiceImpl;

class PieceDetacheeTest {

    @Mock private PieceDetacheeRepository pieceRepository;
    @Mock private CategoriePieceRepository categorieRepo;   // requis par le constructeur 3-args
    @Mock private PieceDetacheeMapper mapper;               // requis par adjustStock → mapper.toResponse()

    @InjectMocks
    private PieceDetacheeServiceImpl pieceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // adjustStock appelle mapper.toResponse(repo.save(entity)) — on retourne un objet vide
        when(mapper.toResponse(any())).thenReturn(new PieceDetacheeDTOResponse());
        when(pieceRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    @DisplayName("adjustStock succès : stock diminué de 5 (10 → 5) et save appelé")
    void testAdjustStock_Success() {
        PieceDetachee piece = new PieceDetachee();
        piece.setId(1L);
        piece.setQuantiteStock(10);

        when(pieceRepository.findById(1L)).thenReturn(Optional.of(piece));

        pieceService.adjustStock(1L, -5);

        assertEquals(5, piece.getQuantiteStock());
        verify(pieceRepository).save(piece);
    }

    @Test
    @DisplayName("adjustStock stock insuffisant → ResponseStatusException 400")
    void testAdjustStock_InsufficientStock_ThrowsException() {
        PieceDetachee piece = new PieceDetachee();
        piece.setId(1L);
        piece.setQuantiteStock(2);

        when(pieceRepository.findById(1L)).thenReturn(Optional.of(piece));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> pieceService.adjustStock(1L, -5));
        assertEquals(400, ex.getStatusCode().value());
    }
}
