package Tg.OSEOR.DIWA.Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.entity.ProForma;
import Tg.OSEOR.DIWA.Backend.repository.DemandeInterventionRepository;
import Tg.OSEOR.DIWA.Backend.repository.ProFormaRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.model.Role;
import Tg.OSEOR.DIWA.Backend.security.enums.ERole;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import Tg.OSEOR.DIWA.Backend.service.document.DocumentGenerationService;
import Tg.OSEOR.DIWA.Backend.service.media.FileStorageService;

/**
 * Sprint 6 — Tests unitaires pour la facturation finale à la CLOTURE.
 *
 * Scénarios couverts :
 *  [generer]
 *  1. Happy path : demande CLOTURE avec ProForma → PDF généré, URL stockée
 *  2. Demande sans ProForma → PDF généré (sans lignes), pas d'erreur
 *  3. Demande introuvable → RuntimeException
 *  4. Email envoyé quand client a un email
 *  5. Email ignoré si client null (pas d'exception)
 *
 *  [telecharger]
 *  6. Client propriétaire → PDF retourné (depuis disque si facture existe)
 *  7. Staff (réceptionniste) → PDF retourné
 *  8. Client non-propriétaire → SecurityException
 *  9. Utilisateur introuvable → RuntimeException
 * 10. Facture déjà générée mais fichier absent → régénération à la volée
 * 11. Facture non encore générée → génération à la volée
 */
@DisplayName("Sprint 6 — Facturation finale CLOTURE")
class FactureClotureTest {

    @Mock private DemandeInterventionRepository demandeRepo;
    @Mock private ProFormaRepository            proFormaRepo;
    @Mock private UserRepository                userRepo;
    @Mock private DocumentGenerationService     docService;
    @Mock private FileStorageService            fileService;
    @Mock private EmailService                  emailService;

    @InjectMocks
    private FactureServiceImpl factureService;

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private User client;
    private User receptionniste;
    private User autreClient;
    private DemandeIntervention demande;
    private ProForma proForma;
    private static final byte[] PDF_BYTES = new byte[]{1, 2, 3};

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        client = new User();
        client.setId(1L);
        client.setPrenom("Ama");
        client.setNom("Kofi");
        client.setEmail("ama@test.tg");
        client.setUsername("ama@test.tg");

        autreClient = new User();
        autreClient.setId(99L);
        autreClient.setEmail("intrus@test.tg");
        autreClient.setUsername("intrus@test.tg");

        receptionniste = new User();
        receptionniste.setId(10L);
        receptionniste.setEmail("recep@diwa.tg");
        receptionniste.setUsername("recep@diwa.tg");
        Role roleRecep = new Role();
        roleRecep.setName(ERole.ROLE_RECEPTIONNISTE);
        receptionniste.setRoles(java.util.Set.of(roleRecep));

        proForma = new ProForma();
        proForma.setId(5L);
        proForma.setReference("PF-2026-00001");
        proForma.setTotalPieces(new BigDecimal("150000"));
        proForma.setTotalMainOeuvre(new BigDecimal("50000"));
        proForma.setFraisDiagnostic(new BigDecimal("10000"));
        proForma.setTotalGeneral(new BigDecimal("210000"));
        proForma.setDiagnosticGratuit(false);

        demande = new DemandeIntervention();
        demande.setId(42L);
        demande.setReference("DEM-2026-00042");
        demande.setClient(client);
        demande.setVehiculeMarque("Toyota");
        demande.setVehiculeModele("Corolla");
        demande.setVehiculeImmatriculation("TG-1234-AB");
        demande.setStatut(DemandeIntervention.StatutDemande.CLOTURE);
        demande.setProForma(proForma);

        // Stubs par défaut
        when(docService.genererFactureCloture(any())).thenReturn(PDF_BYTES);
        when(fileService.storeFacture(any(), anyString())).thenReturn("factures/DEM-2026-00042_facture.pdf");
        when(proFormaRepo.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(fileService.readFile(anyString())).thenReturn(PDF_BYTES);
    }

    // =========================================================================
    // GROUPE 1 : generer()
    // =========================================================================
    @Nested
    @DisplayName("generer(demandeId)")
    class GenererTests {

        @Test
        @DisplayName("1. Happy path — demande CLOTURE → PDF généré, URL stockée sur ProForma")
        void happyPath_pdfGenereUrlStockee() {
            when(demandeRepo.findById(42L)).thenReturn(Optional.of(demande));

            byte[] result = factureService.generer(42L);

            assertArrayEquals(PDF_BYTES, result, "Le PDF généré doit être retourné");
            verify(docService).genererFactureCloture(demande);
            verify(fileService).storeFacture(eq(PDF_BYTES), anyString());
            verify(proFormaRepo).save(argThat(pf -> pf.getFactureUrl() != null));
        }

        @Test
        @DisplayName("2. Demande sans ProForma → PDF généré sans NPE")
        void sanProForma_pasDeNPE() {
            demande.setProForma(null);
            when(demandeRepo.findById(42L)).thenReturn(Optional.of(demande));

            assertDoesNotThrow(() -> factureService.generer(42L),
                "Pas d'exception même sans ProForma");

            verify(docService).genererFactureCloture(demande);
        }

