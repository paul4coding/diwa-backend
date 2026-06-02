package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "parametres_atelier")
public class ParametreAtelier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Seuil km pour diagnostic gratuit
    @Column(nullable = false)
    private Integer seuilKmDiagnosticGratuit = 6000;

    // Tarif kilométrique pour récupération (FCFA par km)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifKmRecuperation = new BigDecimal("1500");

    // Tarif kilométrique pour livraison (FCFA par km)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifKmLivraison = new BigDecimal("1500");

    // Taux horaire main d'œuvre par défaut
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tauxHoraireDefaut = new BigDecimal("15000");

    // Frais de diagnostic standard
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fraisDiagnosticStandard = new BigDecimal("8000");

    public ParametreAtelier() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getSeuilKmDiagnosticGratuit() { return seuilKmDiagnosticGratuit; }
    public void setSeuilKmDiagnosticGratuit(Integer s) { this.seuilKmDiagnosticGratuit = s; }

    public BigDecimal getTarifKmRecuperation() { return tarifKmRecuperation; }
    public void setTarifKmRecuperation(BigDecimal t) { this.tarifKmRecuperation = t; }

    public BigDecimal getTarifKmLivraison() { return tarifKmLivraison; }
    public void setTarifKmLivraison(BigDecimal t) { this.tarifKmLivraison = t; }

    public BigDecimal getTauxHoraireDefaut() { return tauxHoraireDefaut; }
    public void setTauxHoraireDefaut(BigDecimal t) { this.tauxHoraireDefaut = t; }

    public BigDecimal getFraisDiagnosticStandard() { return fraisDiagnosticStandard; }
    public void setFraisDiagnosticStandard(BigDecimal f) { this.fraisDiagnosticStandard = f; }
}
