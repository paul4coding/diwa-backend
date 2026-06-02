package Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO;

import java.time.LocalDateTime;
import java.util.List;

import Tg.OSEOR.DIWA.Backend.entity.enums.MarqueEnum;

public class VehiculeDTOResponse {
    private Long id;
    private String uuid;
    private MarqueEnum marque;
    private String modele;
    private Integer annee;
    private Double prixBase;
    private Integer stock;
    private String fichierGlb;
    private String dossier360;
    private String imagePrincipale;
    private String ficheTechnique;
    private List<VehiculeDTORequest.GalleryImageDTO> imagesGalerie;
    private List<String> couleursDispo;
    private String description;
    private Boolean actif;
    
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public VehiculeDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

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

    public String getImagePrincipale() { return imagePrincipale; }
    public void setImagePrincipale(String imagePrincipale) { this.imagePrincipale = imagePrincipale; }

    public String getDossier360() { return dossier360; }
    public void setDossier360(String dossier360) { this.dossier360 = dossier360; }

    public String getFicheTechnique() { return ficheTechnique; }
    public void setFicheTechnique(String ficheTechnique) { this.ficheTechnique = ficheTechnique; }

    public List<VehiculeDTORequest.GalleryImageDTO> getImagesGalerie() { return imagesGalerie; }
    public void setImagesGalerie(List<VehiculeDTORequest.GalleryImageDTO> imagesGalerie) { this.imagesGalerie = imagesGalerie; }

    public List<String> getCouleursDispo() { return couleursDispo; }
    public void setCouleursDispo(List<String> couleursDispo) { this.couleursDispo = couleursDispo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
}
