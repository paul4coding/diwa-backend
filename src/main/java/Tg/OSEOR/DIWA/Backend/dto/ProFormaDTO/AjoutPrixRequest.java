package Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO;

import java.math.BigDecimal;
import java.util.List;

public class AjoutPrixRequest {
    private BigDecimal fraisDiagnostic;
    private BigDecimal fraisRecuperation;
    private BigDecimal fraisLivraison;
    private List<LignePrixDTO> lignesTravaux;
    private List<LigneMOPrixDTO> lignesMainOeuvre;

    public BigDecimal getFraisDiagnostic() { return fraisDiagnostic; }
    public void setFraisDiagnostic(BigDecimal f) { this.fraisDiagnostic = f; }
    public BigDecimal getFraisRecuperation() { return fraisRecuperation; }
    public void setFraisRecuperation(BigDecimal f) { this.fraisRecuperation = f; }
    public BigDecimal getFraisLivraison() { return fraisLivraison; }
    public void setFraisLivraison(BigDecimal f) { this.fraisLivraison = f; }
    public List<LignePrixDTO> getLignesTravaux() { return lignesTravaux; }
    public void setLignesTravaux(List<LignePrixDTO> l) { this.lignesTravaux = l; }
    public List<LigneMOPrixDTO> getLignesMainOeuvre() { return lignesMainOeuvre; }
    public void setLignesMainOeuvre(List<LigneMOPrixDTO> l) { this.lignesMainOeuvre = l; }

    public static class LignePrixDTO {
        private Long ligneId;
        private BigDecimal prixUnitaire;

        public Long getLigneId() { return ligneId; }
        public void setLigneId(Long l) { this.ligneId = l; }
        public BigDecimal getPrixUnitaire() { return prixUnitaire; }
        public void setPrixUnitaire(BigDecimal p) { this.prixUnitaire = p; }
    }

    public static class LigneMOPrixDTO {
        private Long ligneId;
        private BigDecimal tauxHoraire;

        public Long getLigneId() { return ligneId; }
        public void setLigneId(Long l) { this.ligneId = l; }
        public BigDecimal getTauxHoraire() { return tauxHoraire; }
        public void setTauxHoraire(BigDecimal t) { this.tauxHoraire = t; }
    }
}
