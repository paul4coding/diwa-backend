package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.service.media.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    // Dossiers autorisés — liste blanche stricte
    private static final Set<String> ALLOWED_FOLDERS = Set.of(
            "missions/checking", "missions/livraison", "proformas",
            "vehicules", "pieces", "cms", "general", "assets/360"
    );

    private final FileStorageService fileStorageService;

    public MediaController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {

        // Valider que le dossier est dans la liste blanche
        if (!ALLOWED_FOLDERS.contains(folder)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Dossier non autorisé : " + folder));
        }

        String filePath = fileStorageService.storeFile(file, folder);
        String fileUrl = "/uploads/" + filePath;

        return ResponseEntity.ok(Map.of("url", fileUrl));
    }
}
