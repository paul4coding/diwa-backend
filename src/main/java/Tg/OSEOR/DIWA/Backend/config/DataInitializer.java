package Tg.OSEOR.DIWA.Backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    /**
     * Mot de passe initial de l'administrateur.
     * Injecté depuis la variable d'environnement DIWA_ADMIN_PASSWORD.
     * mustResetPassword est toujours forcé à true → changement obligatoire au premier login.
     */
    @Value("${app.admin.initial-password}")
    private String adminInitialPassword;

    @Bean
    @org.springframework.transaction.annotation.Transactional
    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository,
            CategoriePieceRepository categoriePieceRepository, ServiceSAVRepository serviceSAVRepository,
            GammePrixRepository gammePrixRepository, ParametreAtelierRepository parametreAtelierRepository,
            PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        return args -> {

            // 0. Nettoyage des contraintes CHECK héritées (idempotent)
            try {
                jdbcTemplate.execute("DO $$ DECLARE r RECORD; BEGIN FOR r IN (SELECT conname FROM pg_constraint WHERE conrelid = 'roles'::regclass AND contype = 'c') LOOP EXECUTE 'ALTER TABLE roles DROP CONSTRAINT ' || r.conname; END LOOP; END $$;");
                jdbcTemplate.execute("DO $$ DECLARE r RECORD; BEGIN FOR r IN (SELECT conname FROM pg_constraint WHERE conrelid = 'historique_statuts_demande'::regclass AND contype = 'c') LOOP EXECUTE 'ALTER TABLE historique_statuts_demande DROP CONSTRAINT ' || r.conname; END LOOP; END $$;");
                jdbcTemplate.execute("DO $$ DECLARE r RECORD; BEGIN FOR r IN (SELECT conname FROM pg_constraint WHERE conrelid = 'demandes_intervention'::regclass AND contype = 'c') LOOP EXECUTE 'ALTER TABLE demandes_intervention DROP CONSTRAINT ' || r.conname; END LOOP; END $$;");
                log.info("Nettoyage des contraintes CHECK effectué.");
            } catch (Exception e) {
                log.warn("Nettoyage des contraintes CHECK partiel ou déjà fait.");
            }

            // 1. Initialisation des rôles
            for (ERole eRole : ERole.values()) {
                if (!roleRepository.existsByName(eRole)) {
                    Role role = new Role();
                    role.setName(eRole);
                    roleRepository.save(role);
                    log.info("Rôle créé : {}", eRole);
                }
            }

            // 2. Administrateur par défaut
            // Le mot de passe vient de app.admin.initial-password (variable d'env DIWA_ADMIN_PASSWORD).
            // mustResetPassword = true → l'admin DOIT changer son mot de passe au premier login.
            String adminEmail = "admin@diwa.tg";
            String adminUsername = "admin";
            if (userRepository.findByEmail(adminEmail).isEmpty()
                    && userRepository.findByUsername(adminUsername).isEmpty()) {

                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminInitialPassword));
                admin.setEmailVerified(true);
                admin.setMustResetPassword(true);
                admin.setTwoFactorEnabled(false);

                Role adminRole = roleRepository.findAll().stream()
                        .filter(r -> r.getName() == ERole.ROLE_ADMIN)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Rôle ADMIN introuvable"));
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                admin.setRoles(roles);

                userRepository.save(admin);
                log.info("Administrateur créé : {} (changement de mot de passe requis au premier login)", adminEmail);
            }

            // 2.1 Comptes de test (uniquement si la propriété le permet — jamais en production)
            // Tous créés avec mustResetPassword = true.
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_RECEPTIONNISTE, "reception@diwa.tg", "reception");
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_CHEF_TECHNICIEN, "cheftech@diwa.tg", "cheftech");
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_TECHNICIEN, "tech@diwa.tg", "tech");
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_CHAUFFEUR, "chauffeur@diwa.tg", "chauffeur");
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_CLIENT, "client@diwa.tg", "client");
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_STOCK, "stock@diwa.tg", "stock");
            createTestUser(userRepository, roleRepository, passwordEncoder, ERole.ROLE_DG, "dg@diwa.tg", "dg");

            // 2.2 Paramètres de l'atelier
            if (parametreAtelierRepository.count() == 0) {
                parametreAtelierRepository.save(new ParametreAtelier());
                log.info("Paramètres de l'atelier initialisés par défaut.");
            }

            // 3. Gammes de prix
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
                log.info("Gammes de prix initialisées.");
            }

            // 4. Catégories de pièces
            for (String catName : new String[]{"Lubrifiants", "Moteurs", "Accessoires", "Électricité"}) {
                if (categoriePieceRepository.findAll().stream()
                        .noneMatch(c -> c.getLibelle().equalsIgnoreCase(catName))) {
                    CategoriePiece cat = new CategoriePiece();
                    cat.setLibelle(catName);
                    categoriePieceRepository.save(cat);
                }
            }

            // 5. Services SAV
            String[][] servicesInit = {
                {"Mécanique & Moteur", "Diagnostic et réparation mécanique lourde"},
                {"Électronique & Bord", "Réparation systèmes électriques et informatiques"},
                {"Entretien & Révision", "Maintenance périodique certifiée"},
                {"Carrosserie & Esthétique", "Remise à neuf extérieure et peinture"},
                {"Pièces Détachées", "Vente et installation de pièces d'origine"}
            };
            for (String[] s : servicesInit) {
                boolean exists = serviceSAVRepository.findAll().stream()
                        .anyMatch(serv -> serv.getLibelle().equalsIgnoreCase(s[0]));
                if (!exists) {
                    ServiceSAV service = new ServiceSAV();
                    service.setLibelle(s[0]);
                    service.setDescription(s[1]);
                    service.setPrixBase(0.0);
                    serviceSAVRepository.save(service);
                    log.info("Service SAV créé : {}", s[0]);
                }
            }

            // 6. Plages horaires DIWA
            String[][] plages = {
                {"06:00", "09:00", "1"},
                {"09:00", "12:00", "2"},
                {"14:00", "16:00", "3"},
                {"16:00", "18:00", "4"}
            };
            for (String[] p : plages) {
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM plage_horaire_diwa WHERE heure_debut = ?::time AND heure_fin = ?::time",
                    Integer.class, p[0], p[1]);
                if (count != null && count == 0) {
                    jdbcTemplate.execute(String.format(
                        "INSERT INTO plage_horaire_diwa (heure_debut, heure_fin, active, ordre, create_date, uuid) VALUES ('%s', '%s', true, %s, now(), gen_random_uuid())",
                        p[0], p[1], p[2]));
                    log.info("Plage horaire créée : {} - {}", p[0], p[1]);
                }
            }
        };
    }

    private void createTestUser(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, ERole eRole, String email, String username) {
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByUsername(username).isPresent()) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEmailVerified(true);
        user.setMustResetPassword(true); // Changement obligatoire au premier login
        user.setTwoFactorEnabled(false);

        Role role = roleRepository.findByName(eRole)
                .orElseThrow(() -> new RuntimeException("Rôle " + eRole + " introuvable"));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);
        log.info("Compte de test créé : {} (rôle: {}, reset requis)", email, eRole);
    }
}
