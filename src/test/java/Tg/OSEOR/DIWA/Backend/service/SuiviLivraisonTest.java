package Tg.OSEOR.DIWA.Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import Tg.OSEOR.DIWA.Backend.dto.MissionChauffeurDTO.MissionDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.mappers.MissionMapper;
import Tg.OSEOR.DIWA.Backend.mappers.UserMapper;
import Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur;
import Tg.OSEOR.DIWA.Backend.repository.DemandeInterventionRepository;
import Tg.OSEOR.DIWA.Backend.repository.atelier.MissionChauffeurRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;

/**
 * Sprint 5 — Tests unitaires pour le suivi livraison côté client.
 *
 * Scénarios couverts :
 *  [getLivraisonForClient]
 *  1. Happy path : client propriétaire + mission LIVRAISON → DTO retourné
 *  2. Client non-propriétaire → SecurityException
 *  3. Utilisateur introuvable → RuntimeException
 *  4. Demande introuvable → RuntimeException
 *  5. Aucune mission pour la demande → RuntimeException
 *  6. Seule une mission RECUPERATION → RuntimeException (filtre LIVRAISON)
 *  7. Plusieurs missions dont LIVRAISON → la 1ère (plus récente selon ORDER BY) retournée
 *
 *  [MissionMapper — infos chauffeur]
 *  8. Chauffeur renseigné → DTO contient nom, prénom et téléphone
 *  9. Chauffeur null → DTO sans NPE, champs null
 * 10. Adresse LIVRAISON mappée depuis adresseLivraison (pas adresseRecuperation)
 */
@DisplayName("Sprint 5 — Suivi livraison client + Mapper chauffeur")
class SuiviLivraisonTest {

    // ─── Mocks (service) ─────────────────────────────────────────────────────

    @Mock private MissionChauffeurRepository missionRepo;
    @Mock private DemandeInterventionRepository demandeRepo;
    @Mock private DemandeInterventionService demandeService;
    @Mock private UserRepository userRepository;
    @Mock private MissionMapper missionMapper;
    @Mock private UserMapper userMapper;
    @Mock private NotificationService notificationService;
    @Mock private EmailService emailService;

    @InjectMocks
    private MissionChauffeurServiceImpl service;

    // ─── Fixtures ────────────────────────────────────────────────────────────

    private User clientProprietaire;
    private User autreClient;
    private User chauffeur;
    private DemandeIntervention demande;
    private MissionChauffeur missionLivraison;
    private MissionChauffeur missionRecuperation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        clientProprietaire = new User();
        clientProprietaire.setId(2L);
        clientProprietaire.setPrenom("Jean");
        clientProprietaire.setNom("Client");
        clientProprietaire.setEmail("client@test.tg");
        clientProprietaire.setUsername("client@test.tg");

        autreClient = new User();
        autreClient.setId(99L);
        autreClient.setEmail("intrus@test.tg");
        autreClient.setUsername("intrus@test.tg");

        chauffeur = new User();
        chauffeur.setId(5L);
        chauffeur.setPrenom("Koffi");
        chauffeur.setNom("Agbeko");
        chauffeur.setTelephone("+22890112233");

        demande = new DemandeIntervention();
        demande.setId(10L);
        demande.setClient(clientProprietaire);
        demande.setVehiculeMarque("Toyota");
        demande.setVehiculeModele("Corolla");
        demande.setVehiculeImmatriculation("TG-4567-BY");
        demande.setAdresseLivraison("Quartier Bè, Lomé");
        demande.setStatut(DemandeIntervention.StatutDemande.EN_LIVRAISON);

        missionLivraison = new MissionChauffeur();
        missionLivraison.setId(20L);
        missionLivraison.setChauffeur(chauffeur);
        missionLivraison.setDemande(demande);
        missionLivraison.setType(MissionChauffeur.TypeMission.LIVRAISON);
        missionLivraison.setStatut(MissionChauffeur.StatutMission.EN_ROUTE_VERS_CLIENT);

        missionRecuperation = new MissionChauffeur();
        missionRecuperation.setId(15L);
        missionRecuperation.setChauffeur(chauffeur);
        missionRecuperation.setDemande(demande);
        missionRecuperation.setType(MissionChauffeur.TypeMission.RECUPERATION);
        missionRecuperation.setStatut(MissionChauffeur.StatutMission.TERMINEE);

