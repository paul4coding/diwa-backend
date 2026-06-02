package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "gamme_prix")
public class GammePrix extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 250)
    private String designation;

    @Column(nullable = false, length = 50)
    private String categorie;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prixMin;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prixMax;

    @Column(columnDefinition = "TEXT")
    private String description;

    public GammePrix() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDesignation() { return designation; }
    public void setDesignation(String d) { this.designation = d; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String c) { this.categorie = c; }

    public BigDecimal getPrixMin() { return prixMin; }
    public void setPrixMin(BigDecimal p) { this.prixMin = p; }

    public BigDecimal getPrixMax() { return prixMax; }
    public void setPrixMax(BigDecimal p) { this.prixMax = p; }

    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
}
