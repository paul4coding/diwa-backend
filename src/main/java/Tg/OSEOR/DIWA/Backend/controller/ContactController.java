package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.dto.ContactRequest;
import Tg.OSEOR.DIWA.Backend.entity.ContactMessage;
import Tg.OSEOR.DIWA.Backend.repository.ContactMessageRepository;
import Tg.OSEOR.DIWA.Backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @PostMapping("/send")
    public ResponseEntity<?> sendContactMessage(@RequestBody ContactRequest request) {
        try {
            if (request == null || request.getNom() == null || request.getEmail() == null || request.getMessage() == null) {
                return ResponseEntity.badRequest().body("Données incomplètes.");
            }

            // 1. Sauvegarder en base de données
            ContactMessage message = new ContactMessage();
            message.setNom(request.getNom());
            message.setEmail(request.getEmail());
            message.setTelephone(request.getTelephone());
            message.setMessage(request.getMessage());
            contactMessageRepository.save(message);

            // 2. Envoyer notification à l'admin
            emailService.sendContactEmail(request);

            return ResponseEntity.ok().body(Map.of("message", "Votre message a été envoyé avec succès."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur: " + e.getMessage());
        }
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        return ResponseEntity.ok(contactMessageRepository.findAllByOrderByCreateDateDesc());
    }

    @PostMapping("/admin/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> replyToMessage(@RequestBody Map<String, String> payload) {
        try {
            Long id = Long.parseLong(payload.get("id"));
            String replyContent = payload.get("reponse");

            ContactMessage message = contactMessageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Message non trouvé"));

            // 1. Envoyer le mail de réponse
            emailService.sendContactReply(message.getEmail(), message.getNom(), message.getMessage(), replyContent);

            // 2. Mettre à jour en base de données
            message.setReponse(replyContent);
            message.setDateReponse(LocalDateTime.now());
            message.setRepondu(true);
            message.setLu(true);
            contactMessageRepository.save(message);

            return ResponseEntity.ok().body(Map.of("message", "Réponse envoyée avec succès au client."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur: " + e.getMessage());
        }
    }

    @PutMapping("/admin/mark-read/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        ContactMessage message = contactMessageRepository.findById(id).orElse(null);
        if (message != null) {
            message.setLu(true);
            contactMessageRepository.save(message);
        }
        return ResponseEntity.ok().build();
    }
}
