package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.entity.enums.TypeLigneCommande;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
public class LigneDeCommande extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeLigneCommande type;
    
    private Integer quantite;
    
    private Long referenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id")
    private Commande commande;

    public LigneDeCommande() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TypeLigneCommande getType() { return type; }
    public void setType(TypeLigneCommande type) { this.type = type; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }
}
