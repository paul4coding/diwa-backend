package Tg.OSEOR.DIWA.Backend.dto.CommandeDTO;

import java.time.LocalDateTime;
import java.util.List;

public class CommandeDTOResponse {
    private Long id;
    private String uuid;
    private LocalDateTime dateCommande;
    private String statut;
    private Double prixTotalTTC;
    private Long userId;
    
    private List<LigneDeCommandeDTOResponse> lignes;
    
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public CommandeDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Double getPrixTotalTTC() { return prixTotalTTC; }
    public void setPrixTotalTTC(Double prixTotalTTC) { this.prixTotalTTC = prixTotalTTC; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<LigneDeCommandeDTOResponse> getLignes() { return lignes; }
    public void setLignes(List<LigneDeCommandeDTOResponse> lignes) { this.lignes = lignes; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
}
