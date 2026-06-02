package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.CommandeDTO.CommandeDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.CommandeDTO.LigneDeCommandeDTORequest;

public interface CommandeService {
    CommandeDTOResponse getMyCart(String userEmail); // Récupérer le panier EN_ATTENTE
    CommandeDTOResponse addToCart(String userEmail, LigneDeCommandeDTORequest request);
    CommandeDTOResponse removeFromCart(String userEmail, Long ligneId);
    CommandeDTOResponse checkout(String userEmail); // Valide le panier en commande CONFIRME
    List<CommandeDTOResponse> getMyOrdersHistory(String userEmail); // Historique des achats
    List<CommandeDTOResponse> listAll(); // Admin
    CommandeDTOResponse updateStatus(Long id, String status); // Admin
}
