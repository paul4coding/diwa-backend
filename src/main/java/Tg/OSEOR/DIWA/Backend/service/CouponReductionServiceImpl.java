package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.AppliquerCouponRequest;
import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.CouponRequest;
import Tg.OSEOR.DIWA.Backend.dto.CouponDTO.CouponResponse;
import Tg.OSEOR.DIWA.Backend.entity.CouponReduction;
import Tg.OSEOR.DIWA.Backend.entity.ProForma;
import Tg.OSEOR.DIWA.Backend.repository.CouponReductionRepository;
import Tg.OSEOR.DIWA.Backend.repository.ProFormaRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CouponReductionServiceImpl implements CouponReductionService {

    private final CouponReductionRepository couponRepository;
    private final ProFormaRepository proFormaRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public CouponReductionServiceImpl(CouponReductionRepository couponRepository,
                                      ProFormaRepository proFormaRepository,
                                      UserRepository userRepository,
                                      SimpMessagingTemplate messagingTemplate) {
        this.couponRepository = couponRepository;
        this.proFormaRepository = proFormaRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // ─── Création ────────────────────────────────────────────

    @Override
    public CouponResponse creerCoupon(CouponRequest request, String createurEmail) {
        if (request.getCode() == null || request.getCode().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le code du coupon est obligatoire.");
        if (request.getValeur() == null || request.getValeur().compareTo(BigDecimal.ZERO) <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La valeur doit être positive.");

        String codeNormalise = request.getCode().toUpperCase().trim();
        if (couponRepository.existsByCode(codeNormalise))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un coupon avec ce code existe déjà.");

        CouponReduction.TypeRemise type;
        try {
            type = CouponReduction.TypeRemise.valueOf(request.getTypeRemise());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Type de remise invalide (POURCENTAGE ou MONTANT_FIXE).");
        }

        if (type == CouponReduction.TypeRemise.POURCENTAGE
                && request.getValeur().compareTo(new BigDecimal("100")) > 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un pourcentage ne peut pas dépasser 100.");

        User createur = userRepository.findByIdentifier(createurEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable."));

        CouponReduction coupon = new CouponReduction();
        coupon.setCode(codeNormalise);
        coupon.setDescription(request.getDescription());
        coupon.setValeur(request.getValeur());
        coupon.setTypeRemise(type);
        coupon.setNbMaxUtilisations(request.getNbMaxUtilisations() != null ? request.getNbMaxUtilisations() : 1);
        coupon.setDateExpiration(request.getDateExpiration());
        coupon.setProFormaId(request.getProFormaId());
        coupon.setCreateur(createur);
        coupon.setActif(true);
        coupon.setNbUtilisations(0);

        CouponReduction saved = couponRepository.save(coupon);
        return toResponse(saved, null, null);
    }

    // ─── Liste (admin / réceptionniste) ──────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> listerTous() {
        return couponRepository.findAllByOrderByCreateDateDesc()
                .stream().map(c -> toResponse(c, null, null))
                .collect(Collectors.toList());
    }

    // ─── Vérification (client) ────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public CouponResponse verifierCoupon(String code, Long proFormaId) {
        CouponReduction coupon = couponRepository.findByCode(code.toUpperCase().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon introuvable."));

        if (!coupon.getActif())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce coupon est inactif.");
        if (coupon.estExpire())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce coupon a expiré.");
        if (coupon.estEpuise())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce coupon a atteint son nombre maximal d'utilisations.");
        if (coupon.getProFormaId() != null && !coupon.getProFormaId().equals(proFormaId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce coupon n'est pas valable pour ce devis.");

        // Calculer la remise estimée
        ProForma pf = proFormaRepository.findById(proFormaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pro Forma introuvable."));

        BigDecimal montant = calculerMontantRemise(coupon, pf.getTotalGeneral());
        BigDecimal totalApres = pf.getTotalGeneral().subtract(montant).max(BigDecimal.ZERO);

        return toResponse(coupon, montant, totalApres);
    }

    // ─── Application (client) ────────────────────────────────

    @Override
    public CouponResponse appliquerCoupon(AppliquerCouponRequest request, String clientEmail) {
        if (request.getCode() == null || request.getCode().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le code est obligatoire.");

        CouponReduction coupon = couponRepository.findByCode(request.getCode().toUpperCase().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon introuvable."));

        if (!coupon.getActif())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce coupon est inactif.");
        if (coupon.estExpire())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce coupon a expiré.");
        if (coupon.estEpuise())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce coupon a atteint son nombre maximal d'utilisations.");

        ProForma pf = proFormaRepository.findById(request.getProFormaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pro Forma introuvable."));

        if (coupon.getProFormaId() != null && !coupon.getProFormaId().equals(pf.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce coupon n'est pas valable pour ce devis.");

        // Refuser si un coupon est déjà appliqué
        if (pf.getCouponCode() != null && !pf.getCouponCode().isBlank())
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Un coupon (" + pf.getCouponCode() + ") est déjà appliqué sur ce devis.");

        // Appliquer la remise
        BigDecimal montant = calculerMontantRemise(coupon, pf.getTotalGeneral());
        BigDecimal totalApres = pf.getTotalGeneral().subtract(montant).max(BigDecimal.ZERO);

        pf.setMontantRemise(montant);
        pf.setCouponCode(coupon.getCode());
        proFormaRepository.save(pf);

        // Incrémenter le compteur
        coupon.setNbUtilisations(coupon.getNbUtilisations() + 1);
        couponRepository.save(coupon);

        // Notifier via WebSocket (topic chat de la demande)
        try {
            Long demandeId = pf.getDemande().getId();
            messagingTemplate.convertAndSend(
                "/topic/chat/" + demandeId,
                Map.of(
                    "type", "COUPON_APPLIQUE",
                    "contenu", "🎉 Coupon " + coupon.getCode() + " appliqué — remise de "
                        + formatMontant(coupon) + " sur votre devis.",
                    "auteurNom", "Système DIWA",
                    "roleAuteur", "SYSTEME"
                )
            );
        } catch (Exception ignored) { /* ne pas bloquer si WS indisponible */ }

        return toResponse(coupon, montant, totalApres);
    }

    // ─── Désactivation ───────────────────────────────────────

    @Override
    public void desactiverCoupon(Long id, String adminEmail) {
        CouponReduction coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon introuvable."));
        coupon.setActif(false);
        couponRepository.save(coupon);
    }

    // ─── Helpers privés ──────────────────────────────────────

    BigDecimal calculerMontantRemise(CouponReduction coupon, BigDecimal totalGeneral) {
        if (totalGeneral == null || totalGeneral.compareTo(BigDecimal.ZERO) <= 0)
            return BigDecimal.ZERO;

        BigDecimal montant;
        if (coupon.getTypeRemise() == CouponReduction.TypeRemise.POURCENTAGE) {
            montant = totalGeneral
                    .multiply(coupon.getValeur())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else {
            montant = coupon.getValeur();
        }
        // Plafonner à totalGeneral (pas de remise négative)
        return montant.min(totalGeneral).setScale(2, RoundingMode.HALF_UP);
    }

    private String formatMontant(CouponReduction coupon) {
        if (coupon.getTypeRemise() == CouponReduction.TypeRemise.POURCENTAGE)
            return coupon.getValeur().stripTrailingZeros().toPlainString() + " %";
        return coupon.getValeur().stripTrailingZeros().toPlainString() + " FCFA";
    }

    CouponResponse toResponse(CouponReduction c, BigDecimal montantRemise, BigDecimal totalApresRemise) {
        CouponResponse r = new CouponResponse();
        r.setId(c.getId());
        r.setCode(c.getCode());
        r.setDescription(c.getDescription());
        r.setValeur(c.getValeur());
        r.setTypeRemise(c.getTypeRemise() != null ? c.getTypeRemise().name() : null);
        r.setNbMaxUtilisations(c.getNbMaxUtilisations());
        r.setNbUtilisations(c.getNbUtilisations());
        r.setDateExpiration(c.getDateExpiration());
        r.setActif(Boolean.TRUE.equals(c.getActif()));
        r.setProFormaId(c.getProFormaId());
        r.setExpire(c.estExpire());
        r.setEpuise(c.estEpuise());
        r.setValide(c.estValide());
        if (c.getCreateur() != null)
            r.setCreateurNom(c.getCreateur().getPrenom() + " " + c.getCreateur().getNom());
        r.setMontantRemise(montantRemise);
        r.setTotalApresRemise(totalApresRemise);
        return r;
    }
}
