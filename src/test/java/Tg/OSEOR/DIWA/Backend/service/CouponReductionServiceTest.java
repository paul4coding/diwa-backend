package Tg.OSEOR.DIWA.Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.AppliquerCouponRequest;
import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.CouponRequest;
import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.CouponResponse;
import Tg.OSEOR.DIWA.Backend.entity.CouponReduction;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.entity.ProForma;
import Tg.OSEOR.DIWA.Backend.repository.CouponReductionRepository;
import Tg.OSEOR.DIWA.Backend.repository.ProFormaRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;

/**
 * Tests unitaires — CouponReductionServiceImpl.
 *
 * Scénarios couverts :
 *  1. Entité CouponReduction — estValide / estExpire / estEpuise
 *  2. Calcul du montant de remise (% et fixe)
 *  3. creerCoupon — validations + persistance
 *  4. verifierCoupon — tous les cas d'erreur + succès
 *  5. appliquerCoupon — application et compteur
 */
class CouponReductionServiceTest {

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 1 — Entité CouponReduction
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("CouponReduction — estValide / estExpire / estEpuise")
    class EntiteTests {

        private CouponReduction coupon;

        @BeforeEach
        void setUp() {
            coupon = new CouponReduction();
            coupon.setCode("TEST10");
            coupon.setValeur(new BigDecimal("10"));
            coupon.setTypeRemise(CouponReduction.TypeRemise.POURCENTAGE);
            coupon.setNbMaxUtilisations(5);
            coupon.setNbUtilisations(0);
            coupon.setActif(true);
        }

        @Test
        @DisplayName("Coupon actif non expiré non épuisé → estValide = true")
        void couponValide_trueQuandToutOk() {
            assertTrue(coupon.estValide());
        }

        @Test
        @DisplayName("Coupon inactif → estValide = false")
        void couponInactif_nonValide() {
            coupon.setActif(false);
            assertFalse(coupon.estValide());
        }

        @Test
        @DisplayName("Date expiration dans le passé → estExpire = true")
        void dateExpirationPassee_estExpire() {
            coupon.setDateExpiration(LocalDateTime.now().minusDays(1));
            assertTrue(coupon.estExpire());
            assertFalse(coupon.estValide());
        }

        @Test
        @DisplayName("Date expiration dans le futur → estExpire = false")
        void dateExpirationFuture_nonExpire() {
            coupon.setDateExpiration(LocalDateTime.now().plusDays(30));
            assertFalse(coupon.estExpire());
            assertTrue(coupon.estValide());
        }

        @Test
        @DisplayName("Utilisations atteintes → estEpuise = true")
        void utilisationsAtteintes_estEpuise() {
            coupon.setNbUtilisations(5);
            coupon.setNbMaxUtilisations(5);
            assertTrue(coupon.estEpuise());
            assertFalse(coupon.estValide());
        }

