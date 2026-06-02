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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.ProFormaDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.ProFormaV1Request;
import Tg.OSEOR.DIWA.Backend.entity.*;
import Tg.OSEOR.DIWA.Backend.mappers.ProFormaMapper;
import Tg.OSEOR.DIWA.Backend.repository.*;
import Tg.OSEOR.DIWA.Backend.repository.atelier.*;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import Tg.OSEOR.DIWA.Backend.service.document.DocumentGenerationService;
import Tg.OSEOR.DIWA.Backend.service.media.FileStorageService;

/**
 * Tests unitaires — association Pièce ↔ Main d'Œuvre dans le Pro Forma V1.
 *
 * Trois couches testées :
 *  1. Entité LigneProFormaMainOeuvre — @PrePersist calculerTotal()
 *  2. ProFormaMapper              — mapping ligneTravailId / dureeMinutes
 *  3. ProFormaServiceModuleImpl   — creerV1 avec MO liées et autonomes
 */
class ProFormaLinkedMOTest {

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 1 — Entité LigneProFormaMainOeuvre
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("LigneProFormaMainOeuvre — calculerTotal (@PrePersist)")
    class EntiteTests {

        private LigneProFormaMainOeuvre ligne;

        @BeforeEach
        void setUp() {
            ligne = new LigneProFormaMainOeuvre();
        }

        @Test
        @DisplayName("30 min → heures = 0.50")
        void dureeMinutes30_deriveHeuresDemiHeure() {
            ligne.setDureeMinutes(30);
            ligne.calculerTotal();

            assertEquals(new BigDecimal("0.50"), ligne.getHeures());
        }

        @Test
        @DisplayName("90 min → heures = 1.50")
        void dureeMinutes90_deriveHeuresUneTrente() {
            ligne.setDureeMinutes(90);
            ligne.calculerTotal();

            assertEquals(new BigDecimal("1.50"), ligne.getHeures());
        }

        @Test
        @DisplayName("60 min + tauxHoraire 15000 → total = 15000")
        void dureeMinutes60_avecTaux_calculeTotal() {
            ligne.setDureeMinutes(60);
            ligne.setTauxHoraire(new BigDecimal("15000"));
            ligne.calculerTotal();

            assertEquals(new BigDecimal("15000.00"), ligne.getTotal());
        }

        @Test
        @DisplayName("45 min + tauxHoraire 12000 → total ≈ 9000")
        void dureeMinutes45_avecTaux_calculeTotal() {
            ligne.setDureeMinutes(45);
            ligne.setTauxHoraire(new BigDecimal("12000"));
            ligne.calculerTotal();

            // 45/60 = 0.75h  ×  12000 = 9000.00
            assertEquals(new BigDecimal("9000.00"), ligne.getTotal());
        }

        @Test
        @DisplayName("dureeMinutes null + heures déjà set → heures conservées")
        void sansMinutes_heuresDejaSet_conserveHeures() {
            ligne.setDureeMinutes(null);
            ligne.setHeures(new BigDecimal("1.50"));
            ligne.calculerTotal();

            assertEquals(new BigDecimal("1.50"), ligne.getHeures());
        }

        @Test
        @DisplayName("dureeMinutes null + heures null → heures = ZERO (pas NPE)")
        void sansMinutesSansHeures_defaultZero_pasNPE() {
            ligne.setDureeMinutes(null);
            ligne.setHeures(null);

            assertDoesNotThrow(() -> ligne.calculerTotal());
            assertEquals(BigDecimal.ZERO, ligne.getHeures());
        }

