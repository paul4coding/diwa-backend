package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.entity.enums.TypeMoteur;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Motorisation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeMoteur type;

    private Integer puissance;
    private Integer couple;
    private String moteur;
    private Double prix;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id")
    @JsonIgnore
    private Vehicule vehicule;

    public Motorisation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TypeMoteur getType() { return type; }
    public void setType(TypeMoteur type) { this.type = type; }

    public Integer getPuissance() { return puissance; }
    public void setPuissance(Integer puissance) { this.puissance = puissance; }

    public Integer getCouple() { return couple; }
    public void setCouple(Integer couple) { this.couple = couple; }

    public String getMoteur() { return moteur; }
    public void setMoteur(String moteur) { this.moteur = moteur; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public Vehicule getVehicule() { return vehicule; }
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }
}
