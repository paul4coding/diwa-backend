package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.ChauffeurStatusDTO;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.security.enums.ERole;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import Tg.OSEOR.DIWA.Backend.repository.DemandeInterventionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class LogistiqueServiceImpl implements LogistiqueService {

    private final UserRepository userRepository;
    private final DemandeInterventionRepository demandeRepository;

    public LogistiqueServiceImpl(UserRepository userRepository, DemandeInterventionRepository demandeRepository) {
        this.userRepository = userRepository;
        this.demandeRepository = demandeRepository;
    }

    @Override
    public List<ChauffeurStatusDTO> getChauffeursMonitoring() {
        List<User> chauffeurs = userRepository.findByRolesName(ERole.ROLE_CHAUFFEUR);
        List<ChauffeurStatusDTO> monitoring = new ArrayList<>();

        // On récupère toutes les demandes qui pourraient occuper un chauffeur
        List<DemandeIntervention.StatutDemande> busyStatuses = List.of(
            DemandeIntervention.StatutDemande.CHAUFFEUR_ASSIGNE,
            DemandeIntervention.StatutDemande.VEHICULE_EN_TRANSIT
        );

        for (User c : chauffeurs) {
            String status = "LIBRE";
            String missionRef = null;
            String destination = null;

            // On cherche si ce chauffeur est assigné à une demande active (récupération)
            // Note: On pourrait aussi checker pour la livraison dans une version plus complète
            Optional<DemandeIntervention> activeMission = demandeRepository.findAll().stream()
                .filter(d -> d.getChauffeurRecuperation() != null && 
                             d.getChauffeurRecuperation().getId().equals(c.getId()) &&
                             busyStatuses.contains(d.getStatut()))
                .findFirst();

            if (activeMission.isPresent()) {
                status = activeMission.get().getStatut() == DemandeIntervention.StatutDemande.VEHICULE_EN_TRANSIT 
                         ? "EN_MISSION" : "ASSIGNE";
                missionRef = activeMission.get().getReference();
                destination = activeMission.get().getAdresseRecuperation();
            }

            long total = demandeRepository.countByChauffeurRecuperationId(c.getId()) + 
                         demandeRepository.countByChauffeurLivraisonId(c.getId());
            double score = (total > 0) ? 4.5 + (Math.random() * 0.5) : 5.0; 

            String displayNom = (c.getPrenom() != null ? c.getPrenom() : "") + " " + (c.getNom() != null ? c.getNom() : "");
            if (displayNom.trim().isEmpty()) displayNom = c.getUsername();

            java.time.LocalDateTime dateAssignation = activeMission.isPresent() ? activeMission.get().getUpdateDate() : null;

            monitoring.add(new ChauffeurStatusDTO(
                c.getId(),
                displayNom.trim(),
                c.getTelephone(),
                c.getEmail(),
                status,
                missionRef,
                destination,
                total,
                Math.round(score * 10.0) / 10.0,
                dateAssignation,
                c.getHeureDebutTravail() != null ? c.getHeureDebutTravail().toString() : "07:00",
                c.getHeureFinTravail() != null ? c.getHeureFinTravail().toString() : "18:00",
                activeMission.isPresent() ? activeMission.get().getId() : null
            ));
        }

        return monitoring;
    }
}
