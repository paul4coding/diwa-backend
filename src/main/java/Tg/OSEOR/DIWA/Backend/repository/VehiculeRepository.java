package Tg.OSEOR.DIWA.Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Tg.OSEOR.DIWA.Backend.entity.Vehicule;
import Tg.OSEOR.DIWA.Backend.entity.enums.MarqueEnum;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    List<Vehicule> findByMarque(MarqueEnum marque);

    List<Vehicule> findByStockGreaterThan(Integer threshold);

    boolean existsByModeleAndMarque(String modele, MarqueEnum marque);

    Optional<Vehicule> findByUuid(String uuid);

    Optional<Vehicule> findByImmatriculation(String immatriculation);
}
