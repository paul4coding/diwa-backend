package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pro_formas")
public class ProForma extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id", nullable = false, unique = true)
    private DemandeIntervention demande;

    @Column(unique = true, length = 30)
    private String reference; // PF-2025-00042

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutProForma statut = StatutProForma.BROUILLON;

    // ── Frais ─────────────────────────────────────────────────
    @Column(precision = 12, scale = 2)
    private BigDecimal fraisDiagnostic;

    @Column(nullable = false)
    private Boolean diagnosticGratuit = false;

    @Column(columnDefinition = "TEXT")
    private String motifGratuite;

    @Column(precision = 12, scale = 2)
    private BigDecimal fraisRecuperation;

    @Column(precision = 12, scale = 2)
    private BigDecimal fraisLivraison;

    // ── Totaux (recalculés à chaque modification) ─────────────
    @Column(precision = 12, scale = 2)
    private BigDecimal totalPieces = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalMainOeuvre = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalFraisAnnexes = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalGeneral = BigDecimal.ZERO;

    // ── Coupon de réduction ───────────────────────────────────
    /** Code du coupon appliqué (null si aucun). */
    @Column(length = 50)
    private String couponCode;

    /** Montant de la remise calculée (null si pas de coupon). */
    @Column(precision = 12, scale = 2)
    private BigDecimal montantRemise;

    // ── Auteurs ───────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chef_technicien_id")
    private User chefTechnicien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receptionniste_id")
    private User receptionniste;

    // Date prévisionnelle (calculée par réceptionniste)
    @Column
    private LocalDateTime datePrevRestitution;

    // Durée totale estimée en minutes (somme des heures technicien)
    @Column
    private Integer dureeTotaleMinutes;

    // ── Note interne ──────────────────────────────────────────
    @Column(columnDefinition = "TEXT")
    private String noteInterne;

    // ── PDFs générés ──────────────────────────────────────────
    @Column(length = 300)
    private String pdfUrlClient;

    @Column(length = 300)
    private String pdfUrlTechnicien; // SANS prix

    @Column(length = 300)
    private String pdfUrlReceptionniste;

    // ── Facture finale (générée à la CLOTURE) ─────────────────
    @Column(length = 300)
    private String factureUrl;

    @Column
    private LocalDateTime factureGenereeAt;

    // ── Lignes ────────────────────────────────────────────────
    @OneToMany(mappedBy = "proForma", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private List<LigneProFormaTravail> lignesTravaux = new ArrayList<>();

    @OneToMany(mappedBy = "proForma", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private List<LigneProFormaMainOeuvre> lignesMainOeuvre = new ArrayList<>();

    public enum StatutProForma {
        BROUILLON,
        SOUMIS_RECEPTIONNISTE,
        PRIX_AJOUTES,
        ENVOYE_CLIENT,
        SELECTION_RECUE,
        VALIDE_FINAL,
        CONFIRME_CLIENT,
        ANNULE
    }

    public ProForma() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DemandeIntervention getDemande() { return demande; }
    public void setDemande(DemandeIntervention d) { this.demande = d; }

    public String getReference() { return reference; }
    public void setReference(String r) { this.reference = r; }

    public StatutProForma getStatut() { return statut; }
    public void setStatut(StatutProForma s) { this.statut = s; }

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

    public User getChefTechnicien() { return chefTechnicien; }
    public void setChefTechnicien(User c) { this.chefTechnicien = c; }

    public User getReceptionniste() { return receptionniste; }
    public void setReceptionniste(User r) { this.receptionniste = r; }

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

    public String getFactureUrl() { return factureUrl; }
    public void setFactureUrl(String f) { this.factureUrl = f; }

    public LocalDateTime getFactureGenereeAt() { return factureGenereeAt; }
    public void setFactureGenereeAt(LocalDateTime f) { this.factureGenereeAt = f; }

    public List<LigneProFormaTravail> getLignesTravaux() { return lignesTravaux; }
    public void setLignesTravaux(List<LigneProFormaTravail> l) { this.lignesTravaux = l; }

    public List<LigneProFormaMainOeuvre> getLignesMainOeuvre() { return lignesMainOeuvre; }
    public void setLignesMainOeuvre(List<LigneProFormaMainOeuvre> l) { this.lignesMainOeuvre = l; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String c) { this.couponCode = c; }

    public BigDecimal getMontantRemise() { return montantRemise; }
    public void setMontantRemise(BigDecimal m) { this.montantRemise = m; }

    /** Total effectivement payé par le client après remise. */
    public BigDecimal getTotalApresRemise() {
        if (montantRemise == null || montantRemise.compareTo(BigDecimal.ZERO) == 0)
            return totalGeneral;
        return totalGeneral.subtract(montantRemise).max(BigDecimal.ZERO);
    }
}
