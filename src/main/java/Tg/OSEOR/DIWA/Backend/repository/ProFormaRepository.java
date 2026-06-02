package Tg.OSEOR.DIWA.Backend.repository;

import Tg.OSEOR.DIWA.Backend.entity.ProForma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProFormaRepository extends JpaRepository<ProForma, Long> {
    List<ProForma> findByStatut(ProForma.StatutProForma statut);
    Optional<ProForma> findByDemandeId(Long demandeId);
    Optional<ProForma> findByReference(String reference);
    List<ProForma> findByStatutOrderByCreateDateDesc(ProForma.StatutProForma statut);
}
