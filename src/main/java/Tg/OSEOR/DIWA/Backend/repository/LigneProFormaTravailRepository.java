package Tg.OSEOR.DIWA.Backend.repository;

import Tg.OSEOR.DIWA.Backend.entity.LigneProFormaTravail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneProFormaTravailRepository extends JpaRepository<LigneProFormaTravail, Long> {
    List<LigneProFormaTravail> findByProFormaIdOrderByPositionAsc(Long proFormaId);
    boolean existsByProFormaIdAndStatutNot(Long proFormaId, LigneProFormaTravail.StatutLigne statut);
}
