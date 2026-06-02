package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.dto.MissionChauffeurDTO.*;
import Tg.OSEOR.DIWA.Backend.dto.UserDTO.UserDTOResponse;
import Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur;
import Tg.OSEOR.DIWA.Backend.service.MissionChauffeurService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/atelier/missions")
public class MissionChauffeurController {

    private final MissionChauffeurService missionService;

    public MissionChauffeurController(MissionChauffeurService missionService) {
        this.missionService = missionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<MissionDTOResponse> creer(
            @RequestParam Long demandeId,
            @RequestParam Long chauffeurId,
            @RequestParam String type,
            Authentication auth) {
        return ResponseEntity.status(201).body(
            missionService.creerMission(demandeId, chauffeurId,
                MissionChauffeur.TypeMission.valueOf(type), auth.getName()));
    }

    @GetMapping("/chauffeurs/disponibles")
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<List<UserDTOResponse>> getChauffeursDisponibles() {
        return ResponseEntity.ok(missionService.getChauffeursDisponibles());
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ROLE_CHAUFFEUR')")
    public ResponseEntity<MissionDTOResponse> getMissionActive(Authentication auth) {
        return ResponseEntity.ok(missionService.getMissionActive(auth.getName()));
    }

    @PutMapping("/{id}/depart")
    @PreAuthorize("hasRole('ROLE_CHAUFFEUR')")
    public ResponseEntity<MissionDTOResponse> depart(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(missionService.marquerDepart(id, auth.getName()));
    }

    @PutMapping("/{id}/arrivee-client")
    @PreAuthorize("hasRole('ROLE_CHAUFFEUR')")
    public ResponseEntity<MissionDTOResponse> arriveeClient(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(missionService.marquerArriveeClient(id, auth.getName()));
    }

    @PutMapping("/{id}/checking")
    @PreAuthorize("hasRole('ROLE_CHAUFFEUR')")
    public ResponseEntity<MissionDTOResponse> checking(
            @PathVariable Long id,
            @Valid @RequestBody CheckingRequest req,
            Authentication auth) {
        return ResponseEntity.ok(missionService.soumettreChecking(id, req, auth.getName()));
    }

    @PutMapping("/{id}/approbation")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_USER')")
    public ResponseEntity<MissionDTOResponse> approbation(
            @PathVariable Long id,
            @RequestParam Boolean approuve,
            @RequestParam(required = false) String motifRefus,
            Authentication auth) {
        return ResponseEntity.ok(missionService.approuverChecking(id, approuve, motifRefus, auth.getName()));
    }

    @PutMapping("/{id}/quitter-client")
    @PreAuthorize("hasRole('ROLE_CHAUFFEUR')")
    public ResponseEntity<MissionDTOResponse> quitterClient(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(missionService.marquerQuitterClient(id, auth.getName()));
    }

    @PutMapping("/{id}/arrivee-diwa")
    @PreAuthorize("hasRole('ROLE_CHAUFFEUR')")
    public ResponseEntity<MissionDTOResponse> arriveeDiwa(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(missionService.marquerArriveeDiwa(id, auth.getName()));
    }

    @PutMapping("/{id}/reception-diwa")
    @PreAuthorize("hasRole('ROLE_CHAUFFEUR')")
    public ResponseEntity<MissionDTOResponse> receptionDiwa(
            @PathVariable Long id,
            @Valid @RequestBody ReceptionRequest req,
            Authentication auth) {
        return ResponseEntity.ok(missionService.soumettreReception(id, req, auth.getName()));
    }

    @PutMapping("/{id}/confirmer-livraison")
    @PreAuthorize("hasRole('ROLE_CHAUFFEUR')")
    public ResponseEntity<MissionDTOResponse> confirmerLivraison(
            @PathVariable Long id,
            @Valid @RequestBody ReceptionRequest req,
            Authentication auth) {
        return ResponseEntity.ok(missionService.confirmerLivraison(id, req, auth.getName()));
    }

    @GetMapping("/demande/{demandeId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_RECEPTIONNISTE','ROLE_CHEF_TECHNICIEN')")
    public ResponseEntity<List<MissionDTOResponse>> getParDemande(@PathVariable Long demandeId) {
        return ResponseEntity.ok(missionService.getParDemande(demandeId));
    }

    /**
     * Endpoint client : suivi de la mission de livraison active pour un dossier.
     * Accessible uniquement au client propriétaire du dossier.
     */
    @GetMapping("/livraison-client")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_USER')")
    public ResponseEntity<MissionDTOResponse> getLivraisonClient(
            @RequestParam Long demandeId,
            Authentication auth) {
        return ResponseEntity.ok(missionService.getLivraisonForClient(demandeId, auth.getName()));
    }
}
