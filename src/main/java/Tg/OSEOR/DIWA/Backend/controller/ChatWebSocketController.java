package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.dto.ChatDTO.MessageChatDTO;
import Tg.OSEOR.DIWA.Backend.service.ChatService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{demandeId}")
    @SendTo("/topic/chat/{demandeId}")
    public MessageChatDTO envoyerMessage(
            @DestinationVariable Long demandeId,
            @Payload ChatMessageRequest request,
            Principal principal) {

        MessageChatDTO msg = chatService.envoyerMessage(
                demandeId, request.getContenu(), request.getPieceJointeUrl(), principal.getName()
        );

        messagingTemplate.convertAndSend("/topic/notifications/" + demandeId, msg);
        return msg;
    }

    // ── Request DTO interne ──────────
    public static class ChatMessageRequest {
        @NotBlank
        private String contenu;
        private String pieceJointeUrl;

        public String getContenu() { return contenu; }
        public void setContenu(String c) { this.contenu = c; }
        public String getPieceJointeUrl() { return pieceJointeUrl; }
        public void setPieceJointeUrl(String p) { this.pieceJointeUrl = p; }
    }
}
