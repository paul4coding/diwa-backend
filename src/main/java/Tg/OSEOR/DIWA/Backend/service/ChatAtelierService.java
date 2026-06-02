package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.model.atelier.MessageChatAtelier;
import Tg.OSEOR.DIWA.Backend.repository.DemandeInterventionRepository;
import Tg.OSEOR.DIWA.Backend.repository.atelier.MessageChatAtelierRepository;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatAtelierService {

    private final MessageChatAtelierRepository chatRepository;
    private final DemandeInterventionRepository demandeRepository;
    private final UserRepository userRepository;

    public ChatAtelierService(MessageChatAtelierRepository chatRepository, 
                              DemandeInterventionRepository demandeRepository, 
                              UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.demandeRepository = demandeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public MessageChatAtelier envoyerMessage(Long demandeId, String contenu, String pieceJointeUrl, String email) {
        DemandeIntervention demande = demandeRepository.findById(demandeId).orElseThrow();
        User auteur = userRepository.findByEmail(email).orElseThrow();

        MessageChatAtelier message = new MessageChatAtelier();
        message.setDemande(demande);
        message.setAuteur(auteur);
        message.setContenu(contenu);
        message.setPieceJointeUrl(pieceJointeUrl);
        
        // Déterminer le rôle auteur pour le chat
        if (auteur.getRoles().stream().anyMatch(r -> r.getName().name().equals("ROLE_CLIENT"))) {
            message.setRoleAuteur(MessageChatAtelier.RoleAuteurChat.CLIENT);
        } else if (auteur.getRoles().stream().anyMatch(r -> r.getName().name().equals("ROLE_RECEPTIONNISTE"))) {
            message.setRoleAuteur(MessageChatAtelier.RoleAuteurChat.RECEPTIONNISTE);
        } else if (auteur.getRoles().stream().anyMatch(r -> r.getName().name().equals("ROLE_CHEF_TECHNICIEN"))) {
            message.setRoleAuteur(MessageChatAtelier.RoleAuteurChat.CHEF_TECHNICIEN);
        } else {
            message.setRoleAuteur(MessageChatAtelier.RoleAuteurChat.ADMIN);
        }

        return chatRepository.save(message);
    }

    public List<MessageChatAtelier> getHistorique(Long demandeId) {
        return chatRepository.findByDemandeIdOrderByCreateDateAsc(demandeId);
    }
}
