package Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProFormaDTOResponse {
    private Long id;
    private String reference;
    private String statut;
    private Long demandeId;
    private String demandeUuid;
    private BigDecimal fraisDiagnostic;
    private Boolean diagnosticGratuit;
    private String motifGratuite;
    private BigDecimal fraisRecuperation;
    private BigDecimal fraisLivraison;
    
    private BigDecimal totalPieces;
    private BigDecimal totalMainOeuvre;
    private BigDecimal totalFraisAnnexes;
    private BigDecimal totalGeneral;
    private String couponCode;
    private BigDecimal montantRemise;
    private BigDecimal totalApresRemise;
    
    private String chefTechnicienNom;
    private String receptionnisteNom;
    private LocalDateTime datePrevRestitution;
    private Integer dureeTotaleMinutes;
    private String noteInterne;
    
    private String pdfUrlClient;
    private String pdfUrlTechnicien;
    private String pdfUrlReceptionniste;
    
    private List<LigneTravailDTO> lignesTravaux;
    private List<LigneMainOeuvreDTO> lignesMainOeuvre;

    // Champs de la demande
    private String clientNom;
    private String vehiculeMarque;
    private String vehiculeModele;
    private String vehiculeImmatriculation;
    private Boolean demandeLivraison;
    private String adresseLivraison;
    private String demandeStatut;

    public ProFormaDTOResponse() {}

    // Inner DTO classes
    public static class LigneTravailDTO {
        private Long id;
        private Integer position;
        private String designation;
        private Long pieceId;
        private String marquePiece;
        private String modelePiece;
        private String referencePieceLibre;
        private BigDecimal quantite;
        private BigDecimal prixUnitaire;
        private BigDecimal prixTotal;
        private BigDecimal prixMinConseille;
        private BigDecimal prixMaxConseille;
        private Boolean cocheeParClient;
        private String statut;

        // Getters / Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getPosition() { return position; }
        public void setPosition(Integer p) { this.position = p; }
        public String getDesignation() { return designation; }
        public void setDesignation(String d) { this.designation = d; }
        public Long getPieceId() { return pieceId; }
        public void setPieceId(Long p) { this.pieceId = p; }
        public String getMarquePiece() { return marquePiece; }
        public void setMarquePiece(String m) { this.marquePiece = m; }
        public String getModelePiece() { return modelePiece; }
        public void setModelePiece(String m) { this.modelePiece = m; }
        public String getReferencePieceLibre() { return referencePieceLibre; }
        public void setReferencePieceLibre(String r) { this.referencePieceLibre = r; }
        public BigDecimal getQuantite() { return quantite; }
        public void setQuantite(BigDecimal q) { this.quantite = q; }
        public BigDecimal getPrixUnitaire() { return prixUnitaire; }
        public void setPrixUnitaire(BigDecimal p) { this.prixUnitaire = p; }
        public BigDecimal getPrixTotal() { return prixTotal; }
        public void setPrixTotal(BigDecimal p) { this.prixTotal = p; }
        public BigDecimal getPrixMinConseille() { return prixMinConseille; }
        public void setPrixMinConseille(BigDecimal p) { this.prixMinConseille = p; }
        public BigDecimal getPrixMaxConseille() { return prixMaxConseille; }
        public void setPrixMaxConseille(BigDecimal p) { this.prixMaxConseille = p; }
        public Boolean getCocheeParClient() { return cocheeParClient; }
        public void setCocheeParClient(Boolean c) { this.cocheeParClient = c; }
        public String getStatut() { return statut; }
        public void setStatut(String s) { this.statut = s; }
    }

    public static class LigneMainOeuvreDTO {
        private Long id;
        private Integer position;
        private String typeIntervention;
        private BigDecimal heures;
        /** Durée en minutes (source de vérité côté technicien). */
        private Integer dureeMinutes;
        private String commentaireTechnicien;
        private BigDecimal tauxHoraire;
        private BigDecimal total;
        private Boolean cocheeParClient;
        /** ID de la LigneProFormaTravail associée (null si MO autonome). */
        private Long ligneTravailId;

        // Getters / Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getPosition() { return position; }
        public void setPosition(Integer p) { this.position = p; }
        public String getTypeIntervention() { return typeIntervention; }
        public void setTypeIntervention(String t) { this.typeIntervention = t; }
        public BigDecimal getHeures() { return heures; }
        public void setHeures(BigDecimal h) { this.heures = h; }
        public Integer getDureeMinutes() { return dureeMinutes; }
        public void setDureeMinutes(Integer d) { this.dureeMinutes = d; }
        public String getCommentaireTechnicien() { return commentaireTechnicien; }
        public void setCommentaireTechnicien(String c) { this.commentaireTechnicien = c; }
        public BigDecimal getTauxHoraire() { return tauxHoraire; }
        public void setTauxHoraire(BigDecimal t) { this.tauxHoraire = t; }
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal t) { this.total = t; }
        public Boolean getCocheeParClient() { return cocheeParClient; }
        public void setCocheeParClient(Boolean c) { this.cocheeParClient = c; }
        public Long getLigneTravailId() { return ligneTravailId; }
        public void setLigneTravailId(Long l) { this.ligneTravailId = l; }
    }

    // Getters / Setters for ProFormaDTOResponse
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String r) { this.reference = r; }
    public String getStatut() { return statut; }
    public void setStatut(String s) { this.statut = s; }
    public Long getDemandeId() { return demandeId; }
    public void setDemandeId(Long d) { this.demandeId = d; }
    public String getDemandeUuid() { return demandeUuid; }
    public void setDemandeUuid(String u) { this.demandeUuid = u; }
    public BigDecimal getFraisDiagnostic() { return fraisDiagnostic; }
    public void setFraisDiagnostic(BigDecimal f) { this.fraisDiagnostic = f; }
    public Boolean getDiagnosticGratuit() { return diagnosticGratuit; }
    public void setDiagnosticGratuit(Boolean d) { this.diagnosticGratuit = d; }
    public String getMotifGratuite() { return motifGratuite; }
    public void setMotifGratuite(String m) { this.motifGratuite = m; }
    public BigDecimal getFraisRecuperation() { return fraisRecuperation; }
    public void setFraisRecuperation(BigDecimal f) { this.fraisRecuperation = f; }
    public BigDecimal getFraisLivraison() { return fraisLivraison; }
    public void setFraisLivraison(BigDecimal f) { this.fraisLivraison = f; }
    public BigDecimal getTotalPieces() { return totalPieces; }
    public void setTotalPieces(BigDecimal t) { this.totalPieces = t; }
    public BigDecimal getTotalMainOeuvre() { return totalMainOeuvre; }
    public void setTotalMainOeuvre(BigDecimal t) { this.totalMainOeuvre = t; }
    public BigDecimal getTotalFraisAnnexes() { return totalFraisAnnexes; }
    public void setTotalFraisAnnexes(BigDecimal t) { this.totalFraisAnnexes = t; }
    public BigDecimal getTotalGeneral() { return totalGeneral; }
    public void setTotalGeneral(BigDecimal t) { this.totalGeneral = t; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String c) { this.couponCode = c; }
    public BigDecimal getMontantRemise() { return montantRemise; }
    public void setMontantRemise(BigDecimal m) { this.montantRemise = m; }
    public BigDecimal getTotalApresRemise() { return totalApresRemise; }
    public void setTotalApresRemise(BigDecimal t) { this.totalApresRemise = t; }
    public String getChefTechnicienNom() { return chefTechnicienNom; }
    public void setChefTechnicienNom(String c) { this.chefTechnicienNom = c; }
    public String getReceptionnisteNom() { return receptionnisteNom; }
    public void setReceptionnisteNom(String r) { this.receptionnisteNom = r; }
    public LocalDateTime getDatePrevRestitution() { return datePrevRestitution; }
    public void setDatePrevRestitution(LocalDateTime d) { this.datePrevRestitution = d; }
    public Integer getDureeTotaleMinutes() { return dureeTotaleMinutes; }
    public void setDureeTotaleMinutes(Integer d) { this.dureeTotaleMinutes = d; }
    public String getNoteInterne() { return noteInterne; }
    public void setNoteInterne(String n) { this.noteInterne = n; }
    public String getPdfUrlClient() { return pdfUrlClient; }
    public void setPdfUrlClient(String p) { this.pdfUrlClient = p; }
    public String getPdfUrlTechnicien() { return pdfUrlTechnicien; }
    public void setPdfUrlTechnicien(String p) { this.pdfUrlTechnicien = p; }
    public String getPdfUrlReceptionniste() { return pdfUrlReceptionniste; }
    public void setPdfUrlReceptionniste(String p) { this.pdfUrlReceptionniste = p; }
    public List<LigneTravailDTO> getLignesTravaux() { return lignesTravaux; }
    public void setLignesTravaux(List<LigneTravailDTO> l) { this.lignesTravaux = l; }
    public List<LigneMainOeuvreDTO> getLignesMainOeuvre() { return lignesMainOeuvre; }
    public void setLignesMainOeuvre(List<LigneMainOeuvreDTO> l) { this.lignesMainOeuvre = l; }

    public String getClientNom() { return clientNom; }
    public void setClientNom(String c) { this.clientNom = c; }
    public String getVehiculeMarque() { return vehiculeMarque; }
    public void setVehiculeMarque(String v) { this.vehiculeMarque = v; }
    public String getVehiculeModele() { return vehiculeModele; }
    public void setVehiculeModele(String v) { this.vehiculeModele = v; }
    public String getVehiculeImmatriculation() { return vehiculeImmatriculation; }
    public void setVehiculeImmatriculation(String v) { this.vehiculeImmatriculation = v; }
    public Boolean getDemandeLivraison() { return demandeLivraison; }
    public void setDemandeLivraison(Boolean d) { this.demandeLivraison = d; }
    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String a) { this.adresseLivraison = a; }
    public String getDemandeStatut() { return demandeStatut; }
    public void setDemandeStatut(String s) { this.demandeStatut = s; }
}
