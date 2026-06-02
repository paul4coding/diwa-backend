package Tg.OSEOR.DIWA.Backend.mappers;

import Tg.OSEOR.DIWA.Backend.dto.MissionChauffeurDTO.MissionDTOResponse;
import Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import org.springframework.stereotype.Component;

@Component
public class MissionMapper {

    public MissionDTOResponse toDTO(MissionChauffeur mission) {
        if (mission == null) return null;

        MissionDTOResponse dto = new MissionDTOResponse();
        dto.setId(mission.getId());
        dto.setType(mission.getType().name());
        dto.setStatut(mission.getStatut().name());
        dto.setHeureDepart(mission.getHeureDepart() != null ? mission.getHeureDepart().toString() : null);
        dto.setHeureArriveeClient(mission.getHeureArriveeClient() != null ? mission.getHeureArriveeClient().toString() : null);
        dto.setHeureApprobationClient(mission.getHeureApprobationClient() != null ? mission.getHeureApprobationClient().toString() : null);
        dto.setHeureArriveeDiwa(mission.getHeureArriveeDiwa() != null ? mission.getHeureArriveeDiwa().toString() : null);
        
        dto.setCheckingPhotoAvant(mission.getCheckingPhotoAvant());
        dto.setCheckingPhotoArriere(mission.getCheckingPhotoArriere());
        dto.setCheckingPhotoGauche(mission.getCheckingPhotoGauche());
        dto.setCheckingPhotoDroit(mission.getCheckingPhotoDroit());
        dto.setCheckingVideoUrl(mission.getCheckingVideoUrl());
        dto.setCheckingObservations(mission.getCheckingObservations());
        dto.setCheckingComplet(mission.getCheckingComplet());
        
        dto.setClientApprouve(mission.getClientApprouve());
        dto.setMotifRefusClient(mission.getMotifRefusClient());
        
        dto.setReceptionPhotoAvant(mission.getReceptionPhotoAvant());
        dto.setReceptionPhotoArriere(mission.getReceptionPhotoArriere());
        dto.setReceptionPhotoGauche(mission.getReceptionPhotoGauche());
        dto.setReceptionPhotoDroit(mission.getReceptionPhotoDroit());
        dto.setReceptionObservations(mission.getReceptionObservations());
        dto.setReceptionComplete(mission.getReceptionComplete());
        
        dto.setDistanceKm(mission.getDistanceKm());

        if (mission.getChauffeur() != null) {
            dto.setChauffeurNom(mission.getChauffeur().getNom());
            dto.setChauffeurPrenom(mission.getChauffeur().getPrenom());
            dto.setChauffeurTelephone(mission.getChauffeur().getTelephone());
        }

        DemandeIntervention demande = mission.getDemande();
        if (demande != null) {
            dto.setDemandeReference(demande.getReference());
            dto.setDateMission(demande.getDateRecuperation() != null ? demande.getDateRecuperation().toString() : null);
            dto.setCreneauLibelle(mission.getCreneau() != null ? mission.getCreneau().getLibelle() : 
                (demande.getCreneauSouhaite() != null ? demande.getCreneauSouhaite().getLibelle() : null));
            dto.setAdresse(mission.getType() == MissionChauffeur.TypeMission.RECUPERATION ? 
                demande.getAdresseRecuperation() : demande.getAdresseLivraison());
            dto.setContact(demande.getContactRecuperation()); // Simplifié

            if (demande.getClient() != null) {
                MissionDTOResponse.ClientInfo clientInfo = new MissionDTOResponse.ClientInfo();
                clientInfo.setNom(demande.getClient().getNom());
                clientInfo.setPrenom(demande.getClient().getPrenom());
                clientInfo.setTelephone(demande.getClient().getTelephone());
                dto.setClient(clientInfo);
            }

            MissionDTOResponse.VehiculeInfo vehInfo = new MissionDTOResponse.VehiculeInfo();
            vehInfo.setMarque(demande.getVehiculeMarque());
            vehInfo.setModele(demande.getVehiculeModele());
            vehInfo.setImmatriculation(demande.getVehiculeImmatriculation());
            vehInfo.setCouleur(demande.getVehiculeCouleur());
            dto.setVehicule(vehInfo);
        }

        return dto;
    }
}
