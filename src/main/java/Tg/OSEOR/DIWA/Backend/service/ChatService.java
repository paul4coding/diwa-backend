package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.ChatDTO.MessageChatDTO;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.entity.MessageChat;
import Tg.OSEOR.DIWA.Backend.mappers.ChatMapper;
import Tg.OSEOR.DIWA.Backend.repository.DemandeInterventionRepository;
import Tg.OSEOR.DIWA.Backend.repository.MessageChatRepository;
import Tg.OSEOR.DIWA.Backend.security.enums.ERole;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {

    private final MessageChatRepository messageChatRepository;
    private final DemandeInterventionRepository demandeRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    public ChatService(MessageChatRepository messageChatRepository,
                       DemandeInterventionRepository demandeRepository,
                       UserRepository userRepository,
                       ChatMapper chatMapper) {
        this.messageChatRepository = messageChatRepository;
        this.demandeRepository = demandeRepository;
        this.userRepository = userRepository;
        this.chatMapper = chatMapper;
    }

    public MessageChatDTO envoyerMessage(Long demandeId, String contenu, String pieceJointeUrl, String userEmail) {
        DemandeIntervention demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable"));
        User auteur = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        MessageChat msg = new MessageChat();
        msg.setDemande(demande);
        msg.setAuteur(auteur);
        msg.setContenu(contenu);
        msg.setPieceJointeUrl(pieceJointeUrl);
        msg.setRoleAuteur(getRoleAuteurChat(auteur));
        msg.setLu(false);

        MessageChat saved = messageChatRepository.save(msg);
        return chatMapper.toDTO(saved);
    }

    @Transactional
    public List<MessageChatDTO> getHistorique(Long demandeId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        messageChatRepository.marquerLusParUtilisateur(demandeId, user.getId());
        return messageChatRepository.findByDemandeIdOrderByCreateDateAsc(demandeId)
                .stream().map(chatMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getNombreNonLus(Long demandeId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        return messageChatRepository.countByDemandeIdAndLuFalseAndAuteurIdNot(demandeId, user.getId());
    }

    private MessageChat.RoleAuteurChat getRoleAuteurChat(User user) {
        if (user.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_CHEF_TECHNICIEN))
            return MessageChat.RoleAuteurChat.CHEF_TECHNICIEN;
        if (user.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_RECEPTIONNISTE))
            return MessageChat.RoleAuteurChat.RECEPTIONNISTE;
        if (user.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN))
            return MessageChat.RoleAuteurChat.ADMIN;
        return MessageChat.RoleAuteurChat.CLIENT;
    }
}
