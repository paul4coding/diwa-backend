package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.dto.ChauffeurStatusDTO;
import Tg.OSEOR.DIWA.Backend.service.LogistiqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/logistique")
public class LogistiqueController {

    private final LogistiqueService logistiqueService;

    public LogistiqueController(LogistiqueService logistiqueService) {
        this.logistiqueService = logistiqueService;
    }

    @GetMapping("/chauffeurs/status")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_RECEPTIONNISTE')")
    public ResponseEntity<List<ChauffeurStatusDTO>> getChauffeursStatus() {
        return ResponseEntity.ok(logistiqueService.getChauffeursMonitoring());
    }
}
