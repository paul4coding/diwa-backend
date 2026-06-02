package Tg.OSEOR.DIWA.Backend.mappers;

import org.springframework.stereotype.Component;
import Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO.RendezVousDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.RendezVous;

@Component
public class RendezVousMapper {

    public RendezVousDTOResponse toResponse(RendezVous entity) {
        if (entity == null) return null;
        
        RendezVousDTOResponse dto = new RendezVousDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setDate(entity.getDate());
        if(entity.getStatut() != null) {
            dto.setStatut(entity.getStatut().name());
        }
        
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            // Log de débogage pour voir si l'utilisateur est là
            System.out.println("MAPPING RDV POUR USER: " + entity.getUser().getUsername());
            
            String prenom = entity.getUser().getPrenom();
            String nom = entity.getUser().getNom();
            String full = (prenom != null ? prenom : "") + " " + (nom != null ? nom : "");
            
            if (full.trim().isEmpty()) {
                dto.setUserName(entity.getUser().getUsername()); // Toujours avoir au moins le login
            } else {
                dto.setUserName(full.trim());
            }
        } else {
            System.err.println("ERREUR MAPPING: L'utilisateur est NULL pour le RDV #" + entity.getId());
        }
        
        if (entity.getTechnicien() != null) {
            dto.setTechnicienId(entity.getTechnicien().getId());
            dto.setTechnicienNom(entity.getTechnicien().getNom());
        }
        
        if (entity.getService() != null) {
            dto.setServiceId(entity.getService().getId());
            dto.setServiceLibelle(entity.getService().getLibelle());
        }
        
        if (entity.getVehicule() != null) {
            dto.setVehiculeId(entity.getVehicule().getId());
            if (entity.getVehicule().getMarque() != null) {
                dto.setVehiculeMarque(entity.getVehicule().getMarque().name());
            }
            dto.setVehiculeModele(entity.getVehicule().getModele());
        } else {
            // Utiliser les champs saisis manuellement si pas de véhicule lié
            dto.setVehiculeModele(entity.getModeleVehicule());
        }
        
        if (entity.getCreneau() != null && entity.getCreneau().getHeureDebut() != null) {
            dto.setCreneauId(entity.getCreneau().getId());
            dto.setCreneauHeures(entity.getCreneau().getHeureDebut().toString());
        } else if (entity.getHeureDebut() != null) {
            dto.setCreneauHeures(entity.getHeureDebut());
        }
        
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        
        dto.setImmatriculation(entity.getImmatriculation());
        dto.setDescription(entity.getDescription());
        dto.setVin(entity.getVin());
        dto.setKilometrage(entity.getKilometrage());
        dto.setDetailsSpecifiques(entity.getDetailsSpecifiques());
        if (entity.getUrgence() != null) {
            dto.setUrgence(entity.getUrgence().name());
        }
        
        return dto;
    }
}
