package Tg.OSEOR.DIWA.Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import Tg.OSEOR.DIWA.Backend.dto.MissionChauffeurDTO.ReceptionRequest;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.mappers.MissionMapper;
import Tg.OSEOR.DIWA.Backend.mappers.UserMapper;
import Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur;
import Tg.OSEOR.DIWA.Backend.repository.DemandeInterventionRepository;
import Tg.OSEOR.DIWA.Backend.repository.atelier.MissionChauffeurRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;

/**
 * Tests unitaires pour les gardes de statut du flux livraison dans MissionChauffeurServiceImpl.
 *
 * Scénarios clés :
 * - marquerDepart  : LIVRAISON → demande reste EN_LIVRAISON  /  RECUPERATION → CHAUFFEUR_EN_ROUTE
 * - marquerArriveeClient : LIVRAISON → demande reste EN_LIVRAISON / RECUPERATION → CHAUFFEUR_ARRIVE_CHEZ_CLIENT
 * - confirmerLivraison : demande → CLOTURE, mission → TERMINEE
 * - confirmerLivraison sur mission RECUPERATION → IllegalStateException
 */
class MissionChauffeurDeliveryTest {

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

    private User chauffeur;
    private DemandeIntervention demande;
    private MissionChauffeur missionLivraison;
    private MissionChauffeur missionRecuperation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        chauffeur = new User();
        chauffeur.setId(5L);
        chauffeur.setPrenom("Koffi");
        chauffeur.setNom("Chauffeur");
        chauffeur.setEmail("koffi@diwa.tg");
        chauffeur.setUsername("koffi@diwa.tg");

        User client = new User();
        client.setId(2L);
        client.setPrenom("Jean");
        client.setNom("Client");
        client.setEmail("client@test.tg");

        demande = new DemandeIntervention();
        demande.setId(10L);
        demande.setClient(client);
        demande.setVehiculeMarque("Toyota");
        demande.setVehiculeImmatriculation("TG-1234-AX");
        demande.setStatut(DemandeIntervention.StatutDemande.EN_LIVRAISON);

        missionLivraison = new MissionChauffeur();
        missionLivraison.setId(20L);
        missionLivraison.setChauffeur(chauffeur);
        missionLivraison.setDemande(demande);
        missionLivraison.setType(MissionChauffeur.TypeMission.LIVRAISON);
        missionLivraison.setStatut(MissionChauffeur.StatutMission.ASSIGNEE);

        missionRecuperation = new MissionChauffeur();
        missionRecuperation.setId(21L);
        missionRecuperation.setChauffeur(chauffeur);
        missionRecuperation.setDemande(demande);
        missionRecuperation.setType(MissionChauffeur.TypeMission.RECUPERATION);
        missionRecuperation.setStatut(MissionChauffeur.StatutMission.ASSIGNEE);

        when(userRepository.findByIdentifier("koffi@diwa.tg")).thenReturn(Optional.of(chauffeur));
        when(missionRepo.saveAndFlush(any())).thenAnswer(i -> i.getArguments()[0]);
        when(demandeRepo.saveAndFlush(any())).thenAnswer(i -> i.getArguments()[0]);
        when(missionMapper.toDTO(any())).thenReturn(new MissionDTOResponse());
        doNothing().when(demandeService).ajouterHistorique(any(), any(), any(), any(), anyString());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GROUPE 1 : marquerDepart — garde de statut LIVRAISON vs RECUPERATION
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("marquerDepart")
    class MarquerDepartTests {

        @BeforeEach
        void setupDepart() {
            when(demandeRepo.findById(10L)).thenReturn(Optional.of(demande));
        }

        @Test
        @DisplayName("Mission LIVRAISON — demande doit rester EN_LIVRAISON")
        void livraison_doit_garder_en_livraison() {
            when(missionRepo.findById(20L)).thenReturn(Optional.of(missionLivraison));
            demande.setStatut(DemandeIntervention.StatutDemande.EN_LIVRAISON);

            service.marquerDepart(20L, "koffi@diwa.tg");

            assertEquals(DemandeIntervention.StatutDemande.EN_LIVRAISON, demande.getStatut(),
                "Le statut de la demande ne doit pas changer pour une mission LIVRAISON");
            verify(demandeRepo, never()).saveAndFlush(demande);
        }

