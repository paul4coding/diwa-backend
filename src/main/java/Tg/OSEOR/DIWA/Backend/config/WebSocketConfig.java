package Tg.OSEOR.DIWA.Backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import Tg.OSEOR.DIWA.Backend.security.jwt.JwtChannelInterceptor;
import Tg.OSEOR.DIWA.Backend.security.jwt.JwtTokenProvider;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    /** Origines autorisées — injectée depuis app.frontend.url (ex. https://diwa.tg) */
    @Value("${app.frontend.url}")
    private String frontendUrl;

    public WebSocketConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-atelier")
                .setAllowedOriginPatterns(frontendUrl)
                .withSockJS();

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(frontendUrl)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic", "/queue");
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Enregistre l'intercepteur JWT sur le canal entrant.
     * Chaque frame STOMP CONNECT doit porter un header "Authorization: Bearer <token>".
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new JwtChannelInterceptor(jwtTokenProvider));
    }
}
