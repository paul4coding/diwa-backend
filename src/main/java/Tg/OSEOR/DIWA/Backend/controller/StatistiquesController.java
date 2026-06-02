package Tg.OSEOR.DIWA.Backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.DashboardStatsDTO;
import Tg.OSEOR.DIWA.Backend.service.StatistiquesService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;

@RestController
@RequestMapping("api/v1/stats")
public class StatistiquesController {

    private final StatistiquesService statsService;

    public StatistiquesController(StatistiquesService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'CHEF_TECHNICIEN', 'ROLE_ADMIN', 'ROLE_RECEPTIONNISTE', 'ROLE_CHEF_TECHNICIEN')")
    public ResponseEntity<BaseResponse<DashboardStatsDTO>> getDashboardStats() {
        try {
            DashboardStatsDTO data = statsService.getDashboardStats();
            return ResponseEntity.ok(new BaseResponse<>(200, "Statistiques récupérées", data));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponse.error(500, "Erreur Stats: " + e.getMessage()));
        }
    }

    @GetMapping("/full")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE', 'CHEF_TECHNICIEN', 'ROLE_ADMIN', 'ROLE_RECEPTIONNISTE', 'ROLE_CHEF_TECHNICIEN')")
    public ResponseEntity<BaseResponse<DashboardStatsDTO>> getFullStats() {
        try {
            DashboardStatsDTO data = statsService.getDashboardStats();
            return ResponseEntity.ok(new BaseResponse<>(200, "Analyses complètes récupérées", data));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponse.error(500, "Erreur Analyses: " + e.getMessage()));
        }
    }
}
