package Tg.OSEOR.DIWA.Backend.repository;

import Tg.OSEOR.DIWA.Backend.entity.CouponReduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponReductionRepository extends JpaRepository<CouponReduction, Long> {
    Optional<CouponReduction> findByCode(String code);
    boolean existsByCode(String code);
    List<CouponReduction> findAllByOrderByCreateDateDesc();
    List<CouponReduction> findByProFormaId(Long proFormaId);
}
