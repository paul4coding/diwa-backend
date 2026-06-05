# DIWA Backend — API Spring Boot

API REST de la plateforme DIWA Internationale (gestion de garage automobile).

---

## Stack

| Technologie | Version |
|---|---|
| Java | 17 (LTS) |
| Spring Boot | 3.4.3 |
| PostgreSQL | 15 |
| Maven | 3.8+ |
| Port | 8181 |

---

## Démarrage en développement

```bash
git clone https://github.com/paul4coding/diwa-backend.git
cd diwa-backend

# Lancer (profil dev — lit application-dev.properties)
./mvnw spring-boot:run

# Swagger UI disponible sur :
# http://localhost:8181/swagger-ui.html
```

**Prérequis :** PostgreSQL 15 lancé localement avec la base `DIWA_DB`.

```sql
CREATE DATABASE "DIWA_DB" WITH ENCODING = 'UTF8' TEMPLATE = template0;
```

---

## Build production

```bash
./mvnw package -DskipTests
java -jar target/DIWABackend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## Déploiement Docker (recommandé)

### 1. Cloner les trois dépôts côte à côte

```bash
git clone https://github.com/paul4coding/diwa-backend.git
git clone https://github.com/paul4coding/diwa-frontend.git
```

### 2. Créer le fichier `.env` à la racine du dossier parent

```bash
cp .env.example .env
# Éditer .env avec les vraies valeurs
```

Contenu minimal du `.env` :

```env
POSTGRES_PASSWORD=mot_de_passe_fort
DIWA_APP_JWT_SECRET=cle_jwt_minimum_32_caracteres
SPRING_MAIL_USERNAME=votre@gmail.com
SPRING_MAIL_PASSWORD=app_password_gmail
APP_FRONTEND_URL=https://votre-domaine.com
VITE_API_URL=https://api.votre-domaine.com
```

### 3. Lancer le stack complet

```bash
docker-compose up -d --build

# Vérifier que les 3 services sont UP
docker-compose ps

# Suivre les logs du backend
docker logs diwa-backend -f
```

### 4. Initialiser les données (première fois uniquement)

Après le premier démarrage, Hibernate crée les tables automatiquement.
Exécuter ensuite le script de données initiales :

```bash
docker exec -i diwa-db psql -U postgres -d DIWA_DB < src/main/resources/data.sql
```

### 5. Services exposés

| Service | Port | URL |
|---|---|---|
| Frontend React | 80 | http://votre-ip |
| Backend API | 8181 | http://votre-ip:8181 |
| PostgreSQL | 5432 | réseau interne uniquement |

---

## Variables d'environnement (production)

Voir `.env.example` pour la liste complète.

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Utilisateur PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe PostgreSQL |
| `DIWA_APP_JWT_SECRET` | Clé secrète JWT (min. 32 caractères) |
| `SPRING_MAIL_USERNAME` | Email Gmail expéditeur |
| `SPRING_MAIL_PASSWORD` | App Password Gmail |
| `APP_FRONTEND_URL` | URL publique du frontend (pour les emails) |

---

## Tests

```bash
# Tous les tests
./mvnw test

# Tests spécifiques livraison + facturation
./mvnw test -Dtest="ProFormaDeliveryFlowTest,MissionChauffeurDeliveryTest,FactureClotureTest"
```

---

## Points d'attention déploiement

- **Volume Docker** : monter un volume persistant sur `/app/uploads` (photos missions, factures PDF).
- **CORS** : `APP_FRONTEND_URL` doit correspondre exactement à l'URL publique du frontend.
- **Gmail SMTP** : utiliser un *App Password* (pas le mot de passe du compte).
- **Swagger** : désactivé en profil `prod` par défaut.
