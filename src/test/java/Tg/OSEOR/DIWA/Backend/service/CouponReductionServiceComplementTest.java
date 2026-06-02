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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.CouponResponse;
import Tg.OSEOR.DIWA.Backend.entity.CouponReduction;
import Tg.OSEOR.DIWA.Backend.repository.CouponReductionRepository;
import Tg.OSEOR.DIWA.Backend.repository.ProFormaRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;

/**
 * Tests complémentaires — CouponReductionServiceImpl.
 *
 * Couvre les méthodes non testées dans CouponReductionServiceTest :
 *  1. listerTous  — délégation repository + mapping complet
 *  2. toResponse  — mapping entity → DTO (avec et sans montants)
 *  3. desactiverCoupon — mutation actif + persistance, coupon introuvable
 */
class CouponReductionServiceComplementTest {

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 1 — listerTous
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("listerTous — délégation repository + mapping")
    class ListerTousTests {

        @Mock private CouponReductionRepository couponRepo;
        @Mock private ProFormaRepository proFormaRepo;
        @Mock private UserRepository userRepo;
        @Mock private SimpMessagingTemplate messaging;

        @InjectMocks private CouponReductionServiceImpl service;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        @DisplayName("Aucun coupon en base → liste vide")
        void listeVide_retourneListeVide() {
            when(couponRepo.findAllByOrderByCreateDateDesc()).thenReturn(List.of());

            List<CouponResponse> result = service.listerTous();

            assertTrue(result.isEmpty());
            verify(couponRepo).findAllByOrderByCreateDateDesc();
        }

        @Test
        @DisplayName("Un coupon en base → mapped correctement")
        void unCoupon_mappe() {
            CouponReduction c = coupon("DIWA10", CouponReduction.TypeRemise.POURCENTAGE, "10", 3, 1);
            when(couponRepo.findAllByOrderByCreateDateDesc()).thenReturn(List.of(c));

            List<CouponResponse> result = service.listerTous();

            assertEquals(1, result.size());
            CouponResponse r = result.get(0);
            assertEquals("DIWA10", r.getCode());
            assertEquals("POURCENTAGE", r.getTypeRemise());
            assertEquals(3, r.getNbMaxUtilisations());
            assertEquals(1, r.getNbUtilisations());
            assertTrue(r.isActif());
        }

