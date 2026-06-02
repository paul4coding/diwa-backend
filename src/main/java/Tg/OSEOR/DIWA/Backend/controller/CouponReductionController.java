package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.AppliquerCouponRequest;
import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.CouponRequest;
import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.CouponResponse;
import Tg.OSEOR.DIWA.Backend.service.CouponReductionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponReductionController {

    private final CouponReductionService couponService;

    public CouponReductionController(CouponReductionService couponService) {
        this.couponService = couponService;
    }

    /** Créer un coupon — réservé réceptionniste / admin. */
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_RECEPTIONNISTE','ROLE_ADMIN')")
    public ResponseEntity<CouponResponse> creer(@RequestBody CouponRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponService.creerCoupon(request, principal.getName()));
    }

    /** Lister tous les coupons (avec stats). */
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_RECEPTIONNISTE','ROLE_ADMIN')")
    public List<CouponResponse> lister() {
        return couponService.listerTous();
    }

    /**
     * Vérifier la validité d'un coupon (sans l'appliquer).
     * Retourne la remise estimée si valide, 400 sinon.
     */
    @GetMapping("/{code}/verifier")
    @PreAuthorize("isAuthenticated()")
    public CouponResponse verifier(@PathVariable String code,
                                   @RequestParam Long proFormaId) {
        return couponService.verifierCoupon(code, proFormaId);
    }

    /** Appliquer un coupon à un pro-forma. */
    @PostMapping("/appliquer")
    @PreAuthorize("isAuthenticated()")
    public CouponResponse appliquer(@RequestBody AppliquerCouponRequest request, Principal principal) {
        return couponService.appliquerCoupon(request, principal.getName());
    }

    /** Désactiver un coupon. */
    @PatchMapping("/{id}/desactiver")
    @PreAuthorize("hasAnyRole('ROLE_RECEPTIONNISTE','ROLE_ADMIN')")
    public ResponseEntity<Void> desactiver(@PathVariable Long id, Principal principal) {
        couponService.desactiverCoupon(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
