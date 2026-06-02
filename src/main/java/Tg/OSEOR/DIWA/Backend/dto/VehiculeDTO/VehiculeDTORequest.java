package Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO;

import java.util.List;

import Tg.OSEOR.DIWA.Backend.entity.enums.MarqueEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VehiculeDTORequest {
    @NotNull(message = "La marque est obligatoire")
    private MarqueEnum marque;
    
    @NotBlank(message = "Le modèle est obligatoire")
    private String modele;
    
    @NotNull(message = "L'année est obligatoire")
    private Integer annee;
    
    @NotNull(message = "Le prix de base est obligatoire")
    private Double prixBase;
    
    @NotNull(message = "Le stock est obligatoire")
    private Integer stock;

    private String fichierGlb;
    private String dossier360;
    private String imagePrincipale;
    private String ficheTechnique;
    private List<GalleryImageDTO> imagesGalerie;
    private List<String> couleursDispo;
    private String description;
    private Boolean actif = true;

    public static class GalleryImageDTO {
        private String url;
        private String vue; // INTERIEUR, EXTERIEUR, etc.
        
        public GalleryImageDTO() {}
        public GalleryImageDTO(String url, String vue) { this.url = url; this.vue = vue; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getVue() { return vue; }
        public void setVue(String vue) { this.vue = vue; }
    }

    public VehiculeDTORequest() {}

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

    public List<GalleryImageDTO> getImagesGalerie() { return imagesGalerie; }
    public void setImagesGalerie(List<GalleryImageDTO> imagesGalerie) { this.imagesGalerie = imagesGalerie; }

    public List<String> getCouleursDispo() { return couleursDispo; }
    public void setCouleursDispo(List<String> couleursDispo) { this.couleursDispo = couleursDispo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
}
