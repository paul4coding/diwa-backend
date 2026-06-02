package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.entity.AppNotification;
import Tg.OSEOR.DIWA.Backend.repository.AppNotificationRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private AppNotificationRepository notificationRepo;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/my")
    public ResponseEntity<List<AppNotification>> getMyNotifications(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(notificationRepo.findByUserIdOrderByCreateDateDesc(user.getId()));
    }

    @PutMapping("/mark-read/{id}")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        notificationRepo.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepo.save(n);
        });
        return ResponseEntity.ok().build();
    }

    /**
     * Marque TOUTES les notifications non-lues de l'utilisateur connecté comme lues.
     * Appelé quand le panel de notifications est ouvert côté client.
     */
    @PutMapping("/mark-all-read")
    @Transactional
    public ResponseEntity<Void> markAllRead(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        notificationRepo.markAllReadByUserId(user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear-expired")
    public ResponseEntity<Void> clearExpired() {
        notificationRepo.deleteByExpiresAtBefore(java.time.LocalDateTime.now());
        return ResponseEntity.ok().build();
    }
}
