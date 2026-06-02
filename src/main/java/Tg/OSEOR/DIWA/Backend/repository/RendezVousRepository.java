package Tg.OSEOR.DIWA.Backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Tg.OSEOR.DIWA.Backend.entity.RendezVous;
import Tg.OSEOR.DIWA.Backend.entity.enums.StatutRDV;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    long countByStatut(StatutRDV statut);

    List<RendezVous> findByUserId(Long userId);

    List<RendezVous> findByTechnicienIdAndDate(Long technicienId, LocalDate date);

    List<RendezVous> findByTechnicienIdAndDateBetween(Long technicienId, LocalDate debut, LocalDate fin);
    
    List<RendezVous> findByDateBetween(LocalDate debut, LocalDate fin);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT r FROM RendezVous r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.service LEFT JOIN FETCH r.technicien LEFT JOIN FETCH r.vehicule LEFT JOIN FETCH r.creneau WHERE r.id = :id")
    java.util.Optional<RendezVous> findByIdWithEverything(Long id);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT r FROM RendezVous r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.service LEFT JOIN FETCH r.technicien LEFT JOIN FETCH r.vehicule LEFT JOIN FETCH r.creneau")
    List<RendezVous> findAllWithEverything();

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT r FROM RendezVous r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.service LEFT JOIN FETCH r.technicien LEFT JOIN FETCH r.vehicule LEFT JOIN FETCH r.creneau WHERE r.date BETWEEN :debut AND :fin")
    List<RendezVous> findByDateBetweenWithEverything(LocalDate debut, LocalDate fin);

    boolean existsByCreneauIdAndDate(Long creneauId, LocalDate date);
}
