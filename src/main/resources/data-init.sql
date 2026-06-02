-- DIWA Internationale - Initialisation des données (Week 3)

-- 0. Rôles et Utilisateur Admin (Password: admin123)
INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER'), (2, 'ROLE_MODERATOR'), (3, 'ROLE_ADMIN');

INSERT INTO users (id, username, email, password, email_verified, must_reset_password, two_factor_enabled, date_creation)
VALUES (1, 'admin', 'admin@diwa.tg', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', true, false, false, CURRENT_TIMESTAMP);

INSERT INTO user_roles (user_id, role_id) VALUES (1, 3);

-- 1. Techniciens DIWA
INSERT INTO technicien (id, nom, prenom, specialite, actif, date_creation, version)
VALUES 
(1, 'Koffi', 'Ata', 'MÉCANIQUE GÉNÉRALE', true, CURRENT_TIMESTAMP, 0),
(2, 'Mensah', 'Paul', 'ÉLECTRONIQUE & DIAGNOSTIC', true, CURRENT_TIMESTAMP, 0),
(3, 'Lawson', 'Eric', 'CLIMATISATION & FILTRAGE', true, CURRENT_TIMESTAMP, 0);

-- 2. Plages Horaires (Exemple pour Lundi)
-- Technicien 1
INSERT INTO plage_horaire (id, technicien_id, jour_semaine, heure_debut, heure_fin, est_disponible)
VALUES 
(1, 1, 'MONDAY', '08:00:00', '09:00:00', true),
(2, 1, 'MONDAY', '09:00:00', '10:00:00', true),
(3, 1, 'MONDAY', '10:00:00', '11:00:00', true),
(4, 1, 'MONDAY', '11:00:00', '12:00:00', true),
(5, 1, 'MONDAY', '14:00:00', '15:00:00', true),
(6, 1, 'MONDAY', '15:00:00', '16:00:00', true),
(7, 1, 'MONDAY', '16:00:00', '17:00:00', true),
(8, 1, 'MONDAY', '17:00:00', '18:00:00', true);

-- Technicien 2
INSERT INTO plage_horaire (id, technicien_id, jour_semaine, heure_debut, heure_fin, est_disponible)
VALUES 
(9, 2, 'MONDAY', '08:00:00', '09:00:00', true),
(10, 2, 'MONDAY', '09:00:00', '10:00:00', true),
(11, 2, 'MONDAY', '10:00:00', '11:00:00', true),
(12, 2, 'MONDAY', '11:00:00', '12:00:00', true),
(13, 2, 'MONDAY', '14:00:00', '15:00:00', true);

-- 3. Véhicules (Marques MG, ISUZU, CHEVROLET, BAIC)
INSERT INTO vehicule (id, uuid, marque, modele, prix_ttc, description, image_url, fichier_glb, actif, date_creation)
VALUES 
(1, 'uuid-mg-rx8', 'MG', 'MG RX8', 18500000, 'Le SUV de luxe 7 places par excellence.', '/images/mg_rx8.jpg', '/models/mg_rx8.glb', true, CURRENT_TIMESTAMP),
(2, 'uuid-isuzu-dmax', 'ISUZU', 'D-MAX V-CROSS', 22000000, 'La puissance et la robustesse sans compromis.', '/images/isuzu_dmax.jpg', '/models/isuzu_dmax.glb', true, CURRENT_TIMESTAMP),
(3, 'uuid-chevrolet-traverse', 'CHEVROLET', 'TRAVERSE', 25000000, 'Le confort américain pour toute la famille.', '/images/chevrolet_traverse.jpg', '/models/chevrolet_traverse.glb', true, CURRENT_TIMESTAMP),
(4, 'uuid-baic-bj40', 'BAIC', 'BJ40 EXTREME', 19000000, 'Le tout-terrain pur et dur pour vos aventures.', '/images/baic_bj40.jpg', '/models/baic_bj40.glb', true, CURRENT_TIMESTAMP);

-- 4. Catégories de pièces
INSERT INTO categorie_piece (id, nom, description)
VALUES 
(1, 'LUBRIFIANTS', 'Huiles moteur, transmission et liquides de frein.'),
(2, 'ACCESSOIRES', 'Équipements intérieurs et extérieurs.'),
(3, 'PNEUMATIQUES', 'Pneus toutes marques et jantes.'),
(4, 'ENTRETIEN', 'Filtres, bougies et balais d\'essuie-glace.');
