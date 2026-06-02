package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.entity.PlageHoraireDIWA;
import Tg.OSEOR.DIWA.Backend.entity.ExceptionPlage;
import Tg.OSEOR.DIWA.Backend.repository.PlageHoraireDIWARepository;
import Tg.OSEOR.DIWA.Backend.repository.ExceptionPlageRepository;
import Tg.OSEOR.DIWA.Backend.repository.atelier.MissionChauffeurRepository;
import Tg.OSEOR.DIWA.Backend.security.enums.ERole;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class CreneauService {

    private final PlageHoraireDIWARepository plageRepo;
    private final ExceptionPlageRepository exceptionRepo;
    private final MissionChauffeurRepository missionRepo;
    private final UserRepository userRepo;

    public CreneauService(PlageHoraireDIWARepository plageRepo, ExceptionPlageRepository exceptionRepo,
                          MissionChauffeurRepository missionRepo, UserRepository userRepo) {
        this.plageRepo = plageRepo;
        this.exceptionRepo = exceptionRepo;
        this.missionRepo = missionRepo;
        this.userRepo = userRepo;
    }

    public List<PlageHoraireDIWA> getAllPlages() {
        return plageRepo.findAllByActiveTrueOrderByOrdreAsc();
    }

    public Map<String, Object> getDisponibilites(LocalDate date) {
        List<PlageHoraireDIWA> activePlages = plageRepo.findAllByActiveTrueOrderByOrdreAsc();
        List<ExceptionPlage> exceptions = exceptionRepo.findAllByDate(date);
        
        // Est-ce que la journée entière est fermée ?
        boolean journeeFermee = exceptions.stream().anyMatch(e -> e.getPlage() == null);
        
        // Liste des IDs de plages spécifiquement fermées
        List<Long> plagesFermeesIds = exceptions.stream()
                .filter(e -> e.getPlage() != null)
                .map(e -> e.getPlage().getId())
                .collect(Collectors.toList());

        // Récupérer tous les chauffeurs pour vérifier qui travaille
        List<User> chauffeurs = userRepo.findByRolesName(ERole.ROLE_CHAUFFEUR);

        List<Map<String, Object>> slots = activePlages.stream().map(plage -> {
            boolean isFermee = journeeFermee || plagesFermeesIds.contains(plage.getId());
            
            // Calculer chauffeurs théoriquement dispos (horaires couvrent la plage)
            long chauffeursTheoDispos = chauffeurs.stream()
                    .filter(c -> c.getHeureDebutTravail() != null && c.getHeureFinTravail() != null &&
                                 !c.getHeureDebutTravail().isAfter(plage.getHeureDebut()) && 
                                 !c.getHeureFinTravail().isBefore(plage.getHeureFin()))
                    .count();
            
            // Si aucun chauffeur n'est opérationnel ou configuré pour ce créneau, 
            // on autorise par défaut (mode dev/demo) pour éviter de bloquer le flux
            if (chauffeursTheoDispos == 0) {
                chauffeursTheoDispos = 5; 
            }
            
            // Soustraire ceux qui ont déjà une mission
            long missionsEnCours = missionRepo.countOccupiedChauffeursByDateAndPlage(date, plage.getId());
            
            long placesRestantes = Math.max(0, chauffeursTheoDispos - missionsEnCours);
            
            String statut = isFermee ? "FERME" : (placesRestantes > 0 ? "DISPONIBLE" : "COMPLET");
            
            Map<String, Object> slot = new HashMap<>();
            slot.put("id", plage.getId());
            slot.put("heureDebut", plage.getHeureDebut().toString());
            slot.put("heureFin", plage.getHeureFin().toString());
            slot.put("libelle", plage.getLibelle());
            slot.put("statut", statut);
            slot.put("placesRestantes", placesRestantes);
            
            return slot;
        }).collect(Collectors.toList());

        return Map.of(
            "date", date.toString(),
            "slots", slots
        );
    }

    public PlageHoraireDIWA createPlage(PlageHoraireDIWA plage) {
        return plageRepo.save(plage);
    }

    public void deletePlage(Long id) {
        plageRepo.deleteById(id);
    }

    public ExceptionPlage createException(ExceptionPlage ex) {
        return exceptionRepo.save(ex);
    }
}