        @Test
        @DisplayName("Mission RECUPERATION — demande doit passer à CHAUFFEUR_EN_ROUTE")
        void recuperation_doit_mettre_chauffeur_en_route() {
            when(missionRepo.findById(21L)).thenReturn(Optional.of(missionRecuperation));
            demande.setStatut(DemandeIntervention.StatutDemande.CHAUFFEUR_ASSIGNE);

            service.marquerDepart(21L, "koffi@diwa.tg");

            assertEquals(DemandeIntervention.StatutDemande.CHAUFFEUR_EN_ROUTE, demande.getStatut());
            verify(demandeRepo).saveAndFlush(demande);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GROUPE 2 : marquerArriveeClient — garde de statut LIVRAISON vs RECUPERATION
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("marquerArriveeClient")
    class MarquerArriveeClientTests {

        @BeforeEach
        void setup() {
            when(demandeRepo.findById(10L)).thenReturn(Optional.of(demande));
        }

        @Test
        @DisplayName("Mission LIVRAISON — demande doit rester EN_LIVRAISON")
        void livraison_doit_garder_en_livraison() {
            when(missionRepo.findById(20L)).thenReturn(Optional.of(missionLivraison));
            demande.setStatut(DemandeIntervention.StatutDemande.EN_LIVRAISON);

            service.marquerArriveeClient(20L, "koffi@diwa.tg");

            assertEquals(DemandeIntervention.StatutDemande.EN_LIVRAISON, demande.getStatut());
            verify(demandeRepo, never()).saveAndFlush(demande);
        }

        @Test
        @DisplayName("Mission RECUPERATION — demande doit passer à CHAUFFEUR_ARRIVE_CHEZ_CLIENT")
        void recuperation_doit_passer_arrive_chez_client() {
            when(missionRepo.findById(21L)).thenReturn(Optional.of(missionRecuperation));
            demande.setStatut(DemandeIntervention.StatutDemande.CHAUFFEUR_EN_ROUTE);

            service.marquerArriveeClient(21L, "koffi@diwa.tg");

            assertEquals(DemandeIntervention.StatutDemande.CHAUFFEUR_ARRIVE_CHEZ_CLIENT, demande.getStatut());
            verify(demandeRepo).saveAndFlush(demande);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GROUPE 3 : confirmerLivraison
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("confirmerLivraison")
    class ConfirmerLivraisonTests {

        private ReceptionRequest req;

        @BeforeEach
        void setup() {
            when(missionRepo.findById(20L)).thenReturn(Optional.of(missionLivraison));
            req = new ReceptionRequest();
            req.setPhotoAvantUrl("https://cdn.diwa.tg/avant.jpg");
            req.setPhotoArriereUrl("https://cdn.diwa.tg/arriere.jpg");
            req.setPhotoGaucheUrl("https://cdn.diwa.tg/gauche.jpg");
            req.setPhotoDroitUrl("https://cdn.diwa.tg/droit.jpg");
            req.setObservations("RAS, livraison effectuée");
        }

        @Test
        @DisplayName("Livraison confirmée — demande passe à CLOTURE")
        void doitCloturerLaDemande() {
            service.confirmerLivraison(20L, req, "koffi@diwa.tg");

            assertEquals(DemandeIntervention.StatutDemande.CLOTURE, demande.getStatut());
        }

        @Test
        @DisplayName("Livraison confirmée — mission passe à TERMINEE")
        void doitTerminerLaMission() {
            service.confirmerLivraison(20L, req, "koffi@diwa.tg");

            assertEquals(MissionChauffeur.StatutMission.TERMINEE, missionLivraison.getStatut());
        }

        @Test
        @DisplayName("Livraison confirmée — photos sauvegardées")
        void doitSauvegarderLesPhotos() {
            service.confirmerLivraison(20L, req, "koffi@diwa.tg");

            assertEquals("https://cdn.diwa.tg/avant.jpg", missionLivraison.getReceptionPhotoAvant());
            assertEquals("https://cdn.diwa.tg/arriere.jpg", missionLivraison.getReceptionPhotoArriere());
        }

        @Test
        @DisplayName("Livraison confirmée — email client envoyé")
        void doitEnvoyerEmailClient() {
            service.confirmerLivraison(20L, req, "koffi@diwa.tg");

            verify(emailService, atLeastOnce()).sendLivraisonConfirmee(
                eq("client@test.tg"), eq("Jean"), eq("Toyota"), eq("TG-1234-AX"));
        }

        @Test
        @DisplayName("Confirmer livraison sur mission RECUPERATION — doit lever IllegalStateException")
        void missionRecuperation_doitEchouer() {
            when(missionRepo.findById(21L)).thenReturn(Optional.of(missionRecuperation));

            assertThrows(IllegalStateException.class,
                () -> service.confirmerLivraison(21L, req, "koffi@diwa.tg"));

            assertEquals(DemandeIntervention.StatutDemande.EN_LIVRAISON, demande.getStatut(),
                "Le statut ne doit pas avoir changé");
        }
    }
}
