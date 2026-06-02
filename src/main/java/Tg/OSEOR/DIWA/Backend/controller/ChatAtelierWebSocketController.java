package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.model.atelier.MessageChatAtelier;
import Tg.OSEOR.DIWA.Backend.service.ChatAtelierService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatAtelierWebSocketController {

    private final ChatAtelierService chatService;

    public ChatAtelierWebSocketController(ChatAtelierService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/atelier/chat/{demandeId}")
    @SendTo("/topic/atelier/chat/{demandeId}")
    public MessageChatAtelier envoyerMessage(
            @DestinationVariable Long demandeId,
            @Payload ChatMessageRequest req,
            Principal principal) {
        return chatService.envoyerMessage(demandeId, req.getContenu(), req.getPieceJointeUrl(), principal.getName());
    }

    public static class ChatMessageRequest {
        private String contenu;
        private String pieceJointeUrl;
        public String getContenu() { return contenu; }
        public void setContenu(String c) { this.contenu = c; }
        public String getPieceJointeUrl() { return pieceJointeUrl; }
        public void setPieceJointeUrl(String p) { this.pieceJointeUrl = p; }
    }
}
