package Tg.OSEOR.DIWA.Backend.security.service;

import Tg.OSEOR.DIWA.Backend.service.EmailService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import Tg.OSEOR.DIWA.Backend.dto.UserDTO.UserDTOResponse;
import Tg.OSEOR.DIWA.Backend.mappers.UserMapper;
import Tg.OSEOR.DIWA.Backend.security.UserDetailsImpl;
import Tg.OSEOR.DIWA.Backend.security.dto.JwtAuthenticationResponse;
import Tg.OSEOR.DIWA.Backend.security.dto.LoginRequest;
import Tg.OSEOR.DIWA.Backend.security.dto.SignUpRequest;
import Tg.OSEOR.DIWA.Backend.security.dto.LoginStep1Response;
import Tg.OSEOR.DIWA.Backend.security.dto.LoginStep2Request;
import Tg.OSEOR.DIWA.Backend.security.dto.PasswordResetConfirmRequest;
import Tg.OSEOR.DIWA.Backend.security.enums.ERole;
import Tg.OSEOR.DIWA.Backend.security.jwt.JwtTokenProvider;
import Tg.OSEOR.DIWA.Backend.security.model.Role;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.model.auth.AccountConfirmationToken;
import Tg.OSEOR.DIWA.Backend.security.model.auth.OtpCode.OtpType;
import Tg.OSEOR.DIWA.Backend.security.repository.RoleRepository;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import Tg.OSEOR.DIWA.Backend.security.repository.auth.AccountConfirmationTokenRepository;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtUtils;
    private final AccountConfirmationTokenRepository confirmationTokenRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, 
                           UserRepository userRepository, 
                           RoleRepository roleRepository, 
                           PasswordEncoder encoder, 
                           JwtTokenProvider jwtUtils,
                           AccountConfirmationTokenRepository confirmationTokenRepository,
                           OtpService otpService,
                           EmailService emailService,
                           UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.otpService = otpService;
        this.emailService = emailService;
        this.userMapper = userMapper;
    }

    @Override
    public JwtAuthenticationResponse login(LoginRequest loginRequest) {
        logger.info("Début du processus de login pour: {}", loginRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            logger.info("Authentification Spring Security réussie pour: {}", loginRequest.getUsername());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            logger.info("Vérification du statut du compte (emailVerified) pour l'ID: {}", userDetails.getId());
            User user = userRepository.findById(userDetails.getId()).orElse(null);
            if (user != null && !user.getEmailVerified()) {
                logger.warn("Échec du login: Email non vérifié pour {}", loginRequest.getUsername());
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, 
                    "Veuillez confirmer votre email avant de vous connecter"
                );
            }

            logger.info("Génération du token JWT pour {}", loginRequest.getUsername());
            String jwt = jwtUtils.generateJwtToken(authentication);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            logger.info("Login réussi, retour de la réponse pour {}", loginRequest.getUsername());
            return new JwtAuthenticationResponse(jwt, 
                                                 userDetails.getId(), 
                                                 userDetails.getUsername(), 
                                                 userDetails.getEmail(), 
                                                 userDetails.getPrenom(),
                                                 userDetails.getNom(),
                                                 roles,
                                                 user != null && Boolean.TRUE.equals(user.getMustResetPassword()));
        } catch (org.springframework.security.core.AuthenticationException e) {
            logger.warn("Échec de connexion (Bad Credentials) pour {}", loginRequest.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        } catch (Exception e) {
            logger.error("ERREUR LOGIN CRITIQUE pour {}: {}", loginRequest.getUsername(), e.getMessage());
            if (e instanceof ResponseStatusException) throw (ResponseStatusException) e;
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne lors de la connexion: " + e.getMessage());
        }
    }

    @Override
    public UserDTOResponse register(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erreur: Le nom d'utilisateur est déjà pris!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erreur: L'email est déjà utilisé!");
        }

        // Créer le compte utilisateur
        User user = new User(signUpRequest.getUsername(), 
                             signUpRequest.getEmail(),
                             encoder.encode(signUpRequest.getPassword()),
                             signUpRequest.getPrenom(),
                             signUpRequest.getNom());

        // Sécurité : l'inscription publique assigne toujours ROLE_CLIENT.
        // Les rôles staff (ADMIN, RECEPTIONNISTE, etc.) sont attribués
        // uniquement par un administrateur via AdminController.
        Set<Role> roles = new HashSet<>();
        Role clientRole = roleRepository.findByName(ERole.ROLE_CLIENT)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur: Le rôle par défaut n'est pas trouvé."));
        roles.add(clientRole);

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        // Envoyer email de confirmation
        String token = UUID.randomUUID().toString();
        AccountConfirmationToken confirmationToken = new AccountConfirmationToken();
        confirmationToken.setUser(savedUser);
        confirmationToken.setToken(token);
        confirmationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        confirmationTokenRepository.save(confirmationToken);

        emailService.sendAccountConfirmationEmail(savedUser.getEmail(), savedUser.getUsername(), token);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public LoginStep1Response loginStep1(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getUsername())
                .or(() -> userRepository.findByUsername(loginRequest.getUsername()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect"));

        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");
        }

        if (!user.getEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Veuillez confirmer votre email avant de vous connecter");
        }

        String otp = otpService.generateAndSaveOtp(user, OtpType.LOGIN_2FA);
        logger.info("OTP généré pour {}: {}", user.getEmail(), otp);
        emailService.sendOtpEmail(user.getEmail(), user.getUsername(), otp);

        return new LoginStep1Response(user.getEmail(), "Code de vérification envoyé à " + maskEmail(user.getEmail()), user.getMustResetPassword(), user.getTwoFactorEnabled());
    }

    @Override
    public JwtAuthenticationResponse loginStep2(LoginStep2Request request) {
        String email = request.getEmail().trim();
        String otpCode = request.getOtpCode().trim();
        
        logger.info("Validation OTP (Step 2) pour l'email: {}, Code reçu: '{}'", email, otpCode);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expirée"));

        if (!otpService.verifyOtp(user, otpCode, OtpType.LOGIN_2FA)) {
            logger.warn("Code OTP invalide pour: {}. Code essayé: '{}'", email, otpCode);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Code incorrect ou expiré");
        }

        // Création manuelle de l'authentification (on ne repasse pas par le manager car on n'a pas le mot de passe en clair ici)
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        logger.info("Authentification Step 2 réussie pour: {}", request.getEmail());
        return new JwtAuthenticationResponse(jwt, 
                                             userDetails.getId(), 
                                             userDetails.getUsername(), 
                                             userDetails.getEmail(), 
                                             userDetails.getPrenom(),
                                             userDetails.getNom(),
                                             roles, 
                                             user.getMustResetPassword());
    }

    @Override
    public void confirmAccount(String token) {
        AccountConfirmationToken confirmationToken = confirmationTokenRepository
                .findValidToken(token, LocalDateTime.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lien invalide ou expiré"));

        User user = confirmationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        confirmationToken.setConfirmed(true);
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public void resetPassword(String email, String newPassword, String confirmPassword) {
        // Cette méthode semble être utilisée par une ancienne version ou un flux différent.
        // On devrait utiliser une version qui vérifie l'OTP.
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utilisez la méthode avec OTP pour réinitialiser le mot de passe");
    }

    @Override
    public void changePasswordFirstLogin(String email, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les mots de passe ne correspondent pas");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        if (!Boolean.TRUE.equals(user.getMustResetPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce compte n'est pas configuré pour un changement forcé de mot de passe");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setMustResetPassword(false);
        userRepository.save(user);
    }

    public void resetPasswordWithOtp(String email, String otpCode, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les mots de passe ne correspondent pas");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        if (!otpService.verifyOtp(user, otpCode, OtpType.PASSWORD_RESET)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Code OTP invalide ou expiré");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setMustResetPassword(false);
        userRepository.save(user);
    }

    @Override
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String otp = otpService.generateAndSaveOtp(user, OtpType.PASSWORD_RESET);
            emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), otp);
        });
    }

    private String maskEmail(String email) {
        String[] parts = email.split("@");
        String name = parts[0];
        String masked = name.charAt(0) + "***";
        return masked + "@" + parts[1];
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId()).orElse(null);
    }
}
