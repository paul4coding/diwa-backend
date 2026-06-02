package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.entity.ProForma;
import Tg.OSEOR.DIWA.Backend.repository.DemandeInterventionRepository;
import Tg.OSEOR.DIWA.Backend.repository.ProFormaRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import Tg.OSEOR.DIWA.Backend.service.document.DocumentGenerationService;
import Tg.OSEOR.DIWA.Backend.service.media.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FactureServiceImpl implements FactureService {

    @Autowired private DemandeInterventionRepository demandeRepo;
    @Autowired private ProFormaRepository            proFormaRepo;
    @Autowired private UserRepository                userRepo;
    @Autowired private DocumentGenerationService     docService;
    @Autowired private FileStorageService            fileService;
    @Autowired private EmailService                  emailService;

    // ─── Génération automatique ──────────────────────────────────────────────

    @Override
    @Transactional
    public byte[] generer(Long demandeId) {
        DemandeIntervention demande = demandeRepo.findById(demandeId)
            .orElseThrow(() -> new RuntimeException("Demande introuvable ID=" + demandeId));

        // Génération du PDF
        byte[] pdfBytes = docService.genererFactureCloture(demande);

        // Nom de fichier déterministe pour écraser les anciens si régénération
        String ref = demande.getReference() != null ? demande.getReference() : "D" + demandeId;
        String fileName = ref.replaceAll("[^a-zA-Z0-9_-]", "_") + "_facture.pdf";
        String storedPath = fileService.storeFacture(pdfBytes, fileName);

        // Persistance de l'URL sur le ProForma
        ProForma pf = demande.getProForma();
        if (pf != null) {
            pf.setFactureUrl(storedPath);
            pf.setFactureGenereeAt(LocalDateTime.now());
            proFormaRepo.save(pf);
        }

        // Email asynchrone avec la facture en pièce jointe
        try {
            if (demande.getClient() != null && demande.getClient().getEmail() != null) {
                emailService.sendFactureCloture(
                    demande.getClient().getEmail(),
                    demande.getClient().getPrenom() != null ? demande.getClient().getPrenom() : "Client",
                    demande.getVehiculeMarque(),
                    demande.getVehiculeImmatriculation(),
                    demande.getReference(),
                    pdfBytes
                );
            }
        } catch (Exception e) {
            System.err.println("[DIWA Facture] Email ignoré : " + e.getMessage());
        }

        return pdfBytes;
    }

    // ─── Téléchargement (avec vérification accès) ────────────────────────────

    @Override
    public byte[] telecharger(Long demandeId, String identifier) {
        DemandeIntervention demande = demandeRepo.findById(demandeId)
            .orElseThrow(() -> new RuntimeException("Demande introuvable ID=" + demandeId));

        User user = userRepo.findByIdentifier(identifier)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + identifier));

        // Vérification d'accès : propriétaire OU rôle atelier
        boolean isOwner = demande.getClient() != null
            && demande.getClient().getId().equals(user.getId());
        boolean isStaff = user.getRoles().stream()
            .anyMatch(r -> r.getName() != null && r.getName().name().matches(
                "ROLE_RECEPTIONNISTE|ROLE_CHEF_TECHNICIEN|ROLE_ADMIN"));

        if (!isOwner && !isStaff) {
            throw new SecurityException("Accès non autorisé à cette facture");
        }

        // Si déjà générée et fichier présent → lire depuis disque
        ProForma pf = demande.getProForma();
        if (pf != null && pf.getFactureUrl() != null) {
            try {
                return fileService.readFile(pf.getFactureUrl());
            } catch (Exception e) {
                // Fichier absent → régénérer
                System.err.println("[DIWA Facture] Fichier absent, régénération : " + e.getMessage());
            }
        }

        // Régénération à la volée (sans re-stocker ni re-envoyer l'email)
        return docService.genererFactureCloture(demande);
    }
}
