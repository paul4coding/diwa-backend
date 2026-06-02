package Tg.OSEOR.DIWA.Backend.repository;

import Tg.OSEOR.DIWA.Backend.entity.ExceptionPlage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExceptionPlageRepository extends JpaRepository<ExceptionPlage, Long> {
    List<ExceptionPlage> findAllByDate(LocalDate date);
}
