package Tg.OSEOR.DIWA.Backend.repository;

import Tg.OSEOR.DIWA.Backend.entity.PlageHoraireDIWA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlageHoraireDIWARepository extends JpaRepository<PlageHoraireDIWA, Long> {
    List<PlageHoraireDIWA> findAllByActiveTrueOrderByOrdreAsc();
}
