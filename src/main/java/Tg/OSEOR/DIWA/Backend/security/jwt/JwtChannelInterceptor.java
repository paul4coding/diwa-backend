package Tg.OSEOR.DIWA.Backend.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;

/**
 * Intercepteur STOMP — valide le token JWT sur chaque frame CONNECT.
 *
 * Le client doit envoyer le token dans le header STOMP "Authorization"
 * au moment de la connexion (SockJS / STOMP CONNECT) :
 *
 *   stompClient.connect({ Authorization: "Bearer <token>" }, ...)
 *
 * Les autres frames (SUBSCRIBE, SEND…) passent sans re-validation
 * car la session STOMP est déjà authentifiée.
 */
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtChannelInterceptor.class);

    private final JwtTokenProvider jwtTokenProvider;

    public JwtChannelInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message; // SUBSCRIBE / SEND / DISCONNECT — pas de revalidation
        }

        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("WebSocket CONNECT rejeté — header Authorization manquant");
            throw new AccessDeniedException("Connexion WebSocket non authentifiée : token manquant.");
        }

        String token = authHeader.substring(7);

        if (!jwtTokenProvider.validateJwtToken(token)) {
            log.warn("WebSocket CONNECT rejeté — token JWT invalide ou expiré");
            throw new AccessDeniedException("Connexion WebSocket non authentifiée : token invalide.");
        }

        log.debug("WebSocket CONNECT accepté pour user={}",
                jwtTokenProvider.getUserNameFromJwtToken(token));

        return message;
    }
}
