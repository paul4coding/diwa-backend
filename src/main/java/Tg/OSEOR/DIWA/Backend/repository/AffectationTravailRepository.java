package Tg.OSEOR.DIWA.Backend.repository;

import Tg.OSEOR.DIWA.Backend.entity.AffectationTravail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AffectationTravailRepository extends JpaRepository<AffectationTravail, Long> {
    List<AffectationTravail> findByTechnicienId(Long technicienId);
    List<AffectationTravail> findByProFormaId(Long proFormaId);
    List<AffectationTravail> findByDemandeId(Long demandeId);
}
