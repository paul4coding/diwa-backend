package Tg.OSEOR.DIWA.Backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import Tg.OSEOR.DIWA.Backend.entity.Commande;
import Tg.OSEOR.DIWA.Backend.entity.enums.StatutCommande;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    
    @Query("SELECT SUM(c.prixTotalTTC) FROM Commande c WHERE c.statut = :statut")
    Double sumTotalRevenue(@org.springframework.data.repository.query.Param("statut") StatutCommande statut);

    long countByStatut(StatutCommande statut);
    
    @Query("SELECT SUM(c.prixTotalTTC) FROM Commande c WHERE c.dateCommande BETWEEN :start AND :end AND c.statut = 'PAYE'")
    Double sumRevenueByDate(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end);

    @Query("SELECT DISTINCT c FROM Commande c LEFT JOIN FETCH c.user LEFT JOIN FETCH c.lignes")
    java.util.List<Commande> findAllWithEverything();
}
