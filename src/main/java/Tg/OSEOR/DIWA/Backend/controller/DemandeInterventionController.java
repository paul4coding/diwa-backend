package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.dto.DemandeDTO.DemandeCreateRequest;
import Tg.OSEOR.DIWA.Backend.dto.DemandeDTO.DemandeDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.DemandeDTO.EnregistrementVehiculeRequest;
import Tg.OSEOR.DIWA.Backend.dto.DemandeDTO.PlanificationRequest;
import Tg.OSEOR.DIWA.Backend.service.DemandeInterventionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/demandes")
public class DemandeInterventionController {

    private final DemandeInterventionService demandeService;

    public DemandeInterventionController(DemandeInterventionService demandeService) {
        this.demandeService = demandeService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_USER')")
    public ResponseEntity<DemandeDTOResponse> creerDemande(@Valid @RequestBody DemandeCreateRequest request, Authentication auth) {
        return ResponseEntity.status(201).body(demandeService.createDemande(request, auth.getName()));
    }

    @GetMapping("/mes")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_USER')")
    public ResponseEntity<List<DemandeDTOResponse>> getMesDemandes(Authentication auth) {
        return ResponseEntity.ok(demandeService.getMesDemandes(auth.getName()));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT','ROLE_USER','ROLE_RECEPTIONNISTE','ROLE_CHEF_TECHNICIEN','ROLE_TECHNICIEN','ROLE_CHAUFFEUR','ROLE_ADMIN')")
    public ResponseEntity<DemandeDTOResponse> getByUuid(@PathVariable String uuid, Authentication auth) {
        return ResponseEntity.ok(demandeService.getByUuid(uuid, auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_RECEPTIONNISTE','ROLE_CHEF_TECHNICIEN','ROLE_TECHNICIEN','ROLE_ADMIN')")
    public ResponseEntity<List<DemandeDTOResponse>> getAllFiltrees(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String urgence) {
        return ResponseEntity.ok(demandeService.getAllFiltrees(statut, urgence));
    }

    // ── Workflow Atelier ──────────────────────────────────────

    @PutMapping("/{id}/enregistrer-technique")
    @PreAuthorize("hasAnyRole('ROLE_CHEF_TECHNICIEN', 'ROLE_TECHNICIEN')")
    public ResponseEntity<DemandeDTOResponse> enregistrerVehicule(
            @PathVariable String id,
            @Valid @RequestBody EnregistrementVehiculeRequest request,
            Authentication auth) {
        return ResponseEntity.ok(demandeService.enregistrerVehicule(id, request, auth.getName()));
    }

    @PutMapping("/{id}/valider-reception")
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<DemandeDTOResponse> validerEnregistrement(@PathVariable String id, Authentication auth) {
        return ResponseEntity.ok(demandeService.validerEnregistrement(id, auth.getName()));
    }

    @PutMapping("/{id}/assigner")
    @PreAuthorize("hasAnyRole('ROLE_CHEF_TECHNICIEN', 'ROLE_RECEPTIONNISTE')")
    public ResponseEntity<DemandeDTOResponse> assignerTechnicien(
            @PathVariable String id,
            @RequestParam Long technicienId,
            Authentication auth) {
        return ResponseEntity.ok(demandeService.assignerTechnicien(id, technicienId, auth.getName()));
    }

    @PutMapping("/{id}/cloturer")
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<DemandeDTOResponse> enregistrerSortie(@PathVariable String id, Authentication auth) {
        return ResponseEntity.ok(demandeService.enregistrerSortie(id, auth.getName()));
    }

    // ── Workflow Arrivée Directe (Scénario B) ──────────────────

    @PostMapping("/direct")
    @PreAuthorize("hasAnyRole('ROLE_RECEPTIONNISTE', 'ROLE_CHEF_TECHNICIEN')")
    public ResponseEntity<DemandeDTOResponse> creerDemandeDirecte(@Valid @RequestBody DemandeCreateRequest request, Authentication auth) {
        return ResponseEntity.status(201).body(demandeService.createDemandeDirecte(request, auth.getName()));
    }

    @PutMapping("/{id}/associer-client")
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<DemandeDTOResponse> associerClientController(
            @PathVariable String id,
            @RequestParam Long clientId,
            Authentication auth) {
        return ResponseEntity.ok(demandeService.associerClient(id, clientId, auth.getName()));
    }

    // ── Workflow Chauffeur (Scénario A) ────────────────────────

    @PutMapping("/{id}/assigner-chauffeur")
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<DemandeDTOResponse> assignerChauffeur(
            @PathVariable String id,
            @RequestParam Long chauffeurId,
            Authentication auth) {
        return ResponseEntity.ok(demandeService.assignerChauffeurRecuperation(id, chauffeurId, auth.getName()));
    }

    @PutMapping("/{id}/assigner-chauffeur-livraison")
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<DemandeDTOResponse> assignerChauffeurLivraison(
            @PathVariable String id,
            @RequestParam Long chauffeurId,
            Authentication auth) {
        return ResponseEntity.ok(demandeService.assignerChauffeurLivraison(id, chauffeurId, auth.getName()));
    }

    @PutMapping("/{id}/planification")
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<DemandeDTOResponse> planifierDemande(
            @PathVariable String id,
            @RequestBody PlanificationRequest request,
            Authentication auth) {
        java.time.LocalDate date = (request.getDateRecuperation() != null && !request.getDateRecuperation().isEmpty()) 
                ? java.time.LocalDate.parse(request.getDateRecuperation()) 
                : null;
        return ResponseEntity.ok(demandeService.planifierRecuperation(id, date, request.getCreneauId(), auth.getName()));
    }

    @PutMapping("/{id}/chauffeur-recupere")
    @PreAuthorize("hasRole('ROLE_CHAUFFEUR')")
    public ResponseEntity<DemandeDTOResponse> confirmerRecuperation(@PathVariable String id, Authentication auth) {
        return ResponseEntity.ok(demandeService.confirmerRecuperation(id, auth.getName()));
    }

    @PutMapping("/{id}/chauffeur-arrive")
    @PreAuthorize("hasRole('ROLE_CHAUFFEUR')")
    public ResponseEntity<DemandeDTOResponse> confirmerArriveeGarage(@PathVariable String id, Authentication auth) {
        return ResponseEntity.ok(demandeService.confirmerArriveeGarage(id, auth.getName()));
    }

    @PutMapping("/{id}/annuler-mission")
    @PreAuthorize("hasAnyRole('ROLE_RECEPTIONNISTE', 'ROLE_ADMIN')")
    public ResponseEntity<DemandeDTOResponse> annulerMission(@PathVariable String id, Authentication auth) {
        return ResponseEntity.ok(demandeService.annulerMissionChauffeur(id, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_RECEPTIONNISTE', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteDemande(@PathVariable String id, Authentication auth) {
        demandeService.deleteDemande(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
