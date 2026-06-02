package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.DemandeDTO.DemandeCreateRequest;
import Tg.OSEOR.DIWA.Backend.dto.DemandeDTO.DemandeDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.DemandeDTO.EnregistrementVehiculeRequest;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.security.model.User;

import java.util.List;

public interface DemandeInterventionService {
    DemandeDTOResponse createDemande(DemandeCreateRequest request, String userEmail);
    DemandeDTOResponse enregistrerVehicule(String identifier, EnregistrementVehiculeRequest request, String chefTechEmail);
    DemandeDTOResponse validerEnregistrement(String identifier, String receptionnisteEmail);
    DemandeDTOResponse assignerTechnicien(String identifier, Long technicienId, String chefTechEmail);
    DemandeDTOResponse getByUuid(String uuid, String userEmail);
    List<DemandeDTOResponse> getMesDemandes(String userEmail);
    List<DemandeDTOResponse> getAllFiltrees(String statut, String urgence);
    DemandeDTOResponse enregistrerSortie(String identifier, String receptionnisteEmail);
    void deleteDemande(String identifier, String userEmail);
    
    // Scénario B : Arrivée Directe
    DemandeDTOResponse createDemandeDirecte(DemandeCreateRequest request, String auteurEmail);
    DemandeDTOResponse associerClient(String identifier, Long clientId, String receptionnisteEmail);
    
    // Scénario A : Chauffeurs
    DemandeDTOResponse assignerChauffeurRecuperation(String identifier, Long chauffeurId, String receptionnisteEmail);
    DemandeDTOResponse assignerChauffeurLivraison(String identifier, Long chauffeurId, String receptionnisteEmail);
    DemandeDTOResponse confirmerRecuperation(String identifier, String chauffeurEmail);
    DemandeDTOResponse confirmerArriveeGarage(String identifier, String chauffeurEmail);
    DemandeDTOResponse annulerMissionChauffeur(String identifier, String auteurEmail);
    
    DemandeDTOResponse planifierRecuperation(String identifier, java.time.LocalDate date, Long creneauId, String receptionnisteEmail);
    
    void ajouterHistorique(DemandeIntervention demande, 
                          DemandeIntervention.StatutDemande ancien, 
                          DemandeIntervention.StatutDemande nouveau, 
                          User auteur, 
                          String commentaire);
}