        @Test
        @DisplayName("setCode normalise en majuscules")
        void setCode_normaliseEnMajuscules() {
            coupon.setCode("diwa10");
            assertEquals("DIWA10", coupon.getCode());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 2 — calculerMontantRemise
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("calculerMontantRemise — pourcentage et montant fixe")
    class CalculRemiseTests {

        private CouponReductionServiceImpl service;

        @BeforeEach
        void setUp() {
            service = new CouponReductionServiceImpl(
                mock(CouponReductionRepository.class),
                mock(ProFormaRepository.class),
                mock(UserRepository.class),
                mock(SimpMessagingTemplate.class)
            );
        }

        @Test
        @DisplayName("10% sur 100 000 → remise = 10 000")
        void pourcentage10_sur100000() {
            CouponReduction c = coupon(CouponReduction.TypeRemise.POURCENTAGE, "10");
            assertEquals(new BigDecimal("10000.00"), service.calculerMontantRemise(c, new BigDecimal("100000")));
        }

        @Test
        @DisplayName("25% sur 80 000 → remise = 20 000")
        void pourcentage25_sur80000() {
            CouponReduction c = coupon(CouponReduction.TypeRemise.POURCENTAGE, "25");
            assertEquals(new BigDecimal("20000.00"), service.calculerMontantRemise(c, new BigDecimal("80000")));
        }

        @Test
        @DisplayName("Montant fixe 5000 sur 80 000 → remise = 5 000")
        void montantFixe5000() {
            CouponReduction c = coupon(CouponReduction.TypeRemise.MONTANT_FIXE, "5000");
            assertEquals(new BigDecimal("5000.00"), service.calculerMontantRemise(c, new BigDecimal("80000")));
        }

        @Test
        @DisplayName("Montant fixe > totalGeneral → plafonné à totalGeneral")
        void montantFixeDepasse_plafonne() {
            CouponReduction c = coupon(CouponReduction.TypeRemise.MONTANT_FIXE, "200000");
            BigDecimal total = new BigDecimal("50000");
            // compareTo ignore les différences de scale (50000 vs 50000.00)
            assertEquals(0, total.compareTo(service.calculerMontantRemise(c, total)),
                    "La remise doit être plafonnée au total général");
        }

        @Test
        @DisplayName("100% → remise = totalGeneral (pas de négatif)")
        void pourcentage100_remiseEgaleTotal() {
            CouponReduction c = coupon(CouponReduction.TypeRemise.POURCENTAGE, "100");
            BigDecimal total = new BigDecimal("75000");
            assertEquals(0, total.compareTo(service.calculerMontantRemise(c, total)),
                    "100% doit retourner exactement le totalGeneral");
        }

        @Test
        @DisplayName("totalGeneral null ou zéro → remise = 0")
        void totalNullOuZero_remiseZero() {
            CouponReduction c = coupon(CouponReduction.TypeRemise.POURCENTAGE, "10");
            assertEquals(BigDecimal.ZERO, service.calculerMontantRemise(c, null));
            assertEquals(BigDecimal.ZERO, service.calculerMontantRemise(c, BigDecimal.ZERO));
        }

        private CouponReduction coupon(CouponReduction.TypeRemise type, String valeur) {
            CouponReduction c = new CouponReduction();
            c.setTypeRemise(type);
            c.setValeur(new BigDecimal(valeur));
            return c;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 3 — creerCoupon
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("creerCoupon — validations et persistance")
    class CreerCouponTests {

        @Mock private CouponReductionRepository couponRepo;
        @Mock private ProFormaRepository proFormaRepo;
        @Mock private UserRepository userRepo;
        @Mock private SimpMessagingTemplate messaging;

        @InjectMocks private CouponReductionServiceImpl service;

        private User receptionniste;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            receptionniste = new User();
            receptionniste.setId(1L);
            receptionniste.setPrenom("Marie");
            receptionniste.setNom("Dupont");
            when(userRepo.findByIdentifier("recep@diwa.tg")).thenReturn(Optional.of(receptionniste));
            when(couponRepo.existsByCode(anyString())).thenReturn(false);
            when(couponRepo.save(any())).thenAnswer(i -> {
                CouponReduction c = i.getArgument(0);
                c.setId(1L);
                return c;
            });
        }

        @Test
        @DisplayName("Coupon POURCENTAGE valide → créé avec code en majuscules")
        void couponPourcentage_creeSuces() {
            CouponRequest req = buildRequest("diwa10", "POURCENTAGE", "10", 5);

            CouponResponse resp = service.creerCoupon(req, "recep@diwa.tg");

            assertEquals("DIWA10", resp.getCode());
            assertEquals("POURCENTAGE", resp.getTypeRemise());
            assertEquals(0, resp.getNbUtilisations());
            assertTrue(resp.isActif());
            assertTrue(resp.isValide());
        }

        @Test
        @DisplayName("Coupon MONTANT_FIXE valide → créé correctement")
        void couponMontantFixe_creeSuces() {
            CouponRequest req = buildRequest("VIP50K", "MONTANT_FIXE", "50000", 1);

            CouponResponse resp = service.creerCoupon(req, "recep@diwa.tg");

            assertEquals("VIP50K", resp.getCode());
            assertEquals("MONTANT_FIXE", resp.getTypeRemise());
            assertEquals(new BigDecimal("50000"), resp.getValeur());
        }

        @Test
        @DisplayName("Code déjà existant → 409 CONFLICT")
        void codeExistant_retourne409() {
            when(couponRepo.existsByCode("DIWA10")).thenReturn(true);
            CouponRequest req = buildRequest("DIWA10", "POURCENTAGE", "10", 1);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.creerCoupon(req, "recep@diwa.tg"));
            assertEquals(409, ex.getStatusCode().value());
        }

        @Test
        @DisplayName("Valeur nulle → 400 BAD REQUEST")
        void valeurNulle_retourne400() {
            CouponRequest req = buildRequest("TEST", "POURCENTAGE", null, 1);

            assertThrows(ResponseStatusException.class,
                    () -> service.creerCoupon(req, "recep@diwa.tg"));
        }

        @Test
        @DisplayName("Pourcentage > 100 → 400 BAD REQUEST")
        void pourcentageSup100_retourne400() {
            CouponRequest req = buildRequest("OVER", "POURCENTAGE", "150", 1);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.creerCoupon(req, "recep@diwa.tg"));
            assertEquals(400, ex.getStatusCode().value());
        }

        private CouponRequest buildRequest(String code, String type, String valeur, int maxUt) {
            CouponRequest r = new CouponRequest();
            r.setCode(code);
            r.setTypeRemise(type);
            r.setValeur(valeur != null ? new BigDecimal(valeur) : null);
            r.setNbMaxUtilisations(maxUt);
            return r;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 4 — verifierCoupon
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verifierCoupon — tous les cas d'erreur + succès")
    class VerifierCouponTests {

        @Mock private CouponReductionRepository couponRepo;
        @Mock private ProFormaRepository proFormaRepo;
        @Mock private UserRepository userRepo;
        @Mock private SimpMessagingTemplate messaging;

        @InjectMocks private CouponReductionServiceImpl service;

        private ProForma proForma;
        private CouponReduction coupon;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);

            proForma = new ProForma();
            proForma.setId(100L);
            proForma.setTotalGeneral(new BigDecimal("80000"));

            coupon = new CouponReduction();
            coupon.setId(10L);
            coupon.setCode("DIWA10");
            coupon.setValeur(new BigDecimal("10"));
            coupon.setTypeRemise(CouponReduction.TypeRemise.POURCENTAGE);
            coupon.setNbMaxUtilisations(5);
            coupon.setNbUtilisations(0);
            coupon.setActif(true);

            when(couponRepo.findByCode("DIWA10")).thenReturn(Optional.of(coupon));
            when(proFormaRepo.findById(100L)).thenReturn(Optional.of(proForma));
        }

        @Test
        @DisplayName("Coupon valide → retourne remise estimée correcte")
        void couponValide_retourneRemise() {
            CouponResponse resp = service.verifierCoupon("DIWA10", 100L);

            assertEquals("DIWA10", resp.getCode());
            assertEquals(new BigDecimal("8000.00"), resp.getMontantRemise());
            assertEquals(new BigDecimal("72000.00"), resp.getTotalApresRemise());
        }

        @Test
        @DisplayName("Coupon introuvable → 404")
        void couponIntrouvable_404() {
            when(couponRepo.findByCode("INCONNU")).thenReturn(Optional.empty());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.verifierCoupon("INCONNU", 100L));
            assertEquals(404, ex.getStatusCode().value());
        }

