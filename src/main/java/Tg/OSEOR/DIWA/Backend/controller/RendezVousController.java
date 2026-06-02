package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;
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

    private final RendezVousService rdvService;
    private final Tg.OSEOR.DIWA.Backend.service.EmailService emailService;

    public RendezVousController(RendezVousService rdvService, 
                               @org.springframework.beans.factory.annotation.Qualifier("savEmailService") Tg.OSEOR.DIWA.Backend.service.EmailService emailService) {
        this.rdvService = rdvService;
        this.emailService = emailService;
    }

    @PostMapping("/demander")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> demanderRdv(
        @Valid @RequestBody RendezVousDTORequest request, 
        Authentication authentication) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        RendezVousDTOResponse data = rdvService.demanderRendezVous(request, userId);
        BaseResponse<RendezVousDTOResponse> response = new BaseResponse<>(201, 
            "Demande de RDV envoyée avec succès (en attente de validation)", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/admin/valider/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> valider(@PathVariable Long id) {
        System.out.println(">>> [V10] Tentative de validation Elite pour le RDV #" + id);
        try {
            // 1. Persistance stricte
            RendezVousDTOResponse result = rdvService.validerRendezVousAdmin(id);
            System.out.println(">>> [V10] Succès Base pour RDV #" + id);
            
            // 2. Notification (N'affecte pas le succès global si elle échoue)
            try {
                Tg.OSEOR.DIWA.Backend.entity.RendezVous fullRdv = rdvService.getEntityById(id);
                emailService.sendRendezVousConfirmation(fullRdv);
                return ResponseEntity.ok(BaseResponse.success(result, "RDV #" + id + " validé et client notifié."));
            } catch (Throwable mailError) {
                System.err.println(">>> [V11] ALERTE SYSTEME MAIL: " + mailError.getMessage());
                mailError.printStackTrace();
                return ResponseEntity.ok(BaseResponse.success(result, "RDV validé, mais échec notification (Erreur Système)."));
            }
        } catch (Throwable e) {
            System.err.println(">>> [V11] CRASH FATAL VALIDATION: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponse.error(500, "Erreur fatale validation: " + e.getMessage()));
        }
    }

    @PutMapping("/admin/valider-avec-creneau/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> validerAvecCreneau(
            @PathVariable Long id, 
            @RequestParam Long creneauId) {
        try {
            // 1. Transaction (Mise à jour base + Blocage créneau)
            RendezVousDTOResponse result = rdvService.validerRendezVous(id, creneauId);
            
            // 2. Notification
            try {
                Tg.OSEOR.DIWA.Backend.entity.RendezVous fullRdv = rdvService.getEntityById(id);
                emailService.sendRendezVousConfirmation(fullRdv);
                return ResponseEntity.ok(BaseResponse.success(result, "RDV validé avec créneau et email envoyé."));
            } catch (Exception mailError) {
                return ResponseEntity.ok(BaseResponse.success(result, "RDV validé avec créneau, mais mail en échec: " + mailError.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponse.error(500, "Erreur validation: " + e.getMessage()));
        }
    }

    @PutMapping("/planifier-direct/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> planifier(
            @PathVariable Long id, 
            @RequestParam String date,
            @RequestParam String heure,
            @RequestParam Long techId) {
        return ResponseEntity.ok(new BaseResponse<>(200, "RDV planifié sur la grille", rdvService.planifier(id, date, heure, techId)));
    }

    @PutMapping("/rejeter/{id}")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> rejeterAdmin(@PathVariable Long id) {
        RendezVousDTOResponse data = rdvService.rejeterRendezVousAdmin(id);
        BaseResponse<RendezVousDTOResponse> response = new BaseResponse<>(200, 
            "RDV rejeté par l'administrateur et créneau libéré", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/en-attente")
    public ResponseEntity<?> listerEnAttente() {
        try {
            List<RendezVousDTOResponse> data = rdvService.listerEnAttente();
            return ResponseEntity.ok(new BaseResponse<>(200, "Liste OK", data));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponse.error(500, "Erreur planification: " + e.getMessage()));
        }
    }

    @PutMapping("/admin/annuler/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> annuler(@PathVariable Long id) {
        try {
            // 1. Annulation transactionnelle
            RendezVousDTOResponse result = rdvService.annulerRendezVous(id);
            
            // 2. Notification (Hors transaction)
            try {
                Tg.OSEOR.DIWA.Backend.entity.RendezVous fullRdv = rdvService.getEntityById(id);
                emailService.sendRendezVousAnnulation(fullRdv);
                return ResponseEntity.ok(BaseResponse.success(result, "RDV #" + id + " annulé et client notifié."));
            } catch (Throwable mailError) {
                System.err.println("ALERTE MAIL ANNULATION: " + mailError.getMessage());
                return ResponseEntity.ok(BaseResponse.success(result, "RDV annulé, mais échec de la notification email."));
            }
        } catch (Throwable e) {
            return ResponseEntity.status(500).body(BaseResponse.error(500, "Erreur lors de l'annulation: " + e.getMessage()));
        }
    }

    @PutMapping("/admin/reinitialiser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> reinitialiser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(BaseResponse.success(rdvService.reinitialiserPlanification(id), "Demande remise en attente avec succès."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponse.error(500, "Erreur réinitialisation: " + e.getMessage()));
        }
    }

    @GetMapping("/mes-rdv")
    public ResponseEntity<BaseResponse<List<RendezVousDTOResponse>>> mesRdv(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        List<RendezVousDTOResponse> data = rdvService.listerMesRendezVous(userId);
        BaseResponse<List<RendezVousDTOResponse>> response = new BaseResponse<>(200, 
            "Historique de mes RDVs", data);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<RendezVousDTOResponse>>> listAll() {
        List<RendezVousDTOResponse> data = rdvService.listerTous();
        BaseResponse<List<RendezVousDTOResponse>> response = new BaseResponse<>(200, 
            "Liste complète des RDVs", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<BaseResponse<RendezVousDTOResponse>> getById(@PathVariable Long id) {
        RendezVousDTOResponse data = rdvService.getById(id);
        BaseResponse<RendezVousDTOResponse> response = new BaseResponse<>(200, 
            "RDV récupéré", data);
        return ResponseEntity.ok(response);
    }
}
