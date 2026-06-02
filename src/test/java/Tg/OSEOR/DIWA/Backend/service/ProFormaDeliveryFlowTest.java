package Tg.OSEOR.DIWA.Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.ProFormaDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.*;
import Tg.OSEOR.DIWA.Backend.mappers.ProFormaMapper;
import Tg.OSEOR.DIWA.Backend.repository.*;
import Tg.OSEOR.DIWA.Backend.repository.atelier.*;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import Tg.OSEOR.DIWA.Backend.service.document.DocumentGenerationService;
import Tg.OSEOR.DIWA.Backend.service.media.FileStorageService;

/**
 * Tests unitaires pour le flux complet ProForma avec livraison à domicile.
 *
 * Scénario couvert :
 * SELECTION_RECUE → (livraison) → EN_ATTENTE_CONFIRMATION_FINALE → PROFORMA_VALIDE
 * SELECTION_RECUE → (pas de livraison) → PROFORMA_VALIDE
 */
class ProFormaDeliveryFlowTest {

    @Mock private ProFormaRepository proFormaRepository;
    @Mock private DemandeInterventionRepository demandeRepository;
    @Mock private LigneProFormaTravailRepository ligneTravauxRepository;
    @Mock private LigneProFormaMainOeuvreRepository ligneMainOeuvreRepository;
    @Mock private GammePrixRepository gammePrixRepository;
    @Mock private PieceDetacheeRepository pieceDetacheeRepository;
    @Mock private ParametreAtelierRepository parametreRepo;
    @Mock private UserRepository userRepository;
    @Mock private ProFormaMapper proFormaMapper;
    @Mock private DocumentGenerationService documentGenerationService;
    @Mock private FileStorageService fileStorageService;
    @Mock private EmailService emailService;
    @Mock private HistoriqueStatutDemandeRepository historiqueRepository;
    @Mock private AffectationTravailRepository affectationTravailRepository;
    @Mock private TechnicienRepository technicienRepository;

    @InjectMocks
    private ProFormaServiceModuleImpl service;

    private User receptionniste;
    private User client;
    private DemandeIntervention demande;
    private ProForma proForma;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        receptionniste = new User();
        receptionniste.setId(1L);
        receptionniste.setPrenom("Marie");
        receptionniste.setNom("Dupont");
        receptionniste.setEmail("recep@diwa.tg");
        receptionniste.setUsername("recep@diwa.tg");

        client = new User();
        client.setId(2L);
        client.setPrenom("Jean");
        client.setNom("Client");
        client.setEmail("client@test.tg");
        client.setUsername("client@test.tg");

        demande = new DemandeIntervention();
        demande.setId(10L);
        demande.setClient(client);
        demande.setVehiculeMarque("Toyota");
        demande.setVehiculeImmatriculation("TG-1234-AX");
        demande.setStatut(DemandeIntervention.StatutDemande.SELECTION_RECUE);
        demande.setDemandeLivraison(false);

        proForma = new ProForma();
        proForma.setId(100L);
        proForma.setDemande(demande);
        proForma.setStatut(ProForma.StatutProForma.SELECTION_RECUE);
        proForma.setLignesTravaux(new ArrayList<>());
        proForma.setLignesMainOeuvre(new ArrayList<>());
        proForma.setTotalPieces(new BigDecimal("50000"));
        proForma.setTotalMainOeuvre(new BigDecimal("20000"));
        proForma.setFraisDiagnostic(new BigDecimal("5000"));
        proForma.setFraisRecuperation(BigDecimal.ZERO);
        proForma.setTotalFraisAnnexes(new BigDecimal("5000"));
        proForma.setTotalGeneral(new BigDecimal("75000"));
        demande.setProForma(proForma);

