package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.MissionChauffeurDTO.*;
import Tg.OSEOR.DIWA.Backend.dto.UserDTO.UserDTOResponse;
import Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur;

import java.util.List;

public interface MissionChauffeurService {
    MissionDTOResponse creerMission(Long demandeId, Long chauffeurId, MissionChauffeur.TypeMission type, String receptionnisteEmail);
    List<UserDTOResponse> getChauffeursDisponibles();
    MissionDTOResponse getMissionActive(String chauffeurEmail);
    MissionDTOResponse marquerDepart(Long missionId, String chauffeurEmail);
    MissionDTOResponse marquerArriveeClient(Long missionId, String chauffeurEmail);
    MissionDTOResponse soumettreChecking(Long missionId, CheckingRequest req, String chauffeurEmail);
    MissionDTOResponse approuverChecking(Long missionId, Boolean approuve, String motifRefus, String clientEmail);
    MissionDTOResponse marquerQuitterClient(Long missionId, String identifier);
    MissionDTOResponse marquerArriveeDiwa(Long missionId, String chauffeurEmail);
    MissionDTOResponse soumettreReception(Long missionId, ReceptionRequest req, String chauffeurEmail);
    MissionDTOResponse confirmerLivraison(Long missionId, ReceptionRequest req, String chauffeurEmail);
    List<MissionDTOResponse> getParDemande(Long demandeId);
    MissionDTOResponse getLivraisonForClient(Long demandeId, String identifier);
}
