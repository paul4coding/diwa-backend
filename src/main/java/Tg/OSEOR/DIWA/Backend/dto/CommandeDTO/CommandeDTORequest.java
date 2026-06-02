package Tg.OSEOR.DIWA.Backend.dto.CommandeDTO;

import java.util.List;

public class CommandeDTORequest {
    // Une commande peut être créée directement avec plusieurs lignes, ou vide (panier)
    private List<LigneDeCommandeDTORequest> lignes;

    public CommandeDTORequest() {}

    public List<LigneDeCommandeDTORequest> getLignes() { return lignes; }
    public void setLignes(List<LigneDeCommandeDTORequest> lignes) { this.lignes = lignes; }
}
