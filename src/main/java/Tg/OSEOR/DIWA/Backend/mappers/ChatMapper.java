package Tg.OSEOR.DIWA.Backend.mappers;

import Tg.OSEOR.DIWA.Backend.dto.ChatDTO.MessageChatDTO;
import Tg.OSEOR.DIWA.Backend.entity.MessageChat;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    public MessageChatDTO toDTO(MessageChat m) {
        MessageChatDTO dto = new MessageChatDTO();
        dto.setId(m.getId());
        dto.setContenu(m.getContenu());
        dto.setRoleAuteur(m.getRoleAuteur() != null ? m.getRoleAuteur().name() : null);
        dto.setLu(m.getLu());
        dto.setPieceJointeUrl(m.getPieceJointeUrl());
        dto.setCreatedAt(m.getCreateDate());

        if (m.getAuteur() != null) {
            String nom = (m.getAuteur().getPrenom() != null ? m.getAuteur().getPrenom() : "") + " " +
                         (m.getAuteur().getNom() != null ? m.getAuteur().getNom() : "");
            dto.setAuteurNom(nom.trim().isEmpty() ? m.getAuteur().getUsername() : nom.trim());
        }
        return dto;
    }
}
