package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_pf_travaux")
public class LigneProFormaTravail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proforma_id", nullable = false)
    private ProForma proForma;

    @Column(nullable = false)
    private Integer position;

    // ── Rempli par Chef Technicien (SANS prix) ────────────────
    @Column(nullable = false, length = 250)
    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piece_id")
    private PieceDetachee pieceDetachee;

    @Column(length = 100)
    private String referencePieceLibre;

    @Column(length = 100)
    private String marquePiece;

    @Column(length = 100)
    private String modelePiece;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantite;

    // ── Rempli par Réceptionniste ─────────────────────────────
    @Column(precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(precision = 12, scale = 2)
    private BigDecimal prixTotal;

    // Fourchette conseillée
    @Column(precision = 12, scale = 2)
    private BigDecimal prixMinConseille;

    @Column(precision = 12, scale = 2)
    private BigDecimal prixMaxConseille;

    // ── Décision du client ────────────────────────────────────
    @Column(nullable = false)
    private Boolean cocheeParClient = true;

    // ── Statut d'avancement ───────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatutLigne statut = StatutLigne.NON_DEBUTE;

    public enum StatutLigne { NON_DEBUTE, EN_COURS, TERMINE }

    public LigneProFormaTravail() {}

    @PrePersist @PreUpdate
    public void calculerTotal() {
        if (prixUnitaire != null && quantite != null) {
            this.prixTotal = prixUnitaire.multiply(quantite);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProForma getProForma() { return proForma; }
    public void setProForma(ProForma p) { this.proForma = p; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer p) { this.position = p; }

    public String getDesignation() { return designation; }
    public void setDesignation(String d) { this.designation = d; }

    public PieceDetachee getPieceDetachee() { return pieceDetachee; }
    public void setPieceDetachee(PieceDetachee p) { this.pieceDetachee = p; }

    public String getReferencePieceLibre() { return referencePieceLibre; }
    public void setReferencePieceLibre(String r) { this.referencePieceLibre = r; }

    public String getMarquePiece() { return marquePiece; }
    public void setMarquePiece(String m) { this.marquePiece = m; }

    public String getModelePiece() { return modelePiece; }
    public void setModelePiece(String m) { this.modelePiece = m; }

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

    public StatutLigne getStatut() { return statut; }
    public void setStatut(StatutLigne s) { this.statut = s; }
}
