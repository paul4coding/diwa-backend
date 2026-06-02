package Tg.OSEOR.DIWA.Backend.repository;

import Tg.OSEOR.DIWA.Backend.entity.Favoris;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavorisRepository extends JpaRepository<Favoris, Long> {
    List<Favoris> findByUser_Id(Long userId);
    Optional<Favoris> findByUser_IdAndVehicule_Id(Long userId, Long vehiculeId);
    boolean existsByUser_IdAndVehicule_Id(Long userId, Long vehiculeId);

    @Modifying
    @Transactional
    void deleteByUser_IdAndVehicule_Id(Long userId, Long vehiculeId);
}