        @Test
        @DisplayName("Plusieurs coupons → ordre et mapping conservés")
        void plusieursCoupons_ordreMaintenu() {
            CouponReduction c1 = coupon("RECENT",   CouponReduction.TypeRemise.POURCENTAGE,  "5",     1, 0);
            CouponReduction c2 = coupon("ANCIEN",   CouponReduction.TypeRemise.MONTANT_FIXE, "10000", 5, 3);
            when(couponRepo.findAllByOrderByCreateDateDesc()).thenReturn(List.of(c1, c2));

            List<CouponResponse> result = service.listerTous();

            assertEquals(2, result.size());
            assertEquals("RECENT", result.get(0).getCode());
            assertEquals("ANCIEN", result.get(1).getCode());
            assertEquals("MONTANT_FIXE", result.get(1).getTypeRemise());
            assertEquals(3, result.get(1).getNbUtilisations());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 2 — toResponse
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("toResponse — mapping entity → DTO")
    class ToResponseTests {

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
        @DisplayName("Sans montants → montantRemise et totalApresRemise sont null")
        void sansMontants_champsMontantsNull() {
            CouponReduction c = coupon("VIP", CouponReduction.TypeRemise.POURCENTAGE, "20", 5, 0);

            CouponResponse r = service.toResponse(c, null, null);

            assertEquals("VIP", r.getCode());
            assertEquals("POURCENTAGE", r.getTypeRemise());
            assertNull(r.getMontantRemise());
            assertNull(r.getTotalApresRemise());
            assertTrue(r.isActif());
            assertTrue(r.isValide());
            assertFalse(r.isExpire());
            assertFalse(r.isEpuise());
        }

        @Test
        @DisplayName("Avec montants → montantRemise et totalApresRemise correctement renseignés")
        void avecMontants_champsMontantsPresents() {
            CouponReduction c = coupon("DIWA10", CouponReduction.TypeRemise.POURCENTAGE, "10", 3, 1);
            BigDecimal remise  = new BigDecimal("8000.00");
            BigDecimal totalAp = new BigDecimal("72000.00");

            CouponResponse r = service.toResponse(c, remise, totalAp);

            assertEquals(new BigDecimal("8000.00"),  r.getMontantRemise());
            assertEquals(new BigDecimal("72000.00"), r.getTotalApresRemise());
        }

        @Test
        @DisplayName("Créateur renseigné → createurNom = prénom + nom")
        void createurRenseigne_nomConcatene() {
            CouponReduction c = coupon("TEST", CouponReduction.TypeRemise.MONTANT_FIXE, "5000", 1, 0);
            User u = new User();
            u.setPrenom("Marie");
            u.setNom("Dupont");
            c.setCreateur(u);

            CouponResponse r = service.toResponse(c, null, null);

            assertEquals("Marie Dupont", r.getCreateurNom());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GROUPE 3 — desactiverCoupon
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("desactiverCoupon — mutation actif + persistance")
    class DesactiverCouponTests {

        @Mock private CouponReductionRepository couponRepo;
        @Mock private ProFormaRepository proFormaRepo;
        @Mock private UserRepository userRepo;
        @Mock private SimpMessagingTemplate messaging;

        @InjectMocks private CouponReductionServiceImpl service;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        @DisplayName("Coupon existant → actif passe à false et save appelé")
        void couponExistant_actifFalse() {
            CouponReduction c = coupon("DIWA10", CouponReduction.TypeRemise.POURCENTAGE, "10", 1, 0);
            c.setId(7L);
            when(couponRepo.findById(7L)).thenReturn(Optional.of(c));
            when(couponRepo.save(any())).thenAnswer(i -> i.getArgument(0));

            service.desactiverCoupon(7L, "admin@diwa.tg");

            ArgumentCaptor<CouponReduction> cap = ArgumentCaptor.forClass(CouponReduction.class);
            verify(couponRepo).save(cap.capture());
            assertFalse(cap.getValue().getActif(),
                    "Le coupon doit être désactivé (actif = false)");
        }

        @Test
        @DisplayName("ID introuvable → 404 NOT_FOUND")
        void idIntrouvable_404() {
            when(couponRepo.findById(99L)).thenReturn(Optional.empty());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.desactiverCoupon(99L, "admin@diwa.tg"));
            assertEquals(404, ex.getStatusCode().value());
            verify(couponRepo, never()).save(any());
        }

        @Test
        @DisplayName("Coupon déjà inactif → désactivation idempotente (save quand même appelé)")
        void couponDejaInactif_saveAppele() {
            CouponReduction c = coupon("OLD", CouponReduction.TypeRemise.POURCENTAGE, "5", 1, 1);
            c.setId(3L);
            c.setActif(false);
            when(couponRepo.findById(3L)).thenReturn(Optional.of(c));
            when(couponRepo.save(any())).thenAnswer(i -> i.getArgument(0));

            // Ne doit pas lever d'exception
            assertDoesNotThrow(() -> service.desactiverCoupon(3L, "admin@diwa.tg"));
            verify(couponRepo).save(any());
        }
    }

    // ─── Helper ──────────────────────────────────────────────────────────
    private CouponReduction coupon(String code, CouponReduction.TypeRemise type,
                                   String valeur, int maxUt, int nbUt) {
        CouponReduction c = new CouponReduction();
        c.setCode(code);
        c.setTypeRemise(type);
        c.setValeur(new BigDecimal(valeur));
        c.setNbMaxUtilisations(maxUt);
        c.setNbUtilisations(nbUt);
        c.setActif(true);
        return c;
    }
}
