package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Tg.OSEOR.DIWA.Backend.security.service.UserService userService;

    @Autowired
    private Tg.OSEOR.DIWA.Backend.security.repository.RoleRepository roleRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserAdminDTO> users = userRepository.findAll().stream()
                    .map(user -> new UserAdminDTO(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getRoles().stream()
                                .filter(r -> r != null && r.getName() != null)
                                .map(r -> new RoleDTO(r.getId() != null ? r.getId().longValue() : 0L, r.getName().name()))
                                .collect(Collectors.toList()),
                            Boolean.TRUE.equals(user.getEmailVerified()),
                            user.getTelephone(),
                            user.getPrenom(),
                            user.getNom()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponse.error(500, "Erreur Admin Users: " + e.getMessage()));
        }
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody java.util.Map<String, Object> request) {
        try {
            User user = new User();
            user.setUsername((String) request.get("username"));
            user.setEmail((String) request.get("email"));
            user.setPassword(passwordEncoder.encode((String) request.get("password")));
            user.setTelephone((String) request.get("telephone"));
            user.setPrenom((String) request.get("prenom"));
            user.setNom((String) request.get("nom"));
            user.setEmailVerified(true);

            java.util.List<String> roleNames = (java.util.List<String>) request.get("roles");
            java.util.Set<Tg.OSEOR.DIWA.Backend.security.model.Role> roles = roleNames.stream()
                .map(name -> roleRepository.findByName(Tg.OSEOR.DIWA.Backend.security.enums.ERole.valueOf(name))
                    .orElseThrow(() -> new RuntimeException("Role non trouvé: " + name)))
                .collect(java.util.stream.Collectors.toSet());
            
            user.setRoles(roles);
            userRepository.save(user);
            return ResponseEntity.ok(new BaseResponse<>(200, "Utilisateur créé", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponse.error(500, "Erreur création: " + e.getMessage()));
        }
    }

    @PostMapping("/users/create-chauffeur")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONNISTE')")
    public ResponseEntity<?> createChauffeur(@RequestBody java.util.Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty() || !email.contains("@")) {
                return ResponseEntity.badRequest().body(BaseResponse.error(400, "Email invalide ou manquant"));
            }

            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body(BaseResponse.error(400, "Cet email est déjà utilisé"));
            }

            String username = email.split("@")[0];
            // Si le username existe déjà, on ajoute un suffixe aléatoire ou on utilise l'email complet
            if (userRepository.existsByUsername(username)) {
                username = email.replace("@", "_").replace(".", "_");
            }
            
            if (userRepository.existsByUsername(username)) {
                 username = username + "_" + System.currentTimeMillis() % 1000;
            }

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(request.getOrDefault("password", "password123")));
            user.setTelephone(request.get("telephone"));
            user.setPrenom(request.get("prenom"));
            user.setNom(request.get("nom"));
            user.setEmailVerified(true);
            user.setMustResetPassword(true); // Forcer le changement au premier login

            Tg.OSEOR.DIWA.Backend.security.model.Role role = roleRepository.findByName(Tg.OSEOR.DIWA.Backend.security.enums.ERole.ROLE_CHAUFFEUR)
                .orElseThrow(() -> new RuntimeException("Role CHAUFFEUR non trouvé"));
            
            java.util.Set<Tg.OSEOR.DIWA.Backend.security.model.Role> roles = new java.util.HashSet<>();
            roles.add(role);
            user.setRoles(roles);
            
            userRepository.save(user);
            return ResponseEntity.ok(new BaseResponse<>(200, "Chauffeur créé avec succès", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponse.error(500, "Erreur serveur lors de la création du chauffeur: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody java.util.Map<String, Object> request) {
        try {
            User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            user.setUsername((String) request.get("username"));
            user.setEmail((String) request.get("email"));
            user.setTelephone((String) request.get("telephone"));
            user.setPrenom((String) request.get("prenom"));
            user.setNom((String) request.get("nom"));
            
            if (request.containsKey("password") && request.get("password") != null && !((String)request.get("password")).isEmpty()) {
                user.setPassword(passwordEncoder.encode((String) request.get("password")));
            }

            java.util.List<String> roleNames = (java.util.List<String>) request.get("roles");
            if (roleNames != null) {
                java.util.Set<Tg.OSEOR.DIWA.Backend.security.model.Role> roles = roleNames.stream()
                    .map(name -> roleRepository.findByName(Tg.OSEOR.DIWA.Backend.security.enums.ERole.valueOf(name))
                        .orElseThrow(() -> new RuntimeException("Role non trouvé: " + name)))
                    .collect(java.util.stream.Collectors.toSet());
                user.setRoles(roles);
            }
            
            userRepository.save(user);
            return ResponseEntity.ok(new BaseResponse<>(200, "Utilisateur mis à jour", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponse.error(500, "Erreur maj: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok(new BaseResponse<>(200, "Utilisateur supprimé", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponse.error(500, "Erreur suppression: " + e.getMessage()));
        }
    }

    // DTOs internes pour simplifier l'admin
    public static class UserAdminDTO {
        private Long id;
        private String username;
        private String email;
        private List<RoleDTO> roles;
        private boolean emailVerified;
        private String telephone;
        private String prenom;
        private String nom;

        public UserAdminDTO(Long id, String username, String email, List<RoleDTO> roles, boolean emailVerified, String telephone, String prenom, String nom) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.roles = roles;
            this.emailVerified = emailVerified;
            this.telephone = telephone;
            this.prenom = prenom;
            this.nom = nom;
        }

        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public List<RoleDTO> getRoles() { return roles; }
        public boolean isEmailVerified() { return emailVerified; }
        public String getTelephone() { return telephone; }
        public String getPrenom() { return prenom; }
        public String getNom() { return nom; }
    }

    public static class RoleDTO {
        private Long id;
        private String name;

        public RoleDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
    }
}
