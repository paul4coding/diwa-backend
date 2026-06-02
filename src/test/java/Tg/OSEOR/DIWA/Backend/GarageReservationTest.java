package Tg.OSEOR.DIWA.Backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import Tg.OSEOR.DIWA.Backend.entity.CreneauHoraire;
import Tg.OSEOR.DIWA.Backend.entity.PlageHoraire;
import Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO.RendezVousDTORequest;
import Tg.OSEOR.DIWA.Backend.repository.CreneauHoraireRepository;
import Tg.OSEOR.DIWA.Backend.repository.PlageHoraireRepository;
import Tg.OSEOR.DIWA.Backend.service.GarageServiceImpl;
import org.springframework.web.server.ResponseStatusException;

class GarageReservationTest {

    @Mock
    private PlageHoraireRepository plageRepo;
    
    @Mock
    private CreneauHoraireRepository creneauRepo;

    @InjectMocks
    private GarageServiceImpl garageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPrendreRendezVous_AlreadyReserved_ThrowsConflict() {
        RendezVousDTORequest request = new RendezVousDTORequest();
        request.setCreneauId(1L);
        request.setVehiculeId(1L);
        
        CreneauHoraire creneau = new CreneauHoraire();
        creneau.setEstLibre(false); // Déjà réservé
        
        // On simule que le créneau est trouvé en base
        // Note: GarageServiceImpl.createRendezVous prend des paramètres séparés (plageId, userId, etc.)
        // Je vais adapter le test pour appeler la bonne méthode
        
        when(plageRepo.findById(1L)).thenAnswer(i -> {
             PlageHoraire p = new PlageHoraire();
             p.setEstDisponible(false);
             return Optional.of(p);
        });
        
        assertThrows(ResponseStatusException.class, () -> {
            garageService.createRendezVous(1L, 1L, null, "Vidange", LocalDate.now());
        });
    }
}
