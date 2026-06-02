package Tg.OSEOR.DIWA.Backend.repository.atelier;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionChauffeurRepository extends JpaRepository<MissionChauffeur, Long> {

    @Query("""
        SELECT m FROM MissionChauffeur m
        WHERE m.chauffeur.id = :chauffeurId
        AND m.statut NOT IN (
            Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur$StatutMission.TERMINEE,
            Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur$StatutMission.REFUSE_PAR_CLIENT
        )
        ORDER BY m.createDate DESC
        """)
    List<MissionChauffeur> findMissionsActives(@Param("chauffeurId") Long chauffeurId);

    List<MissionChauffeur> findByChauffeurIdOrderByCreateDateDesc(Long chauffeurId);

    List<MissionChauffeur> findByDemandeIdOrderByCreateDateDesc(Long demandeId);

    List<MissionChauffeur> findByDemandeIdIn(java.util.Collection<Long> demandeIds);

    @Query("""
        SELECT COUNT(m) FROM MissionChauffeur m
        WHERE m.demande.dateRecuperation = :date
        AND m.creneau.id = :plageId
        AND m.statut NOT IN ('TERMINEE', 'REFUSE_PAR_CLIENT')
        """)
    long countOccupiedChauffeursByDateAndPlage(@Param("date") java.time.LocalDate date, @Param("plageId") Long plageId);

    @Query("""
        SELECT u FROM User u 
        JOIN u.roles r 
        WHERE r.name = Tg.OSEOR.DIWA.Backend.security.enums.ERole.ROLE_CHAUFFEUR
        AND u.id NOT IN (
            SELECT m.chauffeur.id FROM MissionChauffeur m 
            WHERE m.statut NOT IN (
                Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur$StatutMission.TERMINEE,
                Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur$StatutMission.REFUSE_PAR_CLIENT
            )
        )
        """)
    List<User> findChauffeursDisponibles();
}
