package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.entity.ProForma;
import Tg.OSEOR.DIWA.Backend.repository.DemandeInterventionRepository;
import Tg.OSEOR.DIWA.Backend.service.FactureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/factures")
@Tag(name = "Factures", description = "Téléchargement des factures finales (Sprint 6)")
public class FactureController {

    @Autowired private FactureService           factureService;
    @Autowired private DemandeInterventionRepository demandeRepo;

    /**
     * Télécharge la facture PDF d'une demande clôturée.
     * Accès : CLIENT propriétaire, RECEPTIONNISTE, CHEF_TECHNICIEN, ADMIN.
     */
    @GetMapping("/{demandeId}")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT','ROLE_USER','ROLE_RECEPTIONNISTE','ROLE_CHEF_TECHNICIEN','ROLE_ADMIN')")
    @Operation(summary = "Télécharger la facture d'un dossier clôturé")
    public ResponseEntity<byte[]> telecharger(
            @PathVariable Long demandeId,
            Authentication auth) {

        byte[] pdf = factureService.telecharger(demandeId, auth.getName());

        // Nom de fichier pour le Content-Disposition
        String ref = demandeRepo.findById(demandeId)
            .map(DemandeIntervention::getReference)
            .orElse("FACT-" + demandeId);

        String filename = "Facture_DIWA_" + ref.replaceAll("[^a-zA-Z0-9_-]", "_") + ".pdf";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    /**
     * Vérifie si la facture existe pour une demande donnée.
     * Retourne {"disponible": true/false, "genereeAt": "..."}.
     */
    @GetMapping("/{demandeId}/statut")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT','ROLE_USER','ROLE_RECEPTIONNISTE','ROLE_CHEF_TECHNICIEN','ROLE_ADMIN')")
    @Operation(summary = "Vérifier la disponibilité de la facture")
    public ResponseEntity<?> statut(@PathVariable Long demandeId) {
        DemandeIntervention demande = demandeRepo.findById(demandeId)
            .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        ProForma pf = demande.getProForma();
        boolean disponible = pf != null && pf.getFactureUrl() != null;
        String genereeAt  = (pf != null && pf.getFactureGenereeAt() != null)
            ? pf.getFactureGenereeAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            : null;

        return ResponseEntity.ok(java.util.Map.of(
            "disponible", disponible,
            "genereeAt",  genereeAt != null ? genereeAt : ""
        ));
    }
}
