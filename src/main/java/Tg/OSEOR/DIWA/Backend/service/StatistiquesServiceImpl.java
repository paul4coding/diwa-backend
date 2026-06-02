package Tg.OSEOR.DIWA.Backend.service;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.DashboardStatsDTO;
import Tg.OSEOR.DIWA.Backend.entity.enums.StatutCommande;
import Tg.OSEOR.DIWA.Backend.entity.enums.StatutRDV;
import Tg.OSEOR.DIWA.Backend.entity.enums.StatutTicket;
import Tg.OSEOR.DIWA.Backend.repository.CommandeRepository;
import Tg.OSEOR.DIWA.Backend.repository.PieceDetacheeRepository;
import Tg.OSEOR.DIWA.Backend.repository.RendezVousRepository;
import Tg.OSEOR.DIWA.Backend.repository.TicketSAVRepository;

@Service
@Transactional(readOnly = true)
public class StatistiquesServiceImpl implements StatistiquesService {

    private final CommandeRepository commandeRepo;
    private final RendezVousRepository rdvRepo;
    private final TicketSAVRepository ticketRepo;
    private final PieceDetacheeRepository pieceRepo;

    public StatistiquesServiceImpl(CommandeRepository commandeRepo, RendezVousRepository rdvRepo, 
                                   TicketSAVRepository ticketRepo, PieceDetacheeRepository pieceRepo) {
        this.commandeRepo = commandeRepo;
        this.rdvRepo = rdvRepo;
        this.ticketRepo = ticketRepo;
        this.pieceRepo = pieceRepo;
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO dto = new DashboardStatsDTO();
        
        Double revenue = commandeRepo.sumTotalRevenue(StatutCommande.PAYE);
        dto.setTotalRevenue(revenue != null ? revenue : 0.0);
        
        dto.setPendingOrders(commandeRepo.countByStatut(StatutCommande.EN_PREPARATION));
        dto.setActiveAppointments(rdvRepo.countByStatut(StatutRDV.PLANIFIE));
        dto.setPendingSAV(ticketRepo.countByStatut(StatutTicket.OUVERT) + ticketRepo.countByStatut(StatutTicket.EN_COURS));
        dto.setLowStockCount(pieceRepo.countByQuantiteStockLessThanEqual(3));
        
        Map<String, Long> distribution = new HashMap<>();
        distribution.put("Ventes", commandeRepo.count());
        distribution.put("SAV", ticketRepo.count());
        distribution.put("RDV", rdvRepo.count());
        dto.setStatusDistribution(distribution);

        // Performance Hebdomadaire (CA réel par jour sur les 7 derniers jours)
        java.util.List<Double> perf = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            Double dailyRev = commandeRepo.sumRevenueByDate(day.atStartOfDay(), day.atTime(23, 59, 59));
            perf.add(dailyRev != null ? dailyRev : 0.0);
        }
        dto.setWeeklyPerformance(perf);

        // Activités Récentes (avec temps relatif simulé mais basé sur données réelles)
        java.util.List<Tg.OSEOR.DIWA.Backend.dto.ActivityDTO> activities = new java.util.ArrayList<>();
        
        try {
            rdvRepo.findAllWithEverything().stream()
                .filter(r -> r.getId() != null)
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .limit(3)
                .forEach(r -> {
                    String libelle = (r.getService() != null) ? r.getService().getLibelle() : "Service";
                    String client = (r.getUser() != null) ? (r.getUser().getPrenom() + " " + r.getUser().getNom()) : "Client";
                    activities.add(new Tg.OSEOR.DIWA.Backend.dto.ActivityDTO("RDV", "RDV " + libelle + " - " + client, "Aujourd'hui", "#10b981"));
                });
        } catch (Exception e) {
            System.err.println("Error fetching RDV stats: " + e.getMessage());
        }
        
        try {
            commandeRepo.findAll().stream()
                .filter(c -> c.getId() != null)
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .limit(2)
                .forEach(c -> {
                    Double total = c.getPrixTotalTTC() != null ? c.getPrixTotalTTC() : 0.0;
                    activities.add(new Tg.OSEOR.DIWA.Backend.dto.ActivityDTO("VENTE", "Commande #" + c.getId() + " - " + total + " FCFA", "Hier", "#3b82f6"));
                });
        } catch (Exception e) {
            System.err.println("Error fetching VENTE stats: " + e.getMessage());
        }

        dto.setRecentActivities(activities);
        
        return dto;
    }
}
