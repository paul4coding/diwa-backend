package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.model.atelier.MessageChatAtelier;
import Tg.OSEOR.DIWA.Backend.service.ChatAtelierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/atelier/chat")
public class ChatAtelierController {

    private final ChatAtelierService chatService;

    public ChatAtelierController(ChatAtelierService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/demande/{demandeId}")
    public ResponseEntity<List<MessageChatAtelier>> getHistorique(@PathVariable Long demandeId) {
        return ResponseEntity.ok(chatService.getHistorique(demandeId));
    }
}
