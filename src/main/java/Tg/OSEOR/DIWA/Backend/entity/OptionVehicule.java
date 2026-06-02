package Tg.OSEOR.DIWA.Backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import Tg.OSEOR.DIWA.Backend.entity.enums.TypeOption;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
public class OptionVehicule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Enumerated(EnumType.STRING)
    private TypeOption type;

    private Double prixSupplement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id")
    @JsonIgnore
    private Vehicule vehicule;

    public OptionVehicule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public TypeOption getType() { return type; }
    public void setType(TypeOption type) { this.type = type; }

    public Double getPrixSupplement() { return prixSupplement; }
    public void setPrixSupplement(Double prixSupplement) { this.prixSupplement = prixSupplement; }

    public Vehicule getVehicule() { return vehicule; }
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }
}
