package Tg.OSEOR.DIWA.Backend.repository.atelier;

import Tg.OSEOR.DIWA.Backend.entity.GammePrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GammePrixRepository extends JpaRepository<GammePrix, Long> {
    List<GammePrix> findByCategorieIgnoreCase(String categorie);
    List<GammePrix> findByDesignationContainingIgnoreCase(String terme);
}
