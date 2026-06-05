package Tg.OSEOR.DIWA.Backend.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Impossible de créer le répertoire d'upload.", ex);
        }
    }

    // Extensions autorisées pour les uploads
    private static final java.util.Set<String> ALLOWED_EXTENSIONS = java.util.Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".pdf", ".glb"
    );

    public String storeFile(MultipartFile file, String subfolder) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            // Extraire et valider l'extension
            String extension = "";
            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                extension = fileName.substring(i).toLowerCase();
            }
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new RuntimeException("Extension de fichier non autorisée : " + extension);
            }

            // Générer un nom de fichier aléatoire (pas de traversal possible)
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // Créer le sous-répertoire et vérifier le confinement
            Path targetLocation = this.fileStorageLocation.resolve(subfolder).normalize();
            if (!targetLocation.startsWith(this.fileStorageLocation)) {
                throw new RuntimeException("Chemin de dossier non autorisé : " + subfolder);
            }
            Files.createDirectories(targetLocation);

            Path filePath = targetLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return subfolder + "/" + uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de stocker le fichier " + fileName, ex);
        }
    }

    public String storePdf(byte[] content, String fileName) {
        try {
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            Path targetLocation = this.fileStorageLocation.resolve("proformas");
            Files.createDirectories(targetLocation);

            Path filePath = targetLocation.resolve(uniqueFileName);
            Files.write(filePath, content);

            return "proformas/" + uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de stocker le PDF " + fileName, ex);
        }
    }

    public String storeFacture(byte[] content, String fileName) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve("factures");
            Files.createDirectories(targetLocation);
            Path filePath = targetLocation.resolve(fileName);
            Files.write(filePath, content);
            return "factures/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de stocker la facture " + fileName, ex);
        }
    }

    public byte[] readFile(String filePathString) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filePathString).normalize();
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new SecurityException("Accès interdit hors du répertoire uploads : " + filePathString);
            }
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de lire le fichier : " + filePathString, ex);
        }
    }

    public void deleteFile(String filePathString) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filePathString).normalize();
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new SecurityException("Accès interdit hors du répertoire uploads : " + filePathString);
            }
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            System.err.println("Erreur lors de la suppression du fichier : " + filePathString);
        }
    }

    public String store360Files(MultipartFile[] files, String folderName) {
        String cleanFolderName = StringUtils.cleanPath(folderName).replaceAll("[^a-zA-Z0-9_-]", "_");
        System.out.println("Début upload 360 pour dossier : " + cleanFolderName);
        
        try {
            // Dossier de destination : uploads/assets/360/{folderName}
            Path targetLocation = this.fileStorageLocation.resolve("assets/360").resolve(cleanFolderName);
            System.out.println("Chemin cible : " + targetLocation.toAbsolutePath());
            
            Files.createDirectories(targetLocation);

            int count = 1;
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null) continue;
                
                // On force le nom à être 1.jpg, 2.jpg... pour une compatibilité parfaite
                String fileName = count + ".jpg";
                Path filePath = targetLocation.resolve(fileName);
                
                System.out.println("Stockage du fichier " + count + " vers : " + filePath.toAbsolutePath());
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                count++;
            }
            return cleanFolderName;
        } catch (IOException ex) {
            System.err.println("Erreur store360Files : " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Impossible de stocker le dossier 360 : " + cleanFolderName, ex);
        }
    }

    public void deleteDirectory(String folderPathString) {
        try {
            Path folderPath = this.fileStorageLocation.resolve(folderPathString).normalize();
            if (!folderPath.startsWith(this.fileStorageLocation)) {
                throw new SecurityException("Accès interdit hors du répertoire uploads : " + folderPathString);
            }
            if (Files.exists(folderPath)) {
                Files.walk(folderPath)
                        .sorted((p1, p2) -> p2.compareTo(p1))
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                System.err.println("Erreur suppression : " + p);
                            }
                        });
            }
        } catch (IOException ex) {
            System.err.println("Erreur dossier : " + folderPathString);
        }
    }
}
