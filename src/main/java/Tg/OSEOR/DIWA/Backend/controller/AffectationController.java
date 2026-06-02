package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.ProFormaDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.ProFormaServiceModule;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/atelier/affectations")
public class AffectationController {

    private final ProFormaServiceModule proFormaService;

    public AffectationController(ProFormaServiceModule proFormaService) {
        this.proFormaService = proFormaService;
    }

    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('ROLE_CHEF_TECHNICIEN')")
    public ResponseEntity<java.util.List<ProFormaDTOResponse>> getAwaiting() {
        return ResponseEntity.ok(proFormaService.getAwaitingAssignment());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_CHEF_TECHNICIEN')")
    public ResponseEntity<java.util.List<Tg.OSEOR.DIWA.Backend.dto.AtelierDTO.AffectationDTO>> getAll() {
        return ResponseEntity.ok(proFormaService.getAllAffectations());
    }

    @PostMapping("/{proFormaId}")
    @PreAuthorize("hasRole('ROLE_CHEF_TECHNICIEN')")
    public ResponseEntity<Void> affecter(@PathVariable Long proFormaId,
                                       @RequestParam Long technicienId,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime finPrevue,
                                       @RequestParam(required = false) String notes,
                                       Authentication auth) {
        proFormaService.affecterTravail(proFormaId, technicienId, debut, finPrevue, notes, auth.getName());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{proFormaId}")
    @PreAuthorize("hasRole('ROLE_CHEF_TECHNICIEN')")
    public ResponseEntity<Void> modifier(@PathVariable Long proFormaId,
                                       @RequestParam Long technicienId,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime finPrevue,
                                       @RequestParam(required = false) String notes,
                                       Authentication auth) {
        proFormaService.modifierAffectation(proFormaId, technicienId, debut, finPrevue, notes, auth.getName());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{proFormaId}/terminer")
    @PreAuthorize("hasRole('ROLE_CHEF_TECHNICIEN')")
    public ResponseEntity<Void> terminer(@PathVariable Long proFormaId, Authentication auth) {
        proFormaService.confirmerFinTravaux(proFormaId, auth.getName());
        return ResponseEntity.ok().build();
    }
}
