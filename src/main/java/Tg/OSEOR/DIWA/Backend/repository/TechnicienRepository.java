package Tg.OSEOR.DIWA.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Tg.OSEOR.DIWA.Backend.entity.Technicien;

@Repository
public interface TechnicienRepository extends JpaRepository<Technicien, Long> {

    List<Technicien> findByActifTrue();

    List<Technicien> findBySpecialiteContainingIgnoreCase(String specialite);

    boolean existsByEmail(String email);
}
