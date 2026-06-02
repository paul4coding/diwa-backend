package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "lignes_pf_main_oeuvre")
public class LigneProFormaMainOeuvre extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proforma_id", nullable = false)
    private ProForma proForma;

    @Column(nullable = false)
    private Integer position;

    // ── Rempli par Chef Technicien ────────────────────────────
    @Column(nullable = false, length = 250)
    private String typeIntervention;

    /** Durée en minutes (source de vérité — heures est dérivé). */
    @Column
    private Integer dureeMinutes;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal heures;

    @Column(columnDefinition = "TEXT")
    private String commentaireTechnicien;

    /**
     * Référence vers la LigneProFormaTravail (pièce) à laquelle cette MO est liée.
     * Null si la MO est autonome (pas associée à une pièce précise).
     */
    @Column(name = "ligne_travail_id")
    private Long ligneTravailId;

    // ── Rempli par Réceptionniste ─────────────────────────────
    @Column(precision = 12, scale = 2)
    private BigDecimal tauxHoraire;

    @Column(precision = 12, scale = 2)
    private BigDecimal total;

    // ── Décision du client ────────────────────────────────────
    @Column(nullable = false)
    private Boolean cocheeParClient = true;

    public LigneProFormaMainOeuvre() {}

    @PrePersist @PreUpdate
    public void calculerTotal() {
        // Dériver heures depuis dureeMinutes si renseigné
        if (dureeMinutes != null && dureeMinutes > 0) {
            this.heures = new BigDecimal(dureeMinutes)
                    .divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP);
        }
        if (heures == null) this.heures = BigDecimal.ZERO;
        if (tauxHoraire != null && heures != null) {
            this.total = tauxHoraire.multiply(heures);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProForma getProForma() { return proForma; }
    public void setProForma(ProForma p) { this.proForma = p; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer p) { this.position = p; }

    public String getTypeIntervention() { return typeIntervention; }
    public void setTypeIntervention(String t) { this.typeIntervention = t; }

    public BigDecimal getHeures() { return heures; }
    public void setHeures(BigDecimal h) { this.heures = h; }

    public String getCommentaireTechnicien() { return commentaireTechnicien; }
    public void setCommentaireTechnicien(String c) { this.commentaireTechnicien = c; }

    public BigDecimal getTauxHoraire() { return tauxHoraire; }
    public void setTauxHoraire(BigDecimal t) { this.tauxHoraire = t; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal t) { this.total = t; }

    public Boolean getCocheeParClient() { return cocheeParClient; }
    public void setCocheeParClient(Boolean c) { this.cocheeParClient = c; }

    public Integer getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(Integer d) { this.dureeMinutes = d; }

    public Long getLigneTravailId() { return ligneTravailId; }
    public void setLigneTravailId(Long l) { this.ligneTravailId = l; }
}
