package Tg.OSEOR.DIWA.Backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import Tg.OSEOR.DIWA.Backend.entity.PieceDetachee;
import java.util.List;

@Repository
public interface PieceDetacheeRepository extends JpaRepository<PieceDetachee, Long> {
    List<PieceDetachee> findByCategorieId(Long id);
    List<PieceDetachee> findByNomContainingIgnoreCaseOrReferenceContainingIgnoreCase(String nom, String reference);
    
    long countByQuantiteStockLessThanEqual(Integer threshold);
}