        @Test
        @DisplayName("Coupon inactif → 400")
        void couponInactif_400() {
            coupon.setActif(false);
            assertThrows400(() -> service.verifierCoupon("DIWA10", 100L));
        }

        @Test
        @DisplayName("Coupon expiré → 400")
        void couponExpire_400() {
            coupon.setDateExpiration(LocalDateTime.now().minusHours(1));
            assertThrows400(() -> service.verifierCoupon("DIWA10", 100L));
        }

        @Test
        @DisplayName("Coupon épuisé → 400")
        void couponEpuise_400() {
            coupon.setNbUtilisations(5);
            assertThrows400(() -> service.verifierCoupon("DIWA10", 100L));
        }

        @Test
        @DisplayName("Coupon lié à un autre pro-forma → 400")
        void couponAutreProForma_400() {
            coupon.setProFormaId(999L);  // différent de 100L
            assertThrows400(() -> service.verifierCoupon("DIWA10", 100L));
        }

        private void assertThrows400(org.junit.jupiter.api.function.Executable e) {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class, e);
            assertEquals(400, ex.getStatusCode().value());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 5 — appliquerCoupon
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("appliquerCoupon — application + compteur")
    class AppliquerCouponTests {

        @Mock private CouponReductionRepository couponRepo;
        @Mock private ProFormaRepository proFormaRepo;
        @Mock private UserRepository userRepo;
        @Mock private SimpMessagingTemplate messaging;

