package Tg.OSEOR.DIWA.Backend.mappers;

import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import Tg.OSEOR.DIWA.Backend.dto.TicketSAVDTO.TicketSAVDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.TicketSAV;

@Component
public class TicketSAVMapper {

    public TicketSAVDTOResponse toResponse(TicketSAV entity) {
        if (entity == null) return null;

        TicketSAVDTOResponse dto = new TicketSAVDTOResponse();
        dto.setId(entity.getId());
        dto.setVehiculeImmat(entity.getVehiculeImmat());
        dto.setVehiculeMarque(entity.getVehiculeMarque());
        dto.setDescription(entity.getDescription());
        dto.setStatut(entity.getStatut());
        dto.setDateCreation(entity.getCreateDate());

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserName(entity.getUser().getUsername());
        } else {
            dto.setUserName("Client Inconnu");
        }

        if (entity.getTechnicien() != null) {
            dto.setTechnicienId(entity.getTechnicien().getId());
            dto.setTechnicienNom(entity.getTechnicien().getNom());
        }

        if (entity.getCommentaires() != null) {
            dto.setCommentaires(entity.getCommentaires().stream()
                .map(c -> new TicketSAVDTOResponse.CommentaireTicketDTO(
                    c.getMessage(), c.getAuteur(), c.getDateCommentaire()))
                .collect(Collectors.toList()));
        }

        return dto;
    }
}
