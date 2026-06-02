package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.dto.ChatDTO.MessageChatDTO;
import Tg.OSEOR.DIWA.Backend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatRestController {

    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/demande/{demandeId}")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT','ROLE_RECEPTIONNISTE','ROLE_CHEF_TECHNICIEN','ROLE_ADMIN')")
    public ResponseEntity<List<MessageChatDTO>> getHistorique(@PathVariable Long demandeId, Authentication auth) {
        return ResponseEntity.ok(chatService.getHistorique(demandeId, auth.getName()));
    }

    @GetMapping("/demande/{demandeId}/non-lus")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT','ROLE_RECEPTIONNISTE','ROLE_CHEF_TECHNICIEN','ROLE_ADMIN')")
    public ResponseEntity<Map<String, Long>> getNonLus(@PathVariable Long demandeId, Authentication auth) {
        return ResponseEntity.ok(Map.of("count", chatService.getNombreNonLus(demandeId, auth.getName())));
    }
}
