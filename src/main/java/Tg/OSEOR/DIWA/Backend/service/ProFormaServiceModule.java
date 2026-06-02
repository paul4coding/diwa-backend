package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.AjoutPrixRequest;
import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.ProFormaDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.ProFormaV1Request;
import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.SelectionClientRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ProFormaServiceModule {
    ProFormaDTOResponse creerV1(String demandeId, ProFormaV1Request request, String chefTechEmail);
    List<ProFormaDTOResponse> getAwaitingAssignment();
    List<Tg.OSEOR.DIWA.Backend.dto.AtelierDTO.AffectationDTO> getAllAffectations();
    ProFormaDTOResponse getLignesTechnicien(Long proFormaId);
    ProFormaDTOResponse getVersionReceptionniste(Long proFormaId);
    ProFormaDTOResponse ajouterPrix(Long proFormaId, AjoutPrixRequest request, String receptionnisteEmail);
    ProFormaDTOResponse envoyerAuClient(Long proFormaId, String receptionnisteEmail);
    ProFormaDTOResponse getVersionClient(Long proFormaId, String userEmail);
    ProFormaDTOResponse soumettreSelection(Long proFormaId, SelectionClientRequest request, String userEmail);
    ProFormaDTOResponse validerSelectionClient(Long proFormaId, String receptionnisteEmail);
    ProFormaDTOResponse mettreAJourLivraison(Long proFormaId, BigDecimal fraisLivraison, String receptionnisteEmail);
    ProFormaDTOResponse confirmerFinal(Long proFormaId, String userEmail);
    ProFormaDTOResponse confirmerProForma(Long proFormaId, String userEmail);
    void updateStatutLigne(Long ligneId, String nouveauStatut, String techEmail);
    
    // Affectation de travail (Chef Technicien)
    void affecterTravail(Long proFormaId, Long technicienId, LocalDateTime dateDebut, LocalDateTime dateFinPrevue, String notes, String chefTechEmail);
    void modifierAffectation(Long proFormaId, Long technicienId, LocalDateTime dateDebut, LocalDateTime dateFinPrevue, String notes, String chefTechEmail);
    void confirmerFinTravaux(Long proFormaId, String chefTechEmail);

    // PDF & Excel
    byte[] genererPdfClient(Long proFormaId);
    byte[] genererPdfTechnicien(Long proFormaId);
    byte[] genererPdfReceptionniste(Long proFormaId);
    byte[] genererExcel(Long proFormaId);
}
