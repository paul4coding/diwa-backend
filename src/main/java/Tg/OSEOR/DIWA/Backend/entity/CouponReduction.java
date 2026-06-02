package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Coupon de réduction applicable sur un Pro Forma.
 * Créé par la réceptionniste, utilisé par le client.
 */
@Entity
@Table(name = "coupons_reduction", indexes = {
    @Index(name = "idx_coupon_code", columnList = "code", unique = true)
})
public class CouponReduction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Code alphanumérique saisi par le client (ex: DIWA10). Toujours stocké en majuscules. */
    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(length = 200)
    private String description;

    /** Montant de la remise (soit un % soit un montant fixe en FCFA). */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valeur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeRemise typeRemise;

    /** Nombre total d'utilisations autorisées. */
    @Column(nullable = false)
    private Integer nbMaxUtilisations = 1;

    /** Compteur d'utilisations réelles (incrémenté lors de chaque application). */
    @Column(nullable = false)
    private Integer nbUtilisations = 0;

    @Column
    private LocalDateTime dateExpiration;

    @Column(nullable = false)
    private Boolean actif = true;

    /**
     * Si renseigné, le coupon est valable uniquement pour ce Pro Forma.
     * Null = coupon global (tout pro-forma éligible).
     */
    @Column(name = "proforma_id")
    private Long proFormaId;

    /** Réceptionniste qui a créé le coupon. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id")
    private User createur;

    public enum TypeRemise {
        POURCENTAGE,
        MONTANT_FIXE
    }

    public CouponReduction() {}

    // ── Getters / Setters ─────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code != null ? code.toUpperCase().trim() : null; }

    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }

    public BigDecimal getValeur() { return valeur; }
    public void setValeur(BigDecimal v) { this.valeur = v; }

    public TypeRemise getTypeRemise() { return typeRemise; }
    public void setTypeRemise(TypeRemise t) { this.typeRemise = t; }

    public Integer getNbMaxUtilisations() { return nbMaxUtilisations; }
    public void setNbMaxUtilisations(Integer n) { this.nbMaxUtilisations = n; }

    public Integer getNbUtilisations() { return nbUtilisations; }
    public void setNbUtilisations(Integer n) { this.nbUtilisations = n; }

    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDateTime d) { this.dateExpiration = d; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean a) { this.actif = a; }

    public Long getProFormaId() { return proFormaId; }
    public void setProFormaId(Long p) { this.proFormaId = p; }

    public User getCreateur() { return createur; }
    public void setCreateur(User c) { this.createur = c; }

    // ── Méthodes utilitaires ──────────────────────────────────

    public boolean estEpuise() {
        return nbUtilisations >= nbMaxUtilisations;
    }

    public boolean estExpire() {
        return dateExpiration != null && dateExpiration.isBefore(LocalDateTime.now());
    }

    public boolean estValide() {
        return Boolean.TRUE.equals(actif) && !estEpuise() && !estExpire();
    }
}