        when(proFormaRepository.findById(100L)).thenReturn(Optional.of(proForma));
        when(userRepository.findByIdentifier("recep@diwa.tg")).thenReturn(Optional.of(receptionniste));
        when(userRepository.findByIdentifier("client@test.tg")).thenReturn(Optional.of(client));
        when(proFormaMapper.toDTO(any())).thenReturn(new ProFormaDTOResponse());
        when(historiqueRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GROUPE 1 : validerSelectionClient
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("validerSelectionClient")
    class ValiderSelectionClientTests {

        @Test
        @DisplayName("Scénario B sans livraison — doit passer à PROFORMA_VALIDE")
        void sansLivraison_doitValider() {
            demande.setDemandeLivraison(false);

            service.validerSelectionClient(100L, "recep@diwa.tg");

            assertEquals(DemandeIntervention.StatutDemande.PROFORMA_VALIDE, demande.getStatut());
            assertEquals(ProForma.StatutProForma.VALIDE_FINAL, proForma.getStatut());
            verify(demandeRepository).save(demande);
        }

        @Test
        @DisplayName("Livraison demandée SANS frais — doit lever 400")
        void avecLivraisonSansFrais_doitEchouer() {
            demande.setDemandeLivraison(true);
            proForma.setFraisLivraison(null);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.validerSelectionClient(100L, "recep@diwa.tg"));

            assertEquals(400, ex.getStatusCode().value());
            assertTrue(ex.getReason().contains("livraison"), "Le message d'erreur doit mentionner la livraison");
            // Statut demande ne doit PAS avoir changé
            assertEquals(DemandeIntervention.StatutDemande.SELECTION_RECUE, demande.getStatut());
        }

        @Test
        @DisplayName("Livraison demandée avec frais à 0 — doit lever 400")
        void avecLivraisonFraisZero_doitEchouer() {
            demande.setDemandeLivraison(true);
            proForma.setFraisLivraison(BigDecimal.ZERO);

            assertThrows(ResponseStatusException.class,
                    () -> service.validerSelectionClient(100L, "recep@diwa.tg"));
        }

        @Test
        @DisplayName("Livraison demandée AVEC frais fixés — doit valider")
        void avecLivraisonAvecFrais_doitValider() {
            demande.setDemandeLivraison(true);
            proForma.setFraisLivraison(new BigDecimal("5000"));

            service.validerSelectionClient(100L, "recep@diwa.tg");

            assertEquals(DemandeIntervention.StatutDemande.PROFORMA_VALIDE, demande.getStatut());
            assertEquals(ProForma.StatutProForma.VALIDE_FINAL, proForma.getStatut());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GROUPE 2 : mettreAJourLivraison
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("mettreAJourLivraison")
    class MettreAJourLivraisonTests {

        @BeforeEach
        void setDelivery() {
            demande.setDemandeLivraison(true);
            demande.setAdresseLivraison("Quartier Bè, Lomé");
        }

        @Test
        @DisplayName("Frais valides — doit passer à EN_ATTENTE_CONFIRMATION_FINALE")
        void fraisValides_doitTransitionnerStatut() {
            service.mettreAJourLivraison(100L, new BigDecimal("8000"), "recep@diwa.tg");

            assertEquals(DemandeIntervention.StatutDemande.EN_ATTENTE_CONFIRMATION_FINALE, demande.getStatut());
            assertEquals(new BigDecimal("8000"), proForma.getFraisLivraison());
            assertEquals(new BigDecimal("8000"), demande.getFraisLivraison());
            verify(demandeRepository).save(demande);
        }

        @Test
        @DisplayName("Total recalculé inclut les frais de livraison")
        void totalRecalculeAvecFraisLivraison() {
            // totalPieces=50000, totalMO=20000, fraisDiag=5000
            // +fraisLivraison=8000 → totalGeneral=83000
            service.mettreAJourLivraison(100L, new BigDecimal("8000"), "recep@diwa.tg");

            assertEquals(new BigDecimal("83000"), proForma.getTotalGeneral());
        }

        @Test
        @DisplayName("Sans livraison demandée — doit lever 400")
        void sansDemandeLivraison_doitEchouer() {
            demande.setDemandeLivraison(false);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.mettreAJourLivraison(100L, new BigDecimal("8000"), "recep@diwa.tg"));

            assertEquals(400, ex.getStatusCode().value());
        }

        @Test
        @DisplayName("Email client est envoyé (attempt)")
        void emailClientEstEnvoye() {
            service.mettreAJourLivraison(100L, new BigDecimal("8000"), "recep@diwa.tg");

            verify(emailService, atLeastOnce()).sendLivraisonFraisAjoutes(
                    eq("client@test.tg"), eq("Jean"),
                    eq("Toyota"), eq("TG-1234-AX"),
                    eq(new BigDecimal("8000")), any());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GROUPE 3 : confirmerFinal (client accepte le devis final)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("confirmerFinal")
    class ConfirmerFinalTests {

        @BeforeEach
        void setAwaitingFinalConfirmation() {
            demande.setStatut(DemandeIntervention.StatutDemande.EN_ATTENTE_CONFIRMATION_FINALE);
        }

        @Test
        @DisplayName("Client confirme — doit passer à PROFORMA_VALIDE")
        void clientConfirme_doitValider() {
            service.confirmerFinal(100L, "client@test.tg");

            assertEquals(DemandeIntervention.StatutDemande.PROFORMA_VALIDE, demande.getStatut());
            assertEquals(ProForma.StatutProForma.VALIDE_FINAL, proForma.getStatut());
        }

        @Test
        @DisplayName("Mauvais statut (pas EN_ATTENTE_CONFIRMATION_FINALE) — doit lever 400")
        void mauvaisStatut_doitEchouer() {
            demande.setStatut(DemandeIntervention.StatutDemande.SELECTION_RECUE);

            assertThrows(ResponseStatusException.class,
                    () -> service.confirmerFinal(100L, "client@test.tg"));

            assertEquals(DemandeIntervention.StatutDemande.SELECTION_RECUE, demande.getStatut());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GROUPE 4 : confirmerProForma (correction bug — était SELECTION_RECUE)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("confirmerProForma — correction statut CONFIRME")
    class ConfirmerProFormaTests {

        @BeforeEach
        void setup() {
            when(documentGenerationService.genererPdf(any(), anyString())).thenReturn(new byte[0]);
            when(fileStorageService.storePdf(any(), anyString())).thenReturn("http://localhost/pdf.pdf");
        }

        @Test
        @DisplayName("Confirmer un proforma doit passer la demande à CONFIRME (pas SELECTION_RECUE)")
        void doitTransitionnerACONFIRME() {
            demande.setStatut(DemandeIntervention.StatutDemande.EN_ATTENTE_CLIENT);

            service.confirmerProForma(100L, "client@test.tg");

            // Correction du bug : était SELECTION_RECUE avant
            assertEquals(DemandeIntervention.StatutDemande.CONFIRME, demande.getStatut(),
                "La demande doit passer à CONFIRME, pas à SELECTION_RECUE");
            assertEquals(ProForma.StatutProForma.CONFIRME_CLIENT, proForma.getStatut());
        }
    }
}
