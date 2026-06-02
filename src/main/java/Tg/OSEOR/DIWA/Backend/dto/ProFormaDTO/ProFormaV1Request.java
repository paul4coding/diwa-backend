package Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO;

import java.math.BigDecimal;
import java.util.List;

public class ProFormaV1Request {
    private String demandeId;
    private List<LigneTravailRequest> lignesTravaux;
    private List<LigneMainOeuvreRequest> lignesMainOeuvre;

    public String getDemandeId() { return demandeId; }
    public void setDemandeId(String d) { this.demandeId = d; }
    public List<LigneTravailRequest> getLignesTravaux() { return lignesTravaux; }
    public void setLignesTravaux(List<LigneTravailRequest> l) { this.lignesTravaux = l; }
    public List<LigneMainOeuvreRequest> getLignesMainOeuvre() { return lignesMainOeuvre; }
    public void setLignesMainOeuvre(List<LigneMainOeuvreRequest> l) { this.lignesMainOeuvre = l; }

    // ─── Pièce / travaux ──────────────────────────────────────
    public static class LigneTravailRequest {
        private String designation;
        private Long pieceDetacheeId;
        private String referencePieceLibre;
        private String marquePiece;
        private String modelePiece;
        private BigDecimal quantite;

        /**
         * MO directement liée à cette pièce (optionnel).
         * Si présente, une LigneProFormaMainOeuvre est automatiquement créée
         * et associée à cette ligne travaux.
         */
        private LigneMainOeuvreAssocieeRequest mainOeuvreAssociee;

        public String getDesignation() { return designation; }
        public void setDesignation(String d) { this.designation = d; }
        public Long getPieceDetacheeId() { return pieceDetacheeId; }
        public void setPieceDetacheeId(Long p) { this.pieceDetacheeId = p; }
        public String getReferencePieceLibre() { return referencePieceLibre; }
        public void setReferencePieceLibre(String r) { this.referencePieceLibre = r; }
        public String getMarquePiece() { return marquePiece; }
        public void setMarquePiece(String m) { this.marquePiece = m; }
        public String getModelePiece() { return modelePiece; }
        public void setModelePiece(String m) { this.modelePiece = m; }
        public BigDecimal getQuantite() { return quantite; }
        public void setQuantite(BigDecimal q) { this.quantite = q; }
        public LigneMainOeuvreAssocieeRequest getMainOeuvreAssociee() { return mainOeuvreAssociee; }
        public void setMainOeuvreAssociee(LigneMainOeuvreAssocieeRequest mo) { this.mainOeuvreAssociee = mo; }
    }

    // ─── MO associée (inline dans une pièce) ─────────────────
    public static class LigneMainOeuvreAssocieeRequest {
        private String typeIntervention;
        /** Durée en minutes (ex : 30 = 0,5h, 90 = 1h30). */
        private Integer dureeMinutes;
        private String commentaireTechnicien;

        public String getTypeIntervention() { return typeIntervention; }
        public void setTypeIntervention(String t) { this.typeIntervention = t; }
        public Integer getDureeMinutes() { return dureeMinutes; }
        public void setDureeMinutes(Integer d) { this.dureeMinutes = d; }
        public String getCommentaireTechnicien() { return commentaireTechnicien; }
        public void setCommentaireTechnicien(String c) { this.commentaireTechnicien = c; }
    }

    // ─── MO autonome (non liée à une pièce) ──────────────────
    public static class LigneMainOeuvreRequest {
        private String typeIntervention;
        /** Durée en minutes — prioritaire sur heures. */
        private Integer dureeMinutes;
        /** Durée en heures (ancienne API, conservée pour compat). */
        private BigDecimal heures;
        private String commentaireTechnicien;

        public String getTypeIntervention() { return typeIntervention; }
        public void setTypeIntervention(String t) { this.typeIntervention = t; }
        public Integer getDureeMinutes() { return dureeMinutes; }
        public void setDureeMinutes(Integer d) { this.dureeMinutes = d; }
        public BigDecimal getHeures() { return heures; }
        public void setHeures(BigDecimal h) { this.heures = h; }
        public String getCommentaireTechnicien() { return commentaireTechnicien; }
        public void setCommentaireTechnicien(String c) { this.commentaireTechnicien = c; }
    }
}
