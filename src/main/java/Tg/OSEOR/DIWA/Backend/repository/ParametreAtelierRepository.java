package Tg.OSEOR.DIWA.Backend.repository;

import Tg.OSEOR.DIWA.Backend.entity.ParametreAtelier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ParametreAtelierRepository extends JpaRepository<ParametreAtelier, Long> {
    
    @Query("SELECT p FROM ParametreAtelier p")
    Optional<ParametreAtelier> findParametre();
}
