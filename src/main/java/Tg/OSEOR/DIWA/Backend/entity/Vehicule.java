package Tg.OSEOR.DIWA.Backend.entity;

import java.util.ArrayList;
import java.util.List;

import Tg.OSEOR.DIWA.Backend.entity.enums.MarqueEnum;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Vehicule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarqueEnum marque;

    private String modele;
    private Integer annee;
    private Double prixBase;
    private Integer stock = 0;
    private String fichierGlb;
    private String dossier360;
    private String imagePrincipale;
    private String ficheTechnique;

    // Champs pour les véhicules clients (DIWA)
    private String immatriculation;
    private Integer kilometrageAchat;

    @ElementCollection
    @CollectionTable(name = "vehicule_couleurs", joinColumns = @JoinColumn(name = "vehicule_id"))
    @Column(name = "couleur")
    private List<String> couleursDispo = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean actif = true;

    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Finition> finitions = new ArrayList<>();

    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Motorisation> motorisations = new ArrayList<>();

    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionVehicule> options = new ArrayList<>();
    
    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssetVisuel> imagesGalerie = new ArrayList<>();

    public Vehicule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MarqueEnum getMarque() { return marque; }
    public void setMarque(MarqueEnum marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public Double getPrixBase() { return prixBase; }
    public void setPrixBase(Double prixBase) { this.prixBase = prixBase; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getFichierGlb() { return fichierGlb; }
    public void setFichierGlb(String fichierGlb) { this.fichierGlb = fichierGlb; }

    public String getDossier360() { return dossier360; }
    public void setDossier360(String dossier360) { this.dossier360 = dossier360; }

    public String getImagePrincipale() { return imagePrincipale; }
    public void setImagePrincipale(String imagePrincipale) { this.imagePrincipale = imagePrincipale; }

    public String getFicheTechnique() { return ficheTechnique; }
    public void setFicheTechnique(String ficheTechnique) { this.ficheTechnique = ficheTechnique; }

    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }

    public Integer getKilometrageAchat() { return kilometrageAchat; }
    public void setKilometrageAchat(Integer kilometrageAchat) { this.kilometrageAchat = kilometrageAchat; }

    public List<String> getCouleursDispo() { return couleursDispo; }
    public void setCouleursDispo(List<String> couleursDispo) { this.couleursDispo = couleursDispo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public List<Finition> getFinitions() { return finitions; }
    public void setFinitions(List<Finition> finitions) { this.finitions = finitions; }

    public List<Motorisation> getMotorisations() { return motorisations; }
    public void setMotorisations(List<Motorisation> motorisations) { this.motorisations = motorisations; }

    public List<OptionVehicule> getOptions() { return options; }
    public void setOptions(List<OptionVehicule> options) { this.options = options; }

    public List<AssetVisuel> getImagesGalerie() { return imagesGalerie; }
    public void setImagesGalerie(List<AssetVisuel> imagesGalerie) { this.imagesGalerie = imagesGalerie; }
}
