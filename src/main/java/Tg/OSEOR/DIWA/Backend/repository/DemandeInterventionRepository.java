package Tg.OSEOR.DIWA.Backend.repository;

import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeInterventionRepository extends JpaRepository<DemandeIntervention, Long> {
    List<DemandeIntervention> findByClientIdOrderByCreateDateDesc(Long clientId);
    List<DemandeIntervention> findByStatutOrderByCreateDateDesc(DemandeIntervention.StatutDemande statut);
    Optional<DemandeIntervention> findByReference(String reference);
    Optional<DemandeIntervention> findByUuid(String uuid);

    List<DemandeIntervention> findByChefTechnicienIdOrderByCreateDateDesc(Long chefTechId);
    List<DemandeIntervention> findByTechnicienIdOrderByCreateDateDesc(Long techId);
    
    long countByStatut(DemandeIntervention.StatutDemande statut);

    @Query("""
        SELECT d FROM DemandeIntervention d
        WHERE (:statut IS NULL OR d.statut = :statut)
        AND (:urg IS NULL OR d.urgence = :urg)
        ORDER BY
          CASE d.urgence WHEN 'TRES_URGENTE' THEN 0 WHEN 'URGENTE' THEN 1 ELSE 2 END,
          d.createDate DESC
        """)
    List<DemandeIntervention> findAllFiltrees(
        @Param("statut") DemandeIntervention.StatutDemande statut,
        @Param("urg") DemandeIntervention.UrgenceDemande urg
    );
    long countByChauffeurRecuperationId(Long chauffeurId);
    long countByChauffeurLivraisonId(Long chauffeurId);
}
