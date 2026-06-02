package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.AppliquerCouponRequest;
import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.CouponRequest;
import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.CouponResponse;

import java.util.List;

public interface CouponReductionService {
    CouponResponse creerCoupon(CouponRequest request, String createurEmail);
    List<CouponResponse> listerTous();
    CouponResponse verifierCoupon(String code, Long proFormaId);
    CouponResponse appliquerCoupon(AppliquerCouponRequest request, String clientEmail);
    void desactiverCoupon(Long id, String adminEmail);
}
