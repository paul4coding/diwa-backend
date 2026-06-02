package Tg.OSEOR.DIWA.Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import Tg.OSEOR.DIWA.Backend.dto.CreneauDTO;
import Tg.OSEOR.DIWA.Backend.entity.PlageHoraire;
import Tg.OSEOR.DIWA.Backend.entity.RendezVous;
import Tg.OSEOR.DIWA.Backend.entity.Technicien;
import Tg.OSEOR.DIWA.Backend.repository.PlageHoraireRepository;
import Tg.OSEOR.DIWA.Backend.repository.RendezVousRepository;
import Tg.OSEOR.DIWA.Backend.repository.ServiceSAVRepository;
import Tg.OSEOR.DIWA.Backend.repository.TechnicienRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;

class RendezVousServiceTest {

    @Mock
    private TechnicienRepository technicienRepository;

    @Mock
    private PlageHoraireRepository plageHoraireRepository;

    @Mock
    private RendezVousRepository rendezVousRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceSAVRepository serviceSAVRepository;

    @InjectMocks
    private GarageServiceImpl garageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRendezVous_WhenDisponible_ShouldCreateAndBlockCreneau() {
        // Arrange
        Long plageId = 1L;
        Long userId = 1L;
        LocalDate date = LocalDate.now().plusDays(1);
        
        Technicien tech = new Technicien();
        tech.setId(1L);
        tech.setNom("Koffi");

        PlageHoraire plage = new PlageHoraire();
        plage.setId(plageId);
        plage.setEstDisponible(true);
        plage.setTechnicien(tech);

        User user = new User();
        user.setId(userId);

        when(plageHoraireRepository.findById(plageId)).thenReturn(Optional.of(plage));
        when(rendezVousRepository.existsByCreneauIdAndDate(plageId, date)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(rendezVousRepository.save(any(RendezVous.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        RendezVous result = garageService.createRendezVous(plageId, userId, null, "Vidange", date);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(tech, result.getTechnicien());
        verify(rendezVousRepository, times(1)).save(any(RendezVous.class));
    }

    @Test
    void createRendezVous_WhenIndisponible_ShouldThrowException() {
        // Arrange
        Long plageId = 1L;
        LocalDate date = LocalDate.now().plusDays(1);
        
        PlageHoraire plage = new PlageHoraire();
        plage.setId(plageId);
        plage.setEstDisponible(false); // Deja pris

        when(plageHoraireRepository.findById(plageId)).thenReturn(Optional.of(plage));

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            garageService.createRendezVous(plageId, 1L, null, "Vidange", date);
        });
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(ex.getReason().contains("déjà pris"));
    }

    @Test
    void getDisponibilites_ShouldReturnList() {
        // Arrange
        LocalDate date = LocalDate.now().plusDays(1);
        Technicien tech = new Technicien();
        tech.setId(1L);
        tech.setNom("Koffi");
        tech.setActif(true);

        PlageHoraire plage = new PlageHoraire();
        plage.setId(1L);
        plage.setHeureDebut(LocalTime.of(8, 0));
        plage.setHeureFin(LocalTime.of(9, 0));
        plage.setEstDisponible(true);
        plage.setTechnicien(tech);

        when(technicienRepository.findByActifTrue()).thenReturn(Collections.singletonList(tech));
        when(plageHoraireRepository.findByTechnicienIdAndJourSemaine(eq(1L), any(DayOfWeek.class)))
            .thenReturn(Collections.singletonList(plage));
        when(rendezVousRepository.existsByCreneauIdAndDate(1L, date)).thenReturn(false);

        // Act
        List<CreneauDTO> result = garageService.getDisponibilites(date, "Vidange");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Koffi ", result.get(0).getNomTechnicien());
    }
}
