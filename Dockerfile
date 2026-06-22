# Stage 1: Build
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Assets catalogue pré-chargés (vehicules, 360°, pièces, modèles 3D)
# Le volume Docker copiera ces fichiers au premier démarrage si le volume est vide
COPY uploads/ /app/uploads/

EXPOSE 8181
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
