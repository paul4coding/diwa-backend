package Tg.OSEOR.DIWA.Backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import Tg.OSEOR.DIWA.Backend.entity.ConfigurationVehicule;
import Tg.OSEOR.DIWA.Backend.entity.ConfigurationVehicule;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import java.util.List;

public interface ConfigurationVehiculeRepository extends JpaRepository<ConfigurationVehicule, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT c FROM ConfigurationVehicule c " +
           "LEFT JOIN FETCH c.vehicule " +
           "LEFT JOIN FETCH c.finition " +
           "LEFT JOIN FETCH c.motorisation " +
           "WHERE c.user = :user")
    List<ConfigurationVehicule> findByUser(@org.springframework.data.repository.query.Param("user") User user);
}
