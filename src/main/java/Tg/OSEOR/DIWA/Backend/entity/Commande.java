package Tg.OSEOR.DIWA.Backend.entity;

import java.time.LocalDateTime;

import Tg.OSEOR.DIWA.Backend.entity.enums.StatutCommande;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Commande extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateCommande;
    
    @Enumerated(EnumType.STRING)
    private StatutCommande statut;
    
    private Double prixTotalTTC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<LigneDeCommande> lignes = new java.util.ArrayList<>();

    public Commande() {}

    public java.util.List<LigneDeCommande> getLignes() { return lignes; }
    public void setLignes(java.util.List<LigneDeCommande> lignes) { this.lignes = lignes; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }

    public StatutCommande getStatut() { return statut; }
    public void setStatut(StatutCommande statut) { this.statut = statut; }

    public Double getPrixTotalTTC() { return prixTotalTTC; }
    public void setPrixTotalTTC(Double prixTotalTTC) { this.prixTotalTTC = prixTotalTTC; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
