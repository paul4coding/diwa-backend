package Tg.OSEOR.DIWA.Backend.dto.CommandeDTO;

public class LigneDeCommandeDTOResponse {
    private Long id;
    private String uuid;
    private String type;
    private Integer quantite;
    private Long referenceId;
    
    // Pour l'affichage frontend dynamique sans faire d'autres requêtes
    private String libelleArticle; 
    private Double prixUnitaireCalcule;
    private Double sousTotal;
    
    public LigneDeCommandeDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public String getLibelleArticle() { return libelleArticle; }
    public void setLibelleArticle(String libelleArticle) { this.libelleArticle = libelleArticle; }

    public Double getPrixUnitaireCalcule() { return prixUnitaireCalcule; }
    public void setPrixUnitaireCalcule(Double prixUnitaireCalcule) { this.prixUnitaireCalcule = prixUnitaireCalcule; }

    public Double getSousTotal() { return sousTotal; }
    public void setSousTotal(Double sousTotal) { this.sousTotal = sousTotal; }
}
