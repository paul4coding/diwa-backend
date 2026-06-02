package Tg.OSEOR.DIWA.Backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import Tg.OSEOR.DIWA.Backend.security.enums.ERole;
import Tg.OSEOR.DIWA.Backend.security.model.Role;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.RoleRepository;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import Tg.OSEOR.DIWA.Backend.entity.ParametreAtelier;
import Tg.OSEOR.DIWA.Backend.repository.ParametreAtelierRepository;
import Tg.OSEOR.DIWA.Backend.entity.ServiceSAV;
import Tg.OSEOR.DIWA.Backend.repository.ServiceSAVRepository;
import Tg.OSEOR.DIWA.Backend.entity.CategoriePiece;
import Tg.OSEOR.DIWA.Backend.repository.CategoriePieceRepository;
import Tg.OSEOR.DIWA.Backend.repository.atelier.GammePrixRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    @org.springframework.transaction.annotation.Transactional
    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository,
            CategoriePieceRepository categoriePieceRepository, ServiceSAVRepository serviceSAVRepository,
            GammePrixRepository gammePrixRepository, ParametreAtelierRepository parametreAtelierRepository,
            PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        return args -> {
            // 0. Nettoyage robuste des contraintes de vérification
            try {
                // Nettoyage des rôles
                jdbcTemplate.execute("DO $$ DECLARE r RECORD; BEGIN FOR r IN (SELECT conname FROM pg_constraint WHERE conrelid = 'roles'::regclass AND contype = 'c') LOOP EXECUTE 'ALTER TABLE roles DROP CONSTRAINT ' || r.conname; END LOOP; END $$;");
                
                // Nettoyage de l'historique (le fautif du 500)
                jdbcTemplate.execute("DO $$ DECLARE r RECORD; BEGIN FOR r IN (SELECT conname FROM pg_constraint WHERE conrelid = 'historique_statuts_demande'::regclass AND contype = 'c') LOOP EXECUTE 'ALTER TABLE historique_statuts_demande DROP CONSTRAINT ' || r.conname; END LOOP; END $$;");
                
                // Nettoyage des demandes
                jdbcTemplate.execute("DO $$ DECLARE r RECORD; BEGIN FOR r IN (SELECT conname FROM pg_constraint WHERE conrelid = 'demandes_intervention'::regclass AND contype = 'c') LOOP EXECUTE 'ALTER TABLE demandes_intervention DROP CONSTRAINT ' || r.conname; END LOOP; END $$;");
                
                System.out.println("Nettoyage des contraintes CHECK effectué avec succès.");
            } catch (Exception e) {
                System.out.println("Info: Nettoyage des contraintes partiel ou déjà fait : " + e.getMessage());
            }

            // 1. Initialisation des rôles
            for (ERole eRole : ERole.values()) {
                if (!roleRepository.existsByName(eRole)) {
                    Role role = new Role();
                    role.setName(eRole);
                    roleRepository.save(role);
                    System.out.println("Rôle créé : " + eRole);
                }
            }

            // 2. Création de l'administrateur par défaut
            String adminEmail = "admin@diwa.tg";
            String adminUsername = "admin";
            if (userRepository.findByEmail(adminEmail).isEmpty() && userRepository.findByUsername(adminUsername).isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmailVerified(true);
                admin.setTwoFactorEnabled(false); // Désactivé par défaut pour faciliter le dev

                Set<Role> roles = new HashSet<>();
                // Sécurisation : on prend le premier rôle trouvé si doublon présent
                Role adminRole = roleRepository.findAll().stream()
                        .filter(r -> r.getName() == ERole.ROLE_ADMIN)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Rôle ADMIN introuvable"));
                roles.add(adminRole);
                admin.setRoles(roles);

                userRepository.save(admin);
                System.out.println("Administrateur par défaut créé : " + adminEmail);
            }

            // 2.1 Création des utilisateurs de test pour les nouveaux rôles
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_RECEPTIONNISTE, "reception@diwa.tg", "reception");
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_CHEF_TECHNICIEN, "cheftech@diwa.tg", "cheftech");
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_TECHNICIEN, "tech@diwa.tg", "tech");
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_CHAUFFEUR, "chauffeur@diwa.tg", "chauffeur");
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_CLIENT, "client@diwa.tg", "client");
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_STOCK, "stock@diwa.tg", "stock");
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_DG, "dg@diwa.tg", "dg");

            // 2.2 Initialisation des paramètres de l'atelier
            if (parametreAtelierRepository.count() == 0) {
                ParametreAtelier params = new ParametreAtelier();
                parametreAtelierRepository.save(params);
                System.out.println("Paramètres de l'atelier initialisés par défaut.");
            }

            // 3. Initialisation des Gammes de Prix
            if (gammePrixRepository.count() == 0) {
                String[][] gammeData = {
                    {"Plaquettes de frein", "Freinage", "15000", "45000", "Remplacement plaquettes avant/arrière"},
                    {"Disques de frein", "Freinage", "40000", "120000", "Paire de disques de frein"},
                    {"Amortisseurs", "Suspension", "60000", "250000", "Paire d'amortisseurs"},
                    {"Batterie 12V", "Electricité", "45000", "95000", "Batterie sans entretien"},
                    {"Kit Distribution", "Moteur", "80000", "350000", "Courroie + galets"},
                    {"Vidange Complète", "Entretien", "25000", "75000", "Huile + Filtres"}
                };
                for (String[] g : gammeData) {
                    Tg.OSEOR.DIWA.Backend.entity.GammePrix gp = new Tg.OSEOR.DIWA.Backend.entity.GammePrix();
                    gp.setDesignation(g[0]);
                    gp.setCategorie(g[1]);
                    gp.setPrixMin(new java.math.BigDecimal(g[2]));
                    gp.setPrixMax(new java.math.BigDecimal(g[3]));
                    gp.setDescription(g[4]);
                    gammePrixRepository.save(gp);
                }
                System.out.println("Gammes de prix initialisées.");
            }

            // 4. Initialisation des catégories de pièces
            String[] commonCategories = { "Lubrifiants", "Moteurs", "Accessoires", "Électricité" };
            for (String catName : commonCategories) {
                if (categoriePieceRepository.findAll().stream()
                        .noneMatch(c -> c.getLibelle().equalsIgnoreCase(catName))) {
                    CategoriePiece cat = new CategoriePiece();
                    cat.setLibelle(catName);
                    categoriePieceRepository.save(cat);
                }
            }

            // 4. Initialisation des services SAV
            String[][] servicesInit = {
                {"mecanique", "Mécanique & Moteur", "Diagnostic et réparation mécanique lourde"},
                {"electronique", "Électronique & Bord", "Réparation systèmes électriques et informatiques"},
                {"entretien", "Entretien & Révision", "Maintenance périodique certifiée"},
                {"carrosserie", "Carrosserie & Esthétique", "Remise à neuf extérieure et peinture"},
                {"pieces", "Pièces Détachées", "Vente et installation de pièces d'origine"}
            };
            
            for (String[] s : servicesInit) {
                boolean exists = serviceSAVRepository.findAll().stream()
                        .anyMatch(serv -> serv.getLibelle().equalsIgnoreCase(s[1]));
                if (!exists) {
                    ServiceSAV service = new ServiceSAV();
                    service.setLibelle(s[1]);
                    service.setDescription(s[2]);
                    service.setPrixBase(0.0);
                    serviceSAVRepository.save(service);
                    System.out.println("SERVICE SAV CRÉÉ : " + s[1]);
                }
            }

            // 5. Initialisation des plages horaires DIWA (Seulement si elles n'existent pas encore)
            String[][] plages = {
                {"06:00", "09:00", "1"},
                {"09:00", "12:00", "2"},
                {"14:00", "16:00", "3"},
                {"16:00", "18:00", "4"}
            };
            for (String[] p : plages) {
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM plage_horaire_diwa WHERE heure_debut = ?::time AND heure_fin = ?::time",
                    Integer.class, p[0], p[1]
                );
                
                if (count != null && count == 0) {
                    jdbcTemplate.execute(String.format(
                        "INSERT INTO plage_horaire_diwa (heure_debut, heure_fin, active, ordre, create_date, uuid) VALUES ('%s', '%s', true, %s, now(), gen_random_uuid())",
                        p[0], p[1], p[2]
                    ));
                    System.out.println("Plage horaire DIWA créée : " + p[0] + " - " + p[1]);
                }
            }
        };
    }

    private void createTestUser(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, 
                                ERole eRole, String email, String username) {
        if (userRepository.findByEmail(email).isEmpty() && userRepository.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("password123"));
            user.setEmailVerified(true);
            user.setTwoFactorEnabled(false);

            Role role = roleRepository.findByName(eRole)
                    .orElseThrow(() -> new RuntimeException("Rôle " + eRole + " introuvable"));
            
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);

            userRepository.save(user);
            System.out.println("Utilisateur de test créé : " + email + " (Rôle: " + eRole + ")");
        }
    }
}
