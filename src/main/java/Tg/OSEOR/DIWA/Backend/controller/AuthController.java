package Tg.OSEOR.DIWA.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Tg.OSEOR.DIWA.Backend.dto.UserDTO.UserDTOResponse;
import Tg.OSEOR.DIWA.Backend.security.dto.JwtAuthenticationResponse;
import Tg.OSEOR.DIWA.Backend.security.dto.LoginRequest;
import Tg.OSEOR.DIWA.Backend.security.dto.LoginStep1Response;
import Tg.OSEOR.DIWA.Backend.security.dto.LoginStep2Request;
import Tg.OSEOR.DIWA.Backend.security.dto.PasswordResetConfirmRequest;
import Tg.OSEOR.DIWA.Backend.security.dto.SignUpRequest;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Tentative de login reçue pour l'utilisateur : [{}]", loginRequest.getUsername());
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/login/step1")
    public ResponseEntity<LoginStep1Response> loginStep1(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.loginStep1(loginRequest));
    }

    @PostMapping("/login/step2")
    public ResponseEntity<JwtAuthenticationResponse> loginStep2(@Valid @RequestBody LoginStep2Request request) {
        return ResponseEntity.ok(authService.loginStep2(request));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTOResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authService.register(signUpRequest));
    }

    @GetMapping("/confirm")
    public ResponseEntity<Void> confirmAccount(@RequestParam String token) {
        authService.confirmAccount(token);
        return ResponseEntity.status(org.springframework.http.HttpStatus.FOUND)
                .location(java.net.URI.create(frontendUrl + "/login?confirmed=true"))
                .build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok("Si l'email existe, un code de réinitialisation a été envoyé");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetConfirmRequest request) {
        authService.resetPasswordWithOtp(request.getEmail(), request.getOtpCode(), request.getNewPassword(),
                request.getConfirmPassword());
        return ResponseEntity.ok("Mot de passe réinitialisé avec succès");
    }

    @PostMapping("/change-password-first-login")
    public ResponseEntity<?> changePasswordFirstLogin(@RequestBody java.util.Map<String, String> request) {
        authService.changePasswordFirstLogin(request.get("email"), request.get("newPassword"),
                request.get("confirmPassword"));
        return ResponseEntity.ok("Mot de passe mis à jour avec succès. Vous pouvez maintenant vous connecter.");
    }
}
