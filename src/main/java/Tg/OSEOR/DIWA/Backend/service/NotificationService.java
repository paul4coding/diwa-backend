package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import Tg.OSEOR.DIWA.Backend.repository.AppNotificationRepository;
import Tg.OSEOR.DIWA.Backend.entity.AppNotification;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    @Autowired
    private AppNotificationRepository notificationRepo;

    public void saveAndSendPush(User user, String title, String body, String type, String targetId) {
        // 1. Sauvegarder en DB pour la cloche
        AppNotification notif = new AppNotification(user, title, body, type, targetId);
        notif.setExpiresAt(LocalDateTime.now().plusDays(7)); // Disparaît après 7 jours
        notificationRepo.save(notif);

        // 2. Envoyer le push mobile si token dispo
        sendPushToUser(user, title, body, Map.of("type", type, "targetId", targetId));
    }

    private void sendPushToUser(User user, String title, String body, Map<String, Object> data) {
        String pushToken = user.getExpoPushToken();
        if (pushToken != null && !pushToken.isEmpty()) {
            sendPush(pushToken, title, body, data);
        }
    }

    public void sendPushToChauffeur(User chauffeur, String title, String body) {
        String pushToken = chauffeur.getExpoPushToken(); 
        if (pushToken == null || pushToken.isEmpty()) return;
        sendPush(pushToken, title, body, null);
    }

    public void sendPushToChauffeur(User chauffeur, String title, String body, Map<String, Object> data) {
        String pushToken = chauffeur.getExpoPushToken();
        if (pushToken == null || pushToken.isEmpty()) return;
        sendPush(pushToken, title, body, data);
    }

    public void sendPushToClient(User client, String title, String body) {
        sendPushToClient(client, title, body, null);
    }

    public void sendPushToClient(User client, String title, String body, Map<String, Object> data) {
        String pushToken = client.getExpoPushToken();
        if (pushToken == null || pushToken.isEmpty()) return;
        sendPush(pushToken, title, body, data);
    }

    public void sendToReceptionniste(String title, String body) {
        // En V1, on peut envoyer un log ou notifier les admins
        System.out.println("NOTIF RECEPTIONNISTE: " + title + " - " + body);
    }

    private void sendPush(String token, String title, String body, Map<String, Object> data) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("to", token);
        payload.put("title", title);
        payload.put("body", body);
        payload.put("sound", "default");
        if (data != null) payload.put("data", data);

        try {
            restTemplate.postForObject(EXPO_PUSH_URL, payload, String.class);
        } catch (Exception e) {
            System.err.println("Erreur notification push : " + e.getMessage());
        }
    }
}
