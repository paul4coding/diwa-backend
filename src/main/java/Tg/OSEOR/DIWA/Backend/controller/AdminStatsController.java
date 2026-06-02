package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.dto.DashboardStatsDTO;
import Tg.OSEOR.DIWA.Backend.repository.CommandeRepository;
import Tg.OSEOR.DIWA.Backend.repository.RendezVousRepository;
import Tg.OSEOR.DIWA.Backend.repository.TechnicienRepository;
import Tg.OSEOR.DIWA.Backend.repository.TicketSAVRepository;
import Tg.OSEOR.DIWA.Backend.repository.VehiculeRepository;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/admin/stats")
public class AdminStatsController {

    @Autowired
    private VehiculeRepository vehiculeRepository;
    
    @Autowired
    private CommandeRepository commandeRepository;
    
    @Autowired
    private RendezVousRepository rendezVousRepository;
    
    @Autowired
    private TicketSAVRepository ticketSAVRepository;
    
    @Autowired
    private TechnicienRepository technicienRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_RECEPTIONNISTE')")
    public ResponseEntity<BaseResponse<DashboardStatsDTO>> getStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        stats.setTotalRevenue(0.0); // À implémenter avec Commande
        stats.setPendingOrders(commandeRepository.count()); // Simplifié pour le test
        stats.setActiveAppointments(rendezVousRepository.count());
        stats.setPendingSAV(ticketSAVRepository.count());
        stats.setLowStockCount(vehiculeRepository.findAll().stream()
                .filter(v -> v.getStock() != null && v.getStock() < 5)
                .count());
        
        stats.setStatusDistribution(new HashMap<>());
        
        return ResponseEntity.ok(new BaseResponse<>(200, "Statistiques récupérées", stats));
    }
}
