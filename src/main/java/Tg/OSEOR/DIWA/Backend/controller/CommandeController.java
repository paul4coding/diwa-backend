package Tg.OSEOR.DIWA.Backend.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.CommandeDTO.CommandeDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.CommandeDTO.LigneDeCommandeDTORequest;
import Tg.OSEOR.DIWA.Backend.service.CommandeService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/commande")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @GetMapping("/cart")
    public ResponseEntity<BaseResponse<CommandeDTOResponse>> getMyCart(Principal principal) {
        // En prod, principal.getName() retourne l'email depuis Spring Security JWT Auth
        String email = principal != null ? principal.getName() : "anonymous@diva.com";
        CommandeDTOResponse data = commandeService.getMyCart(email);
        BaseResponse<CommandeDTOResponse> response = new BaseResponse<>(200, 
            "Panier récupéré", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<BaseResponse<CommandeDTOResponse>> addToCart(
        @Valid @RequestBody LigneDeCommandeDTORequest request, 
        Principal principal) {
        
        String email = principal != null ? principal.getName() : "anonymous@diva.com";
        CommandeDTOResponse data = commandeService.addToCart(email, request);
        BaseResponse<CommandeDTOResponse> response = new BaseResponse<>(201, 
            "Article ajouté au panier", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/cart/remove/{ligneId}")
    public ResponseEntity<BaseResponse<CommandeDTOResponse>> removeFromCart(
        @PathVariable Long ligneId, 
        Principal principal) {
        
        String email = principal != null ? principal.getName() : "anonymous@diva.com";
        CommandeDTOResponse data = commandeService.removeFromCart(email, ligneId);
        BaseResponse<CommandeDTOResponse> response = new BaseResponse<>(200, 
            "Article retiré du panier", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<BaseResponse<CommandeDTOResponse>> checkout(Principal principal) {
        
        String email = principal != null ? principal.getName() : "anonymous@diva.com";
        CommandeDTOResponse data = commandeService.checkout(email);
        BaseResponse<CommandeDTOResponse> response = new BaseResponse<>(200, 
            "Commande validée avec succès", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<BaseResponse<List<CommandeDTOResponse>>> getMyOrdersHistory(Principal principal) {
        String email = principal != null ? principal.getName() : "anonymous@diva.com";
        List<CommandeDTOResponse> data = commandeService.getMyOrdersHistory(email);
        BaseResponse<List<CommandeDTOResponse>> response = new BaseResponse<>(200, 
            "Historique des commandes", data);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<CommandeDTOResponse>>> getAllOrders() {
        List<CommandeDTOResponse> data = commandeService.listAll();
        return ResponseEntity.ok(new BaseResponse<>(200, "Toutes les commandes", data));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<CommandeDTOResponse>> updateStatus(
            @PathVariable Long id, @RequestParam String status) {
        CommandeDTOResponse data = commandeService.updateStatus(id, status);
        return ResponseEntity.ok(new BaseResponse<>(200, "Statut mis à jour", data));
    }
}
