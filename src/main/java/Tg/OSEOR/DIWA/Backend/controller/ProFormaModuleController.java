package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.AjoutPrixRequest;
import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.ProFormaDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.ProFormaV1Request;
import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.SelectionClientRequest;
import Tg.OSEOR.DIWA.Backend.service.ProFormaServiceModule;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/proformas")
public class ProFormaModuleController {

    private final ProFormaServiceModule proFormaService;

    public ProFormaModuleController(ProFormaServiceModule proFormaService) {
        this.proFormaService = proFormaService;
    }

    // POST — Chef Technicien crée la V1
    @PostMapping
    @PreAuthorize("hasRole('ROLE_CHEF_TECHNICIEN')")
    public ResponseEntity<?> creerV1(@RequestBody ProFormaV1Request request, Authentication auth) {
        try {
            System.out.println("[DIWA] Création/Mise à jour Pro Forma V1 pour Demande: " + request.getDemandeId());
            ProFormaDTOResponse response = proFormaService.creerV1(request.getDemandeId(), request, auth.getName());
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            System.err.println("[DIWA-ERROR] " + e.getMessage());
            throw e;
        }
    }

    // GET /:id/technicien — Version sans prix
    @GetMapping("/{id}/technicien")
    @PreAuthorize("hasRole('ROLE_CHEF_TECHNICIEN')")
    public ResponseEntity<ProFormaDTOResponse> getVersionTechnicien(@PathVariable Long id) {
        return ResponseEntity.ok(proFormaService.getLignesTechnicien(id));
    }

    @GetMapping("/{id}/receptionniste")
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<ProFormaDTOResponse> getVersionReceptionniste(@PathVariable Long id) {
        return ResponseEntity.ok(proFormaService.getVersionReceptionniste(id));
    }

    // PUT /:id/prix — Réceptionniste ajoute les prix
    @PutMapping("/{id}/prix")
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<ProFormaDTOResponse> ajouterPrix(@PathVariable Long id, @Valid @RequestBody AjoutPrixRequest request, Authentication auth) {
        return ResponseEntity.ok(proFormaService.ajouterPrix(id, request, auth.getName()));
    }

    // PUT /:id/envoyer-client — Réceptionniste envoie au client
    @PutMapping("/{id}/envoyer-client")
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<ProFormaDTOResponse> envoyerAuClient(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(proFormaService.envoyerAuClient(id, auth.getName()));
    }

    // GET /:id/client — Client voit sa version (sans heures MO)
    @GetMapping("/{id}/client")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_USER')")
    public ResponseEntity<ProFormaDTOResponse> getVersionClient(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(proFormaService.getVersionClient(id, auth.getName()));
    }

    // PUT /:id/selection — Client soumet sa sélection
    @PutMapping("/{id}/selection")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_USER')")
    public ResponseEntity<ProFormaDTOResponse> soumettreSelection(@PathVariable Long id, @RequestBody SelectionClientRequest request, Authentication auth) {
        return ResponseEntity.ok(proFormaService.soumettreSelection(id, request, auth.getName()));
    }

    // PUT /:id/valider — Réceptionniste valide le pro forma final
    @PutMapping("/{id}/valider")
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<ProFormaDTOResponse> valider(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(proFormaService.validerSelectionClient(id, auth.getName()));
    }

    // PUT /:id/confirmer — Client confirme → génère PDFs
    @PutMapping("/{id}/confirmer")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_USER')")
    public ResponseEntity<ProFormaDTOResponse> confirmer(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(proFormaService.confirmerProForma(id, auth.getName()));
    }

    // PUT /:id/mettre-a-jour-livraison — Réceptionniste fixe les frais de livraison (quand client veut livraison)
    @PutMapping("/{id}/mettre-a-jour-livraison")
    @PreAuthorize("hasRole('ROLE_RECEPTIONNISTE')")
    public ResponseEntity<ProFormaDTOResponse> mettreAJourLivraison(
            @PathVariable Long id,
            @RequestParam BigDecimal fraisLivraison,
            Authentication auth) {
        return ResponseEntity.ok(proFormaService.mettreAJourLivraison(id, fraisLivraison, auth.getName()));
    }

    // PUT /:id/confirmer-final — Client confirme le devis final avec frais de livraison
    @PutMapping("/{id}/confirmer-final")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_USER')")
    public ResponseEntity<ProFormaDTOResponse> confirmerFinal(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(proFormaService.confirmerFinal(id, auth.getName()));
    }

    // PUT /lignes/:ligneId/statut — Chef Tech met à jour l'avancement
    @PutMapping("/lignes/{ligneId}/statut")
    @PreAuthorize("hasRole('ROLE_CHEF_TECHNICIEN')")
    public ResponseEntity<Void> updateStatutLigne(@PathVariable Long ligneId, @RequestParam String statut, Authentication auth) {
        proFormaService.updateStatutLigne(ligneId, statut, auth.getName());
        return ResponseEntity.ok().build();
    }
}
