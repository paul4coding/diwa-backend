package Tg.OSEOR.DIWA.Backend.repository.atelier;

import Tg.OSEOR.DIWA.Backend.entity.HistoriqueStatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoriqueStatutDemandeRepository extends JpaRepository<HistoriqueStatutDemande, Long> {
    List<HistoriqueStatutDemande> findByDemandeIdOrderByCreateDateAsc(Long demandeId);
}