        @Test
        @DisplayName("3. Demande introuvable → RuntimeException")
        void demandeIntrouvable_exception() {
            when(demandeRepo.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> factureService.generer(999L));
        }

        @Test
        @DisplayName("4. Email envoyé si client a une adresse email")
        void emailEnvoye_quandClientPresent() {
            when(demandeRepo.findById(42L)).thenReturn(Optional.of(demande));

            factureService.generer(42L);

            verify(emailService).sendFactureCloture(
                eq("ama@test.tg"),
                eq("Ama"),
                eq("Toyota"),
                eq("TG-1234-AB"),
                eq("DEM-2026-00042"),
                eq(PDF_BYTES)
            );
        }

        @Test
        @DisplayName("5. Email ignoré si client null — pas d'exception propagée")
        void emailIgnore_quandClientNull() {
            demande.setClient(null);
            when(demandeRepo.findById(42L)).thenReturn(Optional.of(demande));

            assertDoesNotThrow(() -> factureService.generer(42L));
            verify(emailService, never()).sendFactureCloture(any(), any(), any(), any(), any(), any());
        }
    }

    // =========================================================================
    // GROUPE 2 : telecharger()
    // =========================================================================
    @Nested
    @DisplayName("telecharger(demandeId, identifier)")
    class TelechargerTests {

        @Test
        @DisplayName("6. Client propriétaire + facture existante → PDF lu depuis disque")
        void clientProprietaire_factureExistante_lueDepuisDisque() {
            proForma.setFactureUrl("factures/DEM-2026-00042_facture.pdf");
            when(demandeRepo.findById(42L)).thenReturn(Optional.of(demande));
            when(userRepo.findByIdentifier("ama@test.tg")).thenReturn(Optional.of(client));

            byte[] result = factureService.telecharger(42L, "ama@test.tg");

            assertArrayEquals(PDF_BYTES, result);
            verify(fileService).readFile("factures/DEM-2026-00042_facture.pdf");
            // Pas de régénération
            verify(docService, never()).genererFactureCloture(any());
        }

        @Test
        @DisplayName("7. Staff réceptionniste → accès autorisé")
        void staff_receptionniste_acces() {
            proForma.setFactureUrl("factures/DEM-2026-00042_facture.pdf");
            when(demandeRepo.findById(42L)).thenReturn(Optional.of(demande));
            when(userRepo.findByIdentifier("recep@diwa.tg")).thenReturn(Optional.of(receptionniste));

            assertDoesNotThrow(() -> factureService.telecharger(42L, "recep@diwa.tg"));
        }

        @Test
        @DisplayName("8. Client non-propriétaire → SecurityException")
        void autreClient_securityException() {
            when(demandeRepo.findById(42L)).thenReturn(Optional.of(demande));
            when(userRepo.findByIdentifier("intrus@test.tg")).thenReturn(Optional.of(autreClient));

            assertThrows(SecurityException.class,
                () -> factureService.telecharger(42L, "intrus@test.tg"),
                "Client non-propriétaire doit recevoir SecurityException");
        }

        @Test
        @DisplayName("9. Utilisateur introuvable → RuntimeException")
        void utilisateurIntrouvable_exception() {
            when(demandeRepo.findById(42L)).thenReturn(Optional.of(demande));
            when(userRepo.findByIdentifier("inconnu@test.tg")).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                () -> factureService.telecharger(42L, "inconnu@test.tg"));
        }

        @Test
        @DisplayName("10. Fichier absent (factureUrl renseigné mais readFile échoue) → régénération à la volée")
        void fichierAbsent_regeneration() {
            proForma.setFactureUrl("factures/DEM-2026-00042_facture.pdf");
            when(demandeRepo.findById(42L)).thenReturn(Optional.of(demande));
            when(userRepo.findByIdentifier("ama@test.tg")).thenReturn(Optional.of(client));
            when(fileService.readFile(anyString())).thenThrow(new RuntimeException("Fichier absent"));

            byte[] result = factureService.telecharger(42L, "ama@test.tg");

            assertArrayEquals(PDF_BYTES, result, "PDF régénéré à la volée");
            verify(docService).genererFactureCloture(demande);
        }

        @Test
        @DisplayName("11. Facture non encore générée (factureUrl null) → génération à la volée")
        void factureNonGeneree_generationALaVolee() {
            proForma.setFactureUrl(null);
            when(demandeRepo.findById(42L)).thenReturn(Optional.of(demande));
            when(userRepo.findByIdentifier("ama@test.tg")).thenReturn(Optional.of(client));

            byte[] result = factureService.telecharger(42L, "ama@test.tg");

            assertArrayEquals(PDF_BYTES, result, "PDF généré à la volée si absent");
            verify(docService).genererFactureCloture(demande);
            verify(fileService, never()).readFile(anyString());
        }
    }
}
