package Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO;

import java.util.List;

public class SelectionClientRequest {
    private List<SelectionLigneDTO> lignesTravaux;
    private List<SelectionLigneDTO> lignesMainOeuvre;
    private Boolean demandeLivraison;
    private String adresseLivraison;

    public List<SelectionLigneDTO> getLignesTravaux() { return lignesTravaux; }
    public void setLignesTravaux(List<SelectionLigneDTO> l) { this.lignesTravaux = l; }
    public List<SelectionLigneDTO> getLignesMainOeuvre() { return lignesMainOeuvre; }
    public void setLignesMainOeuvre(List<SelectionLigneDTO> l) { this.lignesMainOeuvre = l; }
    public Boolean getDemandeLivraison() { return demandeLivraison; }
    public void setDemandeLivraison(Boolean d) { this.demandeLivraison = d; }
    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String a) { this.adresseLivraison = a; }

    public static class SelectionLigneDTO {
        private Long ligneId;
        private Boolean cochee;

        public Long getLigneId() { return ligneId; }
        public void setLigneId(Long l) { this.ligneId = l; }
        public Boolean getCochee() { return cochee; }
        public void setCochee(Boolean c) { this.cochee = c; }
    }
}