        // Stubs par défaut
        when(missionMapper.toDTO(any())).thenReturn(new MissionDTOResponse());
        when(missionRepo.saveAndFlush(any())).thenAnswer(i -> i.getArguments()[0]);
    }

    // =========================================================================
    // GROUPE 1 : getLivraisonForClient
    // =========================================================================
    @Nested
    @DisplayName("getLivraisonForClient")
    class GetLivraisonForClientTests {

        @Test
        @DisplayName("1. Happy path — client propriétaire + mission LIVRAISON → DTO retourné")
        void happyPath_retourneDTO() {
            when(userRepository.findByIdentifier("client@test.tg"))
                .thenReturn(Optional.of(clientProprietaire));
            when(demandeRepo.findById(10L))
                .thenReturn(Optional.of(demande));
            when(missionRepo.findByDemandeIdOrderByCreateDateDesc(10L))
                .thenReturn(List.of(missionLivraison));

            MissionDTOResponse result = service.getLivraisonForClient(10L, "client@test.tg");

            assertNotNull(result, "Le DTO doit être retourné");
            verify(missionMapper).toDTO(missionLivraison);
        }

        @Test
        @DisplayName("2. Client non-propriétaire — SecurityException")
        void autreClient_securityException() {
            when(userRepository.findByIdentifier("intrus@test.tg"))
                .thenReturn(Optional.of(autreClient));
            when(demandeRepo.findById(10L))
                .thenReturn(Optional.of(demande));
            // La demande appartient à clientProprietaire (id=2), pas à autreClient (id=99)

            assertThrows(SecurityException.class,
                () -> service.getLivraisonForClient(10L, "intrus@test.tg"),
                "Un client non-propriétaire doit recevoir SecurityException");

            verify(missionRepo, never()).findByDemandeIdOrderByCreateDateDesc(anyLong());
        }

        @Test
        @DisplayName("3. Utilisateur introuvable → RuntimeException")
        void utilisateurIntrouvable_runtimeException() {
            when(userRepository.findByIdentifier("inconnu@test.tg"))
                .thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                () -> service.getLivraisonForClient(10L, "inconnu@test.tg"));

            verify(demandeRepo, never()).findById(anyLong());
        }

        @Test
        @DisplayName("4. Demande introuvable → RuntimeException")
        void demandeIntrouvable_runtimeException() {
            when(userRepository.findByIdentifier("client@test.tg"))
                .thenReturn(Optional.of(clientProprietaire));
            when(demandeRepo.findById(999L))
                .thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                () -> service.getLivraisonForClient(999L, "client@test.tg"));
        }

        @Test
        @DisplayName("5. Aucune mission pour la demande → RuntimeException")
        void aucuneMission_runtimeException() {
            when(userRepository.findByIdentifier("client@test.tg"))
                .thenReturn(Optional.of(clientProprietaire));
            when(demandeRepo.findById(10L))
                .thenReturn(Optional.of(demande));
            when(missionRepo.findByDemandeIdOrderByCreateDateDesc(10L))
                .thenReturn(Collections.emptyList());

            assertThrows(RuntimeException.class,
                () -> service.getLivraisonForClient(10L, "client@test.tg"),
                "Liste vide doit lever RuntimeException");
        }

        @Test
        @DisplayName("6. Seule une mission RECUPERATION — filtrée → RuntimeException")
        void seulementRecuperation_RuntimeException() {
            when(userRepository.findByIdentifier("client@test.tg"))
                .thenReturn(Optional.of(clientProprietaire));
            when(demandeRepo.findById(10L))
                .thenReturn(Optional.of(demande));
            // Seule la mission RECUPERATION dans la liste (doit être filtrée)
            when(missionRepo.findByDemandeIdOrderByCreateDateDesc(10L))
                .thenReturn(List.of(missionRecuperation));

            assertThrows(RuntimeException.class,
                () -> service.getLivraisonForClient(10L, "client@test.tg"),
                "Une seule mission RECUPERATION doit lever RuntimeException après filtre");
        }

        @Test
        @DisplayName("7. Plusieurs missions : retourne la première LIVRAISON (order by date desc)")
        void plusieursMissions_retourneLaPlusRecente() {
            MissionChauffeur ancienneLivraison = new MissionChauffeur();
            ancienneLivraison.setId(5L);
            ancienneLivraison.setChauffeur(chauffeur);
            ancienneLivraison.setDemande(demande);
            ancienneLivraison.setType(MissionChauffeur.TypeMission.LIVRAISON);
            ancienneLivraison.setStatut(MissionChauffeur.StatutMission.TERMINEE);

            // Repository retourne [missionLivraison(id=20), missionRecuperation(id=15), ancienneLivraison(id=5)]
            // → 1ère LIVRAISON = missionLivraison(id=20)
            when(userRepository.findByIdentifier("client@test.tg"))
                .thenReturn(Optional.of(clientProprietaire));
            when(demandeRepo.findById(10L))
                .thenReturn(Optional.of(demande));
            when(missionRepo.findByDemandeIdOrderByCreateDateDesc(10L))
                .thenReturn(List.of(missionLivraison, missionRecuperation, ancienneLivraison));

            service.getLivraisonForClient(10L, "client@test.tg");

            // Doit mapper la plus récente LIVRAISON, pas l'ancienne
            verify(missionMapper).toDTO(missionLivraison);
            verify(missionMapper, never()).toDTO(ancienneLivraison);
            verify(missionMapper, never()).toDTO(missionRecuperation);
        }
    }

    // =========================================================================
    // GROUPE 2 : MissionMapper — infos chauffeur (tests directs sans mock)
    // =========================================================================
    @Nested
    @DisplayName("MissionMapper — infos chauffeur dans le DTO")
    class MissionMapperChauffeurTests {

        private MissionMapper mapper;

        @BeforeEach
        void setUpMapper() {
            mapper = new MissionMapper();
        }

        @Test
        @DisplayName("8. Chauffeur renseigné → DTO contient nom, prénom et téléphone")
        void chauffeurRenseigne_dtoContientInfos() {
            User driver = new User();
            driver.setNom("Agbeko");
            driver.setPrenom("Koffi");
            driver.setTelephone("+22890112233");

            MissionChauffeur m = buildMission(driver);
            MissionDTOResponse dto = mapper.toDTO(m);

            assertEquals("Agbeko",        dto.getChauffeurNom(),      "Nom du chauffeur incorrect");
            assertEquals("Koffi",         dto.getChauffeurPrenom(),   "Prénom du chauffeur incorrect");
            assertEquals("+22890112233",  dto.getChauffeurTelephone(),"Téléphone du chauffeur incorrect");
        }

        @Test
        @DisplayName("9. Chauffeur null → aucun NPE, champs null dans le DTO")
        void chauffeurNull_pasDException() {
            MissionChauffeur m = buildMission(null);

            assertDoesNotThrow(() -> {
                MissionDTOResponse dto = mapper.toDTO(m);
                assertNull(dto.getChauffeurNom(),      "Nom doit être null si chauffeur absent");
                assertNull(dto.getChauffeurPrenom(),   "Prénom doit être null si chauffeur absent");
                assertNull(dto.getChauffeurTelephone(),"Tél doit être null si chauffeur absent");
            });
        }

        @Test
        @DisplayName("10. Adresse LIVRAISON mappée depuis adresseLivraison (pas adresseRecuperation)")
        void livraison_adresseLivraisonMappee() {
            demande.setAdresseRecuperation("Adresse Recup — Ne doit pas apparaître");
            demande.setAdresseLivraison("Quartier Bè, Lomé");

            MissionChauffeur m = buildMission(chauffeur);
            // type déjà LIVRAISON
            MissionDTOResponse dto = mapper.toDTO(m);

            assertEquals("Quartier Bè, Lomé", dto.getAdresse(),
                "Pour une mission LIVRAISON, l'adresse doit être adresseLivraison");
        }

        // ─── Helper ──────────────────────────────────────────────────────────
        private MissionChauffeur buildMission(User driver) {
            MissionChauffeur m = new MissionChauffeur();
            m.setId(20L);
            m.setChauffeur(driver);
            m.setDemande(demande);
            m.setType(MissionChauffeur.TypeMission.LIVRAISON);
            m.setStatut(MissionChauffeur.StatutMission.EN_ROUTE_VERS_CLIENT);
            return m;
        }
    }
}
