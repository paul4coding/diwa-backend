package Tg.OSEOR.DIWA.Backend.repository;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Tg.OSEOR.DIWA.Backend.entity.PlageHoraire;

@Repository
public interface PlageHoraireRepository extends JpaRepository<PlageHoraire, Long> {

    List<PlageHoraire> findByTechnicienIdAndJourSemaine(Long technicienId, DayOfWeek jourSemaine);

    List<PlageHoraire> findByTechnicienIdAndEstDisponibleTrue(Long technicienId);

    void deleteByTechnicienId(Long technicienId);
}
