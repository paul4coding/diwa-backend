package Tg.OSEOR.DIWA.Backend.mappers;

import Tg.OSEOR.DIWA.Backend.dto.DemandeDTO.DemandeDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemandeInterventionMapper {

    @Autowired
    private RendezVousMapper rdvMapper;

    @Autowired
    private Tg.OSEOR.DIWA.Backend.repository.atelier.MissionChauffeurRepository missionRepo;

    private String getFullName(Tg.OSEOR.DIWA.Backend.security.model.User u) {
        if (u == null) return null;
        String name = (u.getPrenom() != null ? u.getPrenom() : "") + " " + (u.getNom() != null ? u.getNom() : "");
        return name.trim().isEmpty() ? u.getUsername() : name.trim();
    }

    public DemandeDTOResponse toDTO(DemandeIntervention d) {
        if (d == null) return null;
        DemandeDTOResponse dto = new DemandeDTOResponse();
        dto.setId(d.getId());
        dto.setUuid(d.getUuid());
        dto.setReference(d.getReference());
        dto.setStatut(d.getStatut() != null ? d.getStatut().name() : null);
        dto.setUrgence(d.getUrgence() != null ? d.getUrgence().name() : null);
        dto.setDescriptionProbleme(d.getDescriptionProbleme());
        dto.setVehiculeImmatriculation(d.getVehiculeImmatriculation());
        dto.setVehiculeNumeroChassis(d.getVehiculeNumeroChassis());
        dto.setVehiculeMarque(d.getVehiculeMarque());
        dto.setVehiculeModele(d.getVehiculeModele());
        dto.setVehiculeAnnee(d.getVehiculeAnnee());
        dto.setVehiculeKilometrage(d.getVehiculeKilometrage());
        dto.setVehiculeCouleur(d.getVehiculeCouleur());
        dto.setVehiculeCarburant(d.getVehiculeCarburant());
        dto.setVehiculeBoiteVitesse(d.getVehiculeBoiteVitesse());
        dto.setPhotoCarteGriseUrl(d.getPhotoCarteGriseUrl());
        dto.setPhotoPlaqueUrl(d.getPhotoPlaqueUrl());
        dto.setPhotosUrlsClient(d.getPhotosUrlsClient());
        
        dto.setDemandeRecuperation(d.getDemandeRecuperation());
        dto.setAdresseRecuperation(d.getAdresseRecuperation());
        dto.setContactRecuperation(d.getContactRecuperation());
        dto.setFraisRecuperation(d.getFraisRecuperation());
        dto.setDateRecuperation(d.getDateRecuperation());
        dto.setCreneauLibelle(d.getCreneauSouhaite() != null ? d.getCreneauSouhaite().getLibelle() : null);
        dto.setNoteDisponibiliteClient(d.getNoteDisponibiliteClient());
        
        dto.setDemandeVisite(d.getDemandeVisite());
        dto.setAdresseVisite(d.getAdresseVisite());
        dto.setCreneauVisite(rdvMapper.toResponse(d.getCreneauVisite()));
        
        dto.setDemandeLivraison(d.getDemandeLivraison());
        dto.setAdresseLivraison(d.getAdresseLivraison());
        dto.setFraisLivraison(d.getFraisLivraison());
        
        dto.setDateHeureReception(d.getDateHeureReception());
        dto.setDateHeureSortie(d.getDateHeureSortie());
        dto.setVehiculeDiwa(d.getVehiculeDiwa());
        dto.setDiagnosticGratuit(d.getDiagnosticGratuit());
        dto.setObservationsArrivee(d.getObservationsArrivee());
        dto.setCreateDate(d.getCreateDate());

        if (d.getClient() != null) {
            dto.setClientNom(getFullName(d.getClient()));
            dto.setClientEmail(d.getClient().getEmail());
            dto.setClientTel(d.getClient().getTelephone());
        }
        if (d.getReceptionniste() != null) {
            dto.setReceptionnisteNom(getFullName(d.getReceptionniste()));
        }
        if (d.getChefTechnicien() != null) {
            dto.setChefTechnicienNom(getFullName(d.getChefTechnicien()));
        }
        if (d.getTechnicien() != null) {
            dto.setTechnicienNom(getFullName(d.getTechnicien()));
        }
        if (d.getChauffeurRecuperation() != null) {
            dto.setChauffeurRecuperationNom(getFullName(d.getChauffeurRecuperation()));
            dto.setChauffeurRecuperationTel(d.getChauffeurRecuperation().getTelephone());
            dto.setChauffeurRecuperationEmail(d.getChauffeurRecuperation().getEmail());
        }
        if (d.getChauffeurLivraison() != null) {
            dto.setChauffeurLivraisonNom(getFullName(d.getChauffeurLivraison()));
            dto.setChauffeurLivraisonTel(d.getChauffeurLivraison().getTelephone());
            dto.setChauffeurLivraisonEmail(d.getChauffeurLivraison().getEmail());
        }
        if (d.getProForma() != null && d.getProForma().getStatut() != null) {
            dto.setProFormaId(d.getProForma().getId());
            dto.setProFormaStatut(d.getProForma().getStatut().name());
        }

        // Mapping Mission Checking
        try {
            missionRepo.findByDemandeIdOrderByCreateDateDesc(d.getId()).stream()
                .filter(m -> m.getType() == Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur.TypeMission.RECUPERATION)
                .findFirst()
                .ifPresent(m -> {
                    dto.setMissionId(m.getId());
                    dto.setMissionStatut(m.getStatut().name());
                    dto.setCheckingPhotoAvant(m.getCheckingPhotoAvant());
                    dto.setCheckingPhotoArriere(m.getCheckingPhotoArriere());
                    dto.setCheckingPhotoGauche(m.getCheckingPhotoGauche());
                    dto.setCheckingPhotoDroit(m.getCheckingPhotoDroit());
                    dto.setCheckingObservations(m.getCheckingObservations());
                    
                    // Nouveaux champs de réception
                    dto.setReceptionPhotoAvant(m.getReceptionPhotoAvant());
                    dto.setReceptionPhotoArriere(m.getReceptionPhotoArriere());
                    dto.setReceptionPhotoGauche(m.getReceptionPhotoGauche());
                    dto.setReceptionPhotoDroit(m.getReceptionPhotoDroit());
                    dto.setReceptionObservations(m.getReceptionObservations());
                });
        } catch (Exception e) {
            System.err.println("Erreur mapping mission pour demande " + d.getId() + ": " + e.getMessage());
        }

        return dto;
    }
}