        @InjectMocks private CouponReductionServiceImpl service;

        private ProForma proForma;
        private CouponReduction coupon;
        private DemandeIntervention demande;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);

            demande = new DemandeIntervention();
            demande.setId(50L);

            proForma = new ProForma();
            proForma.setId(100L);
            proForma.setTotalGeneral(new BigDecimal("100000"));
            proForma.setDemande(demande);

            coupon = new CouponReduction();
            coupon.setId(10L);
            coupon.setCode("DIWA10");
            coupon.setValeur(new BigDecimal("10"));
            coupon.setTypeRemise(CouponReduction.TypeRemise.POURCENTAGE);
            coupon.setNbMaxUtilisations(3);
            coupon.setNbUtilisations(0);
            coupon.setActif(true);

            when(couponRepo.findByCode("DIWA10")).thenReturn(Optional.of(coupon));
            when(proFormaRepo.findById(100L)).thenReturn(Optional.of(proForma));
            when(proFormaRepo.save(any())).thenAnswer(i -> i.getArgument(0));
            when(couponRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        }

        @Test
        @DisplayName("Application réussie → montantRemise et couponCode sur le ProForma")
        void applicationReussie_proFormaMisAJour() {
            AppliquerCouponRequest req = new AppliquerCouponRequest();
            req.setProFormaId(100L);
            req.setCode("DIWA10");

            CouponResponse resp = service.appliquerCoupon(req, "client@test.tg");

            assertEquals(new BigDecimal("10000.00"), resp.getMontantRemise());
            assertEquals(new BigDecimal("90000.00"), resp.getTotalApresRemise());

            // ProForma mis à jour
            assertEquals("DIWA10", proForma.getCouponCode());
            assertEquals(new BigDecimal("10000.00"), proForma.getMontantRemise());
        }

        @Test
        @DisplayName("Compteur nbUtilisations incrémenté de 1")
        void compteurIncremente() {
            AppliquerCouponRequest req = new AppliquerCouponRequest();
            req.setProFormaId(100L);
            req.setCode("DIWA10");

            service.appliquerCoupon(req, "client@test.tg");

            ArgumentCaptor<CouponReduction> cap = ArgumentCaptor.forClass(CouponReduction.class);
            verify(couponRepo).save(cap.capture());
            assertEquals(1, cap.getValue().getNbUtilisations());
        }

        @Test
        @DisplayName("Coupon déjà appliqué → 409 CONFLICT")
        void couponDejaApplique_409() {
            proForma.setCouponCode("DIWA10");

            AppliquerCouponRequest req = new AppliquerCouponRequest();
            req.setProFormaId(100L);
            req.setCode("DIWA10");

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.appliquerCoupon(req, "client@test.tg"));
            assertEquals(409, ex.getStatusCode().value());
        }

        @Test
        @DisplayName("Montant fixe 5000 sur 100 000 → totalApresRemise = 95 000")
        void montantFixe_totalApresRemiseCorrect() {
            coupon.setTypeRemise(CouponReduction.TypeRemise.MONTANT_FIXE);
            coupon.setValeur(new BigDecimal("5000"));

            AppliquerCouponRequest req = new AppliquerCouponRequest();
            req.setProFormaId(100L);
            req.setCode("DIWA10");

            CouponResponse resp = service.appliquerCoupon(req, "client@test.tg");

            assertEquals(new BigDecimal("5000.00"), resp.getMontantRemise());
            assertEquals(new BigDecimal("95000.00"), resp.getTotalApresRemise());
        }

        @Test
        @DisplayName("Coupon épuisé → ne peut plus être appliqué (400)")
        void couponEpuise_refuseApplication() {
            coupon.setNbUtilisations(3);

            AppliquerCouponRequest req = new AppliquerCouponRequest();
            req.setProFormaId(100L);
            req.setCode("DIWA10");

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.appliquerCoupon(req, "client@test.tg"));
            assertEquals(400, ex.getStatusCode().value());

            // ProForma ne doit pas avoir été modifié
            assertNull(proForma.getCouponCode());
            verify(proFormaRepo, never()).save(any());
        }
    }
}
