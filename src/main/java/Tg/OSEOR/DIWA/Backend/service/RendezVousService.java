package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO.RendezVousDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO.RendezVousDTOResponse;

public interface RendezVousService {
    RendezVousDTOResponse demanderRendezVous(RendezVousDTORequest request, Long userId);
    RendezVousDTOResponse validerRendezVous(Long id, Long creneauId);
    RendezVousDTOResponse planifier(Long id, String date, String heure, Long techId);
    RendezVousDTOResponse annulerRendezVous(Long id);
    RendezVousDTOResponse validerRendezVousAdmin(Long id);
    RendezVousDTOResponse rejeterRendezVousAdmin(Long id);
    List<RendezVousDTOResponse> listerTous();
    List<RendezVousDTOResponse> listerEnAttente();
    List<RendezVousDTOResponse> listerMesRendezVous(Long userId);
    RendezVousDTOResponse getById(Long id);
    Tg.OSEOR.DIWA.Backend.entity.RendezVous getEntityById(Long id);
    RendezVousDTOResponse reinitialiserPlanification(Long id);
}
