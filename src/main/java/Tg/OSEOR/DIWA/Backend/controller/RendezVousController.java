package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO.RendezVousDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO.RendezVousDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.RendezVousService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import Tg.OSEOR.DIWA.Backend.security.UserDetailsImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/rendezvous")
public class RendezVousController {

    private static final Logger log = LoggerFactory.getLogger(RendezVousController.class);

    private final RendezVousService rdvService;
    private final Tg.OSEOR.DIWA.Backend.service.EmailService emailService;

    public RendezVousController(RendezVousService rdvService,
            @org.springframework.beans.factory.annotation.Qualifier("savEmailService")
            Tg.OSEOR.DIWA.Backend.service.EmailService emailService) {
        this.rdvService = rdvService;
        this.emailService = emailService;
    }

    @PostMapping("/demander")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> demanderRdv(
            @Valid @RequestBody RendezVousDTORequest request,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        RendezVousDTOResponse data = rdvService.demanderRendezVous(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse<>(201, "Demande de RDV envoyée avec succès (en attente de validation)", data));
    }

    @PutMapping("/admin/valider/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> valider(@PathVariable Long id) {
        // Validation métier — si elle échoue, GlobalExceptionHandler retourne 500 générique.
        RendezVousDTOResponse result = rdvService.validerRendezVousAdmin(id);

        // Notification email (échec non bloquant — le RDV reste validé).
        try {
            emailService.sendRendezVousConfirmation(rdvService.getEntityById(id));
            return ResponseEntity.ok(BaseResponse.success(result, "RDV validé et client notifié."));
        } catch (Exception mailError) {
            log.warn("Notification email RDV #{} en échec : {}", id, mailError.getMessage());
            return ResponseEntity.ok(BaseResponse.success(result, "RDV validé, mais échec de la notification email."));
        }
    }

    @PutMapping("/admin/valider-avec-creneau/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> validerAvecCreneau(
            @PathVariable Long id, @RequestParam Long creneauId) {
        RendezVousDTOResponse result = rdvService.validerRendezVous(id, creneauId);

        try {
            emailService.sendRendezVousConfirmation(rdvService.getEntityById(id));
            return ResponseEntity.ok(BaseResponse.success(result, "RDV validé avec créneau et email envoyé."));
        } catch (Exception mailError) {
            log.warn("Notification email RDV #{} en échec : {}", id, mailError.getMessage());
            return ResponseEntity.ok(BaseResponse.success(result, "RDV validé avec créneau, mais mail en échec."));
        }
    }

    @PutMapping("/planifier-direct/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> planifier(
            @PathVariable Long id, @RequestParam String date,
            @RequestParam String heure, @RequestParam Long techId) {
        return ResponseEntity.ok(
                new BaseResponse<>(200, "RDV planifié sur la grille", rdvService.planifier(id, date, heure, techId)));
    }

    @PutMapping("/rejeter/{id}")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> rejeterAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(200,
                "RDV rejeté par l'administrateur et créneau libéré", rdvService.rejeterRendezVousAdmin(id)));
    }

    @GetMapping("/en-attente")
    public ResponseEntity<?> listerEnAttente() {
        return ResponseEntity.ok(new BaseResponse<>(200, "Liste OK", rdvService.listerEnAttente()));
    }

    @PutMapping("/admin/annuler/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> annuler(@PathVariable Long id) {
        RendezVousDTOResponse result = rdvService.annulerRendezVous(id);

        try {
            emailService.sendRendezVousAnnulation(rdvService.getEntityById(id));
            return ResponseEntity.ok(BaseResponse.success(result, "RDV annulé et client notifié."));
        } catch (Exception mailError) {
            log.warn("Notification annulation RDV #{} en échec : {}", id, mailError.getMessage());
            return ResponseEntity.ok(BaseResponse.success(result, "RDV annulé, mais échec de la notification email."));
        }
    }

    @PutMapping("/admin/reinitialiser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> reinitialiser(@PathVariable Long id) {
        return ResponseEntity.ok(
                BaseResponse.success(rdvService.reinitialiserPlanification(id), "Demande remise en attente avec succès."));
    }

    @GetMapping("/mes-rdv")
    public ResponseEntity<BaseResponse<List<RendezVousDTOResponse>>> mesRdv(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(new BaseResponse<>(200, "Historique de mes RDVs",
                rdvService.listerMesRendezVous(userDetails.getId())));
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<RendezVousDTOResponse>>> listAll() {
        return ResponseEntity.ok(new BaseResponse<>(200, "Liste complète des RDVs", rdvService.listerTous()));
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(200, "RDV récupéré", rdvService.getById(id)));
    }
}
