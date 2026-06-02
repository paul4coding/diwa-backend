package Tg.OSEOR.DIWA.Backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.AppliquerCouponRequest;
import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.CouponRequest;
import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.CouponResponse;
import Tg.OSEOR.DIWA.Backend.service.CouponReductionService;

/**
 * Tests unitaires — CouponReductionController.
 *
 * Tests purs Mockito (aucun contexte Spring).
 * Vérifie la délégation au service et la construction des réponses HTTP.
 *
 * Scénarios couverts (10 tests) :
 *  1. creer   — succès 201, doublon 409
 *  2. lister  — liste peuplée, liste vide
 *  3. verifier — coupon valide, coupon introuvable 404
 *  4. appliquer — succès, déjà appliqué 409
 *  5. desactiver — succès 204, introuvable 404
 */
class CouponReductionControllerTest {

    @Mock private CouponReductionService couponService;
    @Mock private Principal principal;

    private CouponReductionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new CouponReductionController(couponService);
        when(principal.getName()).thenReturn("admin@diwa.tg");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 1 — creer
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("creer — POST /api/v1/coupons")
    class CreerTests {

        @Test
        @DisplayName("Création réussie → 201 CREATED avec la réponse du service")
        void creationReussie_retourne201() {
            CouponRequest req = buildRequest("diwa10", "POURCENTAGE", "10", 5);
            CouponResponse expected = response("DIWA10");
            when(couponService.creerCoupon(req, "admin@diwa.tg")).thenReturn(expected);

            ResponseEntity<CouponResponse> result = controller.creer(req, principal);

            assertEquals(HttpStatus.CREATED, result.getStatusCode());
            assertNotNull(result.getBody());
            assertEquals("DIWA10", result.getBody().getCode());
            verify(couponService).creerCoupon(req, "admin@diwa.tg");
        }

        @Test
        @DisplayName("Code en doublon → service lance 409, exception propagée")
        void codeDoublon_409Propage() {
            CouponRequest req = buildRequest("DIWA10", "POURCENTAGE", "10", 1);
            when(couponService.creerCoupon(any(), any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Code déjà existant."));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> controller.creer(req, principal));
            assertEquals(409, ex.getStatusCode().value());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 2 — lister
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("lister — GET /api/v1/coupons")
    class ListerTests {

        @Test
        @DisplayName("Plusieurs coupons → liste complète retournée")
        void listeCoupons_retournee() {
            CouponResponse r1 = response("A10");
            CouponResponse r2 = response("VIP50K");
            when(couponService.listerTous()).thenReturn(List.of(r1, r2));

            List<CouponResponse> result = controller.lister();

            assertEquals(2, result.size());
            assertEquals("A10",    result.get(0).getCode());
            assertEquals("VIP50K", result.get(1).getCode());
            verify(couponService).listerTous();
        }

        @Test
        @DisplayName("Aucun coupon créé → liste vide")
        void listeVide_retourneListeVide() {
            when(couponService.listerTous()).thenReturn(List.of());

            List<CouponResponse> result = controller.lister();

            assertTrue(result.isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 3 — verifier
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verifier — GET /api/v1/coupons/{code}/verifier")
    class VerifierTests {

        @Test
        @DisplayName("Code valide + proFormaId → remise estimée retournée")
        void couponValide_remiseEstimee() {
            CouponResponse expected = response("DIWA10");
            expected.setMontantRemise(new BigDecimal("8000.00"));
            expected.setTotalApresRemise(new BigDecimal("72000.00"));
            when(couponService.verifierCoupon("DIWA10", 100L)).thenReturn(expected);

            CouponResponse result = controller.verifier("DIWA10", 100L);

            assertEquals("DIWA10", result.getCode());
            assertEquals(new BigDecimal("8000.00"), result.getMontantRemise());
            verify(couponService).verifierCoupon("DIWA10", 100L);
        }

        @Test
        @DisplayName("Coupon introuvable → 404 propagé")
        void couponIntrouvable_404() {
            when(couponService.verifierCoupon("INCONNU", 1L))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon introuvable."));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> controller.verifier("INCONNU", 1L));
            assertEquals(404, ex.getStatusCode().value());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 4 — appliquer
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("appliquer — POST /api/v1/coupons/appliquer")
    class AppliquerTests {

        @Test
        @DisplayName("Application réussie → réponse du service avec montants")
        void applicationReussie_montantsRetournes() {
            AppliquerCouponRequest req = new AppliquerCouponRequest();
            req.setCode("DIWA10");
            req.setProFormaId(100L);

            CouponResponse expected = response("DIWA10");
            expected.setMontantRemise(new BigDecimal("10000.00"));
            expected.setTotalApresRemise(new BigDecimal("90000.00"));
            when(couponService.appliquerCoupon(req, "admin@diwa.tg")).thenReturn(expected);

            CouponResponse result = controller.appliquer(req, principal);

            assertEquals(new BigDecimal("10000.00"), result.getMontantRemise());
            assertEquals(new BigDecimal("90000.00"), result.getTotalApresRemise());
            verify(couponService).appliquerCoupon(req, "admin@diwa.tg");
        }

        @Test
        @DisplayName("Coupon déjà appliqué → 409 propagé")
        void couponDejaApplique_409() {
            AppliquerCouponRequest req = new AppliquerCouponRequest();
            req.setCode("DIWA10");
            req.setProFormaId(100L);
            when(couponService.appliquerCoupon(any(), any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT,
                            "Un coupon est déjà appliqué sur ce devis."));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> controller.appliquer(req, principal));
            assertEquals(409, ex.getStatusCode().value());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 5 — desactiver
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("desactiver — PATCH /api/v1/coupons/{id}/desactiver")
    class DesactiverTests {

        @Test
        @DisplayName("Désactivation réussie → 204 NO_CONTENT, service appelé")
        void desactivationReussie_204() {
            doNothing().when(couponService).desactiverCoupon(5L, "admin@diwa.tg");

            ResponseEntity<Void> result = controller.desactiver(5L, principal);

            assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
            verify(couponService).desactiverCoupon(5L, "admin@diwa.tg");
        }

        @Test
        @DisplayName("ID introuvable → 404 propagé")
        void idIntrouvable_404() {
            doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon introuvable."))
                    .when(couponService).desactiverCoupon(eq(99L), any());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> controller.desactiver(99L, principal));
            assertEquals(404, ex.getStatusCode().value());
        }
    }

    // ─── Helper ──────────────────────────────────────────────────────────
    private CouponResponse response(String code) {
        CouponResponse r = new CouponResponse();
        r.setCode(code);
        r.setActif(true);
        r.setValide(true);
        return r;
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