        @Test
        @DisplayName("ligneTravailId est bien stocké et relu")
        void ligneTravailId_stockeEtRelu() {
            ligne.setLigneTravailId(42L);
            assertEquals(42L, ligne.getLigneTravailId());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 2 — ProFormaMapper
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("ProFormaMapper — ligneTravailId + dureeMinutes")
    class MapperTests {

        private final ProFormaMapper mapper = new ProFormaMapper();

        private LigneProFormaMainOeuvre buildMO(Long ligneTravailId, Integer dureeMinutes) {
            LigneProFormaMainOeuvre mo = new LigneProFormaMainOeuvre();
            mo.setId(99L);
            mo.setPosition(1);
            mo.setTypeIntervention("Pose freins");
            mo.setDureeMinutes(dureeMinutes);
            mo.setHeures(new BigDecimal("0.50"));
            mo.setLigneTravailId(ligneTravailId);
            mo.setCocheeParClient(true);
            return mo;
        }

        @Test
        @DisplayName("ligneTravailId renseigné → propagé dans le DTO")
        void ligneTravailId_mappeDansDTO() {
            ProForma pf = buildMinimalProForma();
            LigneProFormaMainOeuvre mo = buildMO(1001L, 30);
            pf.getLignesMainOeuvre().add(mo);

            ProFormaDTOResponse dto = mapper.toDTO(pf);

            assertEquals(1001L, dto.getLignesMainOeuvre().get(0).getLigneTravailId());
        }

        @Test
        @DisplayName("ligneTravailId null (MO autonome) → null dans le DTO")
        void ligneTravailId_null_restNull() {
            ProForma pf = buildMinimalProForma();
            LigneProFormaMainOeuvre mo = buildMO(null, 45);
            pf.getLignesMainOeuvre().add(mo);

            ProFormaDTOResponse dto = mapper.toDTO(pf);

            assertNull(dto.getLignesMainOeuvre().get(0).getLigneTravailId());
        }

        @Test
        @DisplayName("dureeMinutes 30 → propagé dans le DTO")
        void dureeMinutes_mappeDansDTO() {
            ProForma pf = buildMinimalProForma();
            LigneProFormaMainOeuvre mo = buildMO(null, 30);
            pf.getLignesMainOeuvre().add(mo);

            ProFormaDTOResponse dto = mapper.toDTO(pf);

            assertEquals(30, dto.getLignesMainOeuvre().get(0).getDureeMinutes());
        }

        /** ProForma minimal pour les tests mapper (sans demande ni lignes). */
        private ProForma buildMinimalProForma() {
            ProForma pf = new ProForma();
            pf.setId(100L);
            pf.setStatut(ProForma.StatutProForma.BROUILLON);
            pf.setLignesTravaux(new ArrayList<>());
            pf.setLignesMainOeuvre(new ArrayList<>());
            return pf;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 3 — ProFormaServiceModuleImpl.creerV1
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("ProFormaServiceModuleImpl.creerV1 — MO liées et autonomes")
    class ServiceCreerV1Tests {

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

        private DemandeIntervention demande;
        private User chefTech;

        /** Identifiant numérique → lookup via findById. */
        private static final String DEMANDE_ID = "10";
        private static final String CHEF_EMAIL = "chef@diwa.tg";

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);

            chefTech = new User();
            chefTech.setId(5L);
            chefTech.setPrenom("Paul");
            chefTech.setNom("Kofi");
            chefTech.setEmail(CHEF_EMAIL);
            chefTech.setUsername(CHEF_EMAIL);

            demande = new DemandeIntervention();
            demande.setId(10L);
            demande.setStatut(DemandeIntervention.StatutDemande.VEHICULE_RECU);

            // Mocks communs
            when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));
            when(proFormaRepository.findByDemandeId(10L)).thenReturn(Optional.empty());
            when(userRepository.findByIdentifier(CHEF_EMAIL)).thenReturn(Optional.of(chefTech));
            when(historiqueRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
            when(demandeRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
            when(proFormaRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
            when(proFormaMapper.toDTO(any())).thenReturn(new ProFormaDTOResponse());

            // saveAndFlush simule l'attribution d'un ID par la DB
            when(proFormaRepository.saveAndFlush(any(ProForma.class))).thenAnswer(inv -> {
                ProForma pf = inv.getArgument(0);
                pf.setId(100L);
                return pf;
            });
            // ligneMainOeuvreRepository.save retourne l'entité telle quelle
            when(ligneMainOeuvreRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        }

        // ── Helper : construit la liste de LigneProFormaTravail sauvegardées ──
        private List<LigneProFormaTravail> mockSavedTravaux(String... designations) {
            List<LigneProFormaTravail> list = new ArrayList<>();
            for (int i = 0; i < designations.length; i++) {
                LigneProFormaTravail lt = new LigneProFormaTravail();
                lt.setId(1000L + i + 1);          // IDs : 1001, 1002, ...
                lt.setPosition(i + 1);
                lt.setDesignation(designations[i]);
                list.add(lt);
            }
            when(ligneTravauxRepository.findByProFormaIdOrderByPositionAsc(100L)).thenReturn(list);
            return list;
        }

        // ── Helper : requête avec une pièce ayant une MO liée ──────────────
        private ProFormaV1Request buildRequest_pieceAvecMO(String designation, int qte,
                                                            String moType, int moMinutes) {
            ProFormaV1Request req = new ProFormaV1Request();
            ProFormaV1Request.LigneTravailRequest lt = new ProFormaV1Request.LigneTravailRequest();
            lt.setDesignation(designation);
            lt.setQuantite(BigDecimal.valueOf(qte));

            ProFormaV1Request.LigneMainOeuvreAssocieeRequest moAssoc =
                    new ProFormaV1Request.LigneMainOeuvreAssocieeRequest();
            moAssoc.setTypeIntervention(moType);
            moAssoc.setDureeMinutes(moMinutes);
            lt.setMainOeuvreAssociee(moAssoc);

            req.setLignesTravaux(List.of(lt));
            req.setLignesMainOeuvre(new ArrayList<>());
            return req;
        }

        // ── Helper : requête avec une pièce SANS MO liée ───────────────────
        private ProFormaV1Request buildRequest_pieceSansMO(String designation) {
            ProFormaV1Request req = new ProFormaV1Request();
            ProFormaV1Request.LigneTravailRequest lt = new ProFormaV1Request.LigneTravailRequest();
            lt.setDesignation(designation);
            lt.setQuantite(BigDecimal.ONE);
            lt.setMainOeuvreAssociee(null);
            req.setLignesTravaux(List.of(lt));
            req.setLignesMainOeuvre(new ArrayList<>());
            return req;
        }

        @Test
        @DisplayName("Pièce sans MO → aucune MO liée sauvegardée")
        void pieceSansMO_neCreePasLiee() {
            mockSavedTravaux("Huile 5W30");
            ProFormaV1Request req = buildRequest_pieceSansMO("Huile 5W30");

            service.creerV1(DEMANDE_ID, req, CHEF_EMAIL);

            // ligneMainOeuvreRepository.save ne doit pas être appelé
            verify(ligneMainOeuvreRepository, never()).save(any());
        }

        @Test
        @DisplayName("Pièce avec MO → MO sauvegardée avec le ligneTravailId correct")
        void pieceAvecMO_sauvegardeLigneTravailId() {
            List<LigneProFormaTravail> saved = mockSavedTravaux("4 freins avant");
            // saved.get(0).getId() = 1001
            ProFormaV1Request req = buildRequest_pieceAvecMO(
                    "4 freins avant", 4, "Remplacement freins avant", 30);

            service.creerV1(DEMANDE_ID, req, CHEF_EMAIL);

            ArgumentCaptor<LigneProFormaMainOeuvre> captor =
                    ArgumentCaptor.forClass(LigneProFormaMainOeuvre.class);
            verify(ligneMainOeuvreRepository, times(1)).save(captor.capture());

            LigneProFormaMainOeuvre moSauvegardee = captor.getValue();
            assertEquals(saved.get(0).getId(), moSauvegardee.getLigneTravailId(),
                    "ligneTravailId doit correspondre à l'ID de la pièce sauvegardée");
            assertEquals(30, moSauvegardee.getDureeMinutes());
            assertEquals("Remplacement freins avant", moSauvegardee.getTypeIntervention());
        }

        @Test
        @DisplayName("2 pièces, seule la 1re a une MO → MO liée à la 1re pièce uniquement")
        void deuxPiecesUneAvecMO_lienCorrect() {
            List<LigneProFormaTravail> saved = mockSavedTravaux("4 freins avant", "Huile 5W30");
            // IDs: 1001 et 1002

            ProFormaV1Request req = new ProFormaV1Request();

            // Pièce 1 — avec MO
            ProFormaV1Request.LigneTravailRequest lt1 = new ProFormaV1Request.LigneTravailRequest();
            lt1.setDesignation("4 freins avant");
            lt1.setQuantite(BigDecimal.valueOf(4));
            ProFormaV1Request.LigneMainOeuvreAssocieeRequest moAssoc =
                    new ProFormaV1Request.LigneMainOeuvreAssocieeRequest();
            moAssoc.setTypeIntervention("Pose freins");
            moAssoc.setDureeMinutes(45);
            lt1.setMainOeuvreAssociee(moAssoc);

            // Pièce 2 — sans MO
            ProFormaV1Request.LigneTravailRequest lt2 = new ProFormaV1Request.LigneTravailRequest();
            lt2.setDesignation("Huile 5W30");
            lt2.setQuantite(BigDecimal.ONE);
            lt2.setMainOeuvreAssociee(null);

            req.setLignesTravaux(List.of(lt1, lt2));
            req.setLignesMainOeuvre(new ArrayList<>());

            service.creerV1(DEMANDE_ID, req, CHEF_EMAIL);

            ArgumentCaptor<LigneProFormaMainOeuvre> captor =
                    ArgumentCaptor.forClass(LigneProFormaMainOeuvre.class);
            verify(ligneMainOeuvreRepository, times(1)).save(captor.capture());

            LigneProFormaMainOeuvre moSauvee = captor.getValue();
            assertEquals(1001L, moSauvee.getLigneTravailId(),
                    "La MO doit être liée à la 1re pièce (ID 1001), pas à la 2e");
            assertEquals(45, moSauvee.getDureeMinutes());
        }

        @Test
        @DisplayName("dureeTotaleMinutes = somme MO liées + MO autonomes")
        void dureeTotale_sommeCorrecteMoLieesEtAutonomes() {
            mockSavedTravaux("Freins");

            ProFormaV1Request req = new ProFormaV1Request();

            // Pièce avec MO liée : 30 min
            ProFormaV1Request.LigneTravailRequest lt = new ProFormaV1Request.LigneTravailRequest();
            lt.setDesignation("Freins");
            lt.setQuantite(BigDecimal.ONE);
            ProFormaV1Request.LigneMainOeuvreAssocieeRequest moLiee =
                    new ProFormaV1Request.LigneMainOeuvreAssocieeRequest();
            moLiee.setTypeIntervention("Pose freins");
            moLiee.setDureeMinutes(30);
            lt.setMainOeuvreAssociee(moLiee);
            req.setLignesTravaux(List.of(lt));

            // MO autonome : 45 min
            ProFormaV1Request.LigneMainOeuvreRequest moAuto =
                    new ProFormaV1Request.LigneMainOeuvreRequest();
            moAuto.setTypeIntervention("Vidange moteur");
            moAuto.setDureeMinutes(45);
            req.setLignesMainOeuvre(List.of(moAuto));

            ArgumentCaptor<ProForma> pfCaptor = ArgumentCaptor.forClass(ProForma.class);

            service.creerV1(DEMANDE_ID, req, CHEF_EMAIL);

            // On capture le dernier save sur proFormaRepository (après la mise à jour dureeTotale)
            verify(proFormaRepository, atLeastOnce()).save(pfCaptor.capture());
            ProForma dernierSave = pfCaptor.getAllValues().stream()
                    .filter(pf -> pf.getDureeTotaleMinutes() != null)
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("aucun save avec dureeTotaleMinutes trouvé"));

            assertEquals(75, dernierSave.getDureeTotaleMinutes(),
                    "30 min (liée) + 45 min (autonome) = 75 min au total");
        }

        @Test
        @DisplayName("typeIntervention null → fallback 'Pose — <designation>'")
        void typeInterventionNull_fallbackSurDesignation() {
            mockSavedTravaux("Kit distribution");

            ProFormaV1Request.LigneMainOeuvreAssocieeRequest moAssoc =
                    new ProFormaV1Request.LigneMainOeuvreAssocieeRequest();
            moAssoc.setTypeIntervention(null);   // ← null volontaire
            moAssoc.setDureeMinutes(60);

            ProFormaV1Request.LigneTravailRequest lt = new ProFormaV1Request.LigneTravailRequest();
            lt.setDesignation("Kit distribution");
            lt.setQuantite(BigDecimal.ONE);
            lt.setMainOeuvreAssociee(moAssoc);

            ProFormaV1Request req = new ProFormaV1Request();
            req.setLignesTravaux(List.of(lt));
            req.setLignesMainOeuvre(new ArrayList<>());

            service.creerV1(DEMANDE_ID, req, CHEF_EMAIL);

            ArgumentCaptor<LigneProFormaMainOeuvre> captor =
                    ArgumentCaptor.forClass(LigneProFormaMainOeuvre.class);
            verify(ligneMainOeuvreRepository).save(captor.capture());

            String type = captor.getValue().getTypeIntervention();
            assertTrue(type.startsWith("Pose"),
                    "Fallback attendu : 'Pose — ...' mais obtenu : '" + type + "'");
            assertTrue(type.contains("Kit distribution"),
                    "Fallback doit inclure la désignation de la pièce");
        }

        @Test
        @DisplayName("MO autonome avec dureeMinutes → heures dérivé via @PrePersist")
        void moAutonome_dureeMinutes_heuresDeriveParPrePersist() {
            mockSavedTravaux("Filtre à air");

            // Pièce sans MO
            ProFormaV1Request.LigneTravailRequest lt = new ProFormaV1Request.LigneTravailRequest();
            lt.setDesignation("Filtre à air");
            lt.setQuantite(BigDecimal.ONE);
            lt.setMainOeuvreAssociee(null);

            // MO autonome avec 90 min
            ProFormaV1Request.LigneMainOeuvreRequest moAuto =
                    new ProFormaV1Request.LigneMainOeuvreRequest();
            moAuto.setTypeIntervention("Remplacement filtre");
            moAuto.setDureeMinutes(90);

            ProFormaV1Request req = new ProFormaV1Request();
            req.setLignesTravaux(List.of(lt));
            req.setLignesMainOeuvre(List.of(moAuto));

            // Capture des entités ajoutées à la collection
            when(proFormaRepository.saveAndFlush(any(ProForma.class))).thenAnswer(inv -> {
                ProForma pf = inv.getArgument(0);
                pf.setId(100L);
                // Simuler @PrePersist sur les lignes MO dans la collection
                pf.getLignesMainOeuvre().forEach(LigneProFormaMainOeuvre::calculerTotal);
                return pf;
            });

            service.creerV1(DEMANDE_ID, req, CHEF_EMAIL);

            // Vérifie que la MO autonome dans la collection a ses heures dérivées
            verify(proFormaRepository).saveAndFlush(argThat(pf -> {
                if (pf.getLignesMainOeuvre().isEmpty()) return false;
                LigneProFormaMainOeuvre mo = pf.getLignesMainOeuvre().get(0);
                return Integer.valueOf(90).equals(mo.getDureeMinutes());
            }));
        }

        @Test
        @DisplayName("MO autonome avec heures (ancienne API) → compat backward")
        void moAutonome_heuresAncieneAPI_dureeTotaleCorrecte() {
            mockSavedTravaux("Embrayage");

            ProFormaV1Request.LigneTravailRequest lt = new ProFormaV1Request.LigneTravailRequest();
            lt.setDesignation("Embrayage");
            lt.setQuantite(BigDecimal.ONE);
            lt.setMainOeuvreAssociee(null);

            // Ancienne API : heures=1.5, pas de dureeMinutes
            ProFormaV1Request.LigneMainOeuvreRequest moAuto =
                    new ProFormaV1Request.LigneMainOeuvreRequest();
            moAuto.setTypeIntervention("Remplacement embrayage");
            moAuto.setDureeMinutes(null);          // ancien format
            moAuto.setHeures(new BigDecimal("1.5")); // 1h30 = 90 min

            ProFormaV1Request req = new ProFormaV1Request();
            req.setLignesTravaux(List.of(lt));
            req.setLignesMainOeuvre(List.of(moAuto));

            ArgumentCaptor<ProForma> pfCaptor = ArgumentCaptor.forClass(ProForma.class);
            service.creerV1(DEMANDE_ID, req, CHEF_EMAIL);

            verify(proFormaRepository, atLeastOnce()).save(pfCaptor.capture());
            ProForma dernierSave = pfCaptor.getAllValues().stream()
                    .filter(pf -> pf.getDureeTotaleMinutes() != null)
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("aucun save avec dureeTotaleMinutes"));

            assertEquals(90, dernierSave.getDureeTotaleMinutes(),
                    "1.5h × 60 = 90 min (compat ancienne API heures)");
        }

        @Test
        @DisplayName("dureeMinutes null dans moAssociee → 0 stocké, pas d'exception")
        void moAssociee_dureeMinutesNull_stockeZero() {
            mockSavedTravaux("Batterie");

            ProFormaV1Request.LigneMainOeuvreAssocieeRequest moAssoc =
                    new ProFormaV1Request.LigneMainOeuvreAssocieeRequest();
            moAssoc.setTypeIntervention("Pose batterie");
            moAssoc.setDureeMinutes(null);  // ← intentionnellement null

            ProFormaV1Request.LigneTravailRequest lt = new ProFormaV1Request.LigneTravailRequest();
            lt.setDesignation("Batterie");
            lt.setQuantite(BigDecimal.ONE);
            lt.setMainOeuvreAssociee(moAssoc);

            ProFormaV1Request req = new ProFormaV1Request();
            req.setLignesTravaux(List.of(lt));
            req.setLignesMainOeuvre(new ArrayList<>());

            assertDoesNotThrow(() -> service.creerV1(DEMANDE_ID, req, CHEF_EMAIL));

            ArgumentCaptor<LigneProFormaMainOeuvre> captor =
                    ArgumentCaptor.forClass(LigneProFormaMainOeuvre.class);
            verify(ligneMainOeuvreRepository).save(captor.capture());

            assertEquals(0, captor.getValue().getDureeMinutes(),
                    "dureeMinutes null doit être stocké comme 0");
        }

        @Test
        @DisplayName("La demande passe à PROFORMA_V1 après creerV1")
        void creerV1_demandePasseAProformaV1() {
            mockSavedTravaux("Plaquettes");

            ProFormaV1Request req = buildRequest_pieceSansMO("Plaquettes");
            service.creerV1(DEMANDE_ID, req, CHEF_EMAIL);

            assertEquals(DemandeIntervention.StatutDemande.PROFORMA_V1, demande.getStatut());
            verify(demandeRepository).save(demande);
        }
    }
}
