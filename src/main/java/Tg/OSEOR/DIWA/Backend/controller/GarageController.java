package Tg.OSEOR.DIWA.Backend.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import Tg.OSEOR.DIWA.Backend.dto.CreneauDTO;
import Tg.OSEOR.DIWA.Backend.dto.GlobalPlanningDTO;
import Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO.RendezVousDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.RendezVous;
import Tg.OSEOR.DIWA.Backend.mappers.RendezVousMapper;
import Tg.OSEOR.DIWA.Backend.service.GarageService;
import Tg.OSEOR.DIWA.Backend.service.PlageHoraireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/garage")
@Tag(name = "Garage", description = "Module de gestion de la disponibilité et des rendez-vous")
public class GarageController {

    @Autowired
    private GarageService garageService;

    @Autowired
    private PlageHoraireService plageHoraireService;

    @Autowired
    private RendezVousMapper rdvMapper;

    @GetMapping("/disponibilites")
    @Operation(summary = "Obtenir les créneaux disponibles pour une date et un type de service")
    public ResponseEntity<List<CreneauDTO>> getDisponibilites(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "") String type) {
        return ResponseEntity.ok(garageService.getDisponibilites(date, type));
    }

    @PostMapping("/rdv")
    @Operation(summary = "Créer un rendez-vous (réservation atomique du créneau)")
    public ResponseEntity<RendezVousDTOResponse> createRendezVous(
            @RequestParam Long plageHoraireId,
            @RequestParam Long userId,
            @RequestParam(required = false) Long serviceId,
            @RequestParam String typeIntervention,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        RendezVous rdv = garageService.createRendezVous(plageHoraireId, userId, serviceId, typeIntervention, date);
        return ResponseEntity.status(201).body(rdvMapper.toResponse(rdv));
    }

    @GetMapping("/rdv/mes")
    @Operation(summary = "Obtenir mes rendez-vous")
    public ResponseEntity<List<RendezVousDTOResponse>> getMesRendezVous(@RequestParam Long userId) {
        List<RendezVousDTOResponse> dtos = garageService.getMesRendezVous(userId).stream()
            .map(rdvMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/rdv/{id}/statut")
    @PreAuthorize("hasRole('TECHNICIEN') or hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour le statut d'un rendez-vous (TECHNICIEN/ADMIN)")
    public ResponseEntity<RendezVousDTOResponse> updateStatut(
            @PathVariable Long id,
            @RequestParam String statut) {
        return ResponseEntity.ok(rdvMapper.toResponse(garageService.updateStatutRdv(id, statut)));
    }

    @GetMapping("/planning/{techId}")
    @Operation(summary = "Obtenir le planning d'un technicien pour une semaine")
    public ResponseEntity<Map<DayOfWeek, List<RendezVousDTOResponse>>> getPlanningTechnicien(
            @PathVariable Long techId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semaine) {
        Map<DayOfWeek, List<RendezVous>> planning = garageService.getPlanningTechnicien(techId, semaine);
        
        Map<DayOfWeek, List<RendezVousDTOResponse>> dtoPlanning = planning.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().stream().map(rdvMapper::toResponse).collect(Collectors.toList())
            ));
            
        return ResponseEntity.ok(dtoPlanning);
    }

    @PutMapping("/techniciens/{techId}/planning/init")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Initialiser le planning default d'un technicien (60 créneaux)")
    public ResponseEntity<Void> initPlanning(@PathVariable Long techId) {
        plageHoraireService.initPlanningDefault(techId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/planning/global")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir le planning global (RDV + Interventions) pour l'admin")
    public ResponseEntity<?> getGlobalPlanning(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        try {
            return ResponseEntity.ok(garageService.getGlobalPlanning(debut, fin));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage(), "statut", 500));
        }
    }

    @PutMapping("/interventions/{ticketId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Affecter un ticket SAV à un technicien")
    public ResponseEntity<Void> assignIntervention(@PathVariable Long ticketId, @RequestParam Long techId) {
        garageService.assignTicketToTechnician(ticketId, techId);
        return ResponseEntity.ok().build();
    }
}
