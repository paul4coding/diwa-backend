package Tg.OSEOR.DIWA.Backend.repository;

import Tg.OSEOR.DIWA.Backend.entity.LigneProFormaMainOeuvre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneProFormaMainOeuvreRepository extends JpaRepository<LigneProFormaMainOeuvre, Long> {
    List<LigneProFormaMainOeuvre> findByProFormaIdOrderByPositionAsc(Long proFormaId);
}
