package Tg.OSEOR.DIWA.Backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.CommandeDTO.CommandeDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.CommandeDTO.LigneDeCommandeDTORequest;
import Tg.OSEOR.DIWA.Backend.entity.*;
import Tg.OSEOR.DIWA.Backend.entity.enums.StatutCommande;
import Tg.OSEOR.DIWA.Backend.entity.enums.TypeLigneCommande;
import Tg.OSEOR.DIWA.Backend.mappers.CommandeMapper;
import Tg.OSEOR.DIWA.Backend.repository.*;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;

@Service
@Transactional
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepo;
    private final LigneDeCommandeRepository ligneRepo;
    private final UserRepository userRepo;
    private final ConfigurationVehiculeRepository configRepo;
    private final PieceDetacheeRepository pieceRepo;
    private final CommandeMapper mapper;

    public CommandeServiceImpl(
            CommandeRepository commandeRepo, LigneDeCommandeRepository ligneRepo,
            UserRepository userRepo, ConfigurationVehiculeRepository configRepo,
            PieceDetacheeRepository pieceRepo, CommandeMapper mapper) {
        this.commandeRepo = commandeRepo;
        this.ligneRepo = ligneRepo;
        this.userRepo = userRepo;
        this.configRepo = configRepo;
        this.pieceRepo = pieceRepo;
        this.mapper = mapper;
    }

    private Commande findOrCreateActiveCart(User user) {
        return commandeRepo.findAll().stream()
            .filter(c -> c.getUser() != null && c.getUser().getId().equals(user.getId()) 
                      && c.getStatut() == StatutCommande.PANIER)
            .findFirst()
            .orElseGet(() -> {
                Commande newCart = new Commande();
                newCart.setUser(user);
                newCart.setStatut(StatutCommande.PANIER);
                newCart.setDateCommande(LocalDateTime.now());
                newCart.setPrixTotalTTC(0.0);
                return commandeRepo.save(newCart);
            });
    }
    
    private void recalculateCartTotal(Commande cart) {
        List<LigneDeCommande> lignes = ligneRepo.findAll().stream()
            .filter(l -> l.getCommande() != null && l.getCommande().getId().equals(cart.getId()))
            .collect(Collectors.toList());
            
        double total = 0.0;
        for(LigneDeCommande ligne : lignes) {
            if(ligne.getType() == TypeLigneCommande.CONFIG) {
                ConfigurationVehicule conf = configRepo.findById(ligne.getReferenceId()).orElse(null);
                if(conf != null && conf.getPrixTotal() != null) {
                    total += conf.getPrixTotal() * ligne.getQuantite();
                }
            } else if(ligne.getType() == TypeLigneCommande.PIECE) {
                PieceDetachee piece = pieceRepo.findById(ligne.getReferenceId()).orElse(null);
                if(piece != null && piece.getPrixUnitaire() != null) {
                    total += piece.getPrixUnitaire() * ligne.getQuantite();
                }
            }
        }
        
        cart.setPrixTotalTTC(total);
        commandeRepo.save(cart);
    }

    @Override
    public CommandeDTOResponse getMyCart(String userEmail) {
        User user = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
        Commande cart = findOrCreateActiveCart(user);
        List<LigneDeCommande> lignes = ligneRepo.findAll().stream()
            .filter(l -> l.getCommande() != null && l.getCommande().getId().equals(cart.getId()))
            .collect(Collectors.toList());
            
        return mapper.toResponse(cart, lignes);
    }

    @Override
    public CommandeDTOResponse addToCart(String userEmail, LigneDeCommandeDTORequest request) {
        User user = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
        Commande cart = findOrCreateActiveCart(user);
        
        // Vérification de la référence
        if (request.getType() == TypeLigneCommande.CONFIG) {
            configRepo.findById(request.getReferenceId())
                .orElseThrow(() -> new RuntimeException("Configuration introuvable"));
        } else {
            pieceRepo.findById(request.getReferenceId())
                .orElseThrow(() -> new RuntimeException("Pièce introuvable"));
        }
        
        LigneDeCommande ligne = mapper.toLigneEntity(request);
        ligne.setCommande(cart);
        ligneRepo.save(ligne);
        
        recalculateCartTotal(cart);
        
        List<LigneDeCommande> lignes = ligneRepo.findAll().stream()
            .filter(l -> l.getCommande() != null && l.getCommande().getId().equals(cart.getId()))
            .collect(Collectors.toList());
            
        return mapper.toResponse(cart, lignes);
    }

    @Override
    public CommandeDTOResponse removeFromCart(String userEmail, Long ligneId) {
        User user = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
        Commande cart = findOrCreateActiveCart(user);
        
        LigneDeCommande ligne = ligneRepo.findById(ligneId)
            .orElseThrow(() -> new RuntimeException("Ligne non trouvée"));
            
        if (!ligne.getCommande().getId().equals(cart.getId())) {
            throw new RuntimeException("Cette ligne n'appartient pas à votre panier actif");
        }
        
        ligneRepo.delete(ligne);
        recalculateCartTotal(cart);
        
        List<LigneDeCommande> lignes = ligneRepo.findAll().stream()
            .filter(l -> l.getCommande() != null && l.getCommande().getId().equals(cart.getId()) && !l.getId().equals(ligneId))
            .collect(Collectors.toList());
            
        return mapper.toResponse(cart, lignes);
    }

    @Override
    public CommandeDTOResponse checkout(String userEmail) {
        User user = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
        Commande cart = findOrCreateActiveCart(user);
        List<LigneDeCommande> lignes = ligneRepo.findAll().stream()
            .filter(l -> l.getCommande() != null && l.getCommande().getId().equals(cart.getId()))
            .collect(Collectors.toList());
            
        if (lignes.isEmpty()) {
            throw new RuntimeException("Le panier est vide");
        }
        
        recalculateCartTotal(cart);
        cart.setStatut(StatutCommande.PAYE); // Validation de la commande (checkout -> Payé ou En préparation selon le flux)
        cart.setDateCommande(LocalDateTime.now());
        commandeRepo.save(cart);
        
        return mapper.toResponse(cart, lignes);
    }

    @Override
    public List<CommandeDTOResponse> getMyOrdersHistory(String userEmail) {
        User user = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
        return commandeRepo.findAll().stream()
            .filter(c -> c.getUser() != null && c.getUser().getId().equals(user.getId()) 
                      && c.getStatut() != StatutCommande.PANIER)
            .map(c -> {
                List<LigneDeCommande> lignes = ligneRepo.findAll().stream()
                    .filter(l -> l.getCommande() != null && l.getCommande().getId().equals(c.getId()))
                    .collect(Collectors.toList());
                return mapper.toResponse(c, lignes);
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<CommandeDTOResponse> listAll() {
        return commandeRepo.findAllWithEverything().stream()
            .map(c -> mapper.toResponse(c, new java.util.ArrayList<>(c.getLignes())))
            .collect(Collectors.toList());
    }

    @Override
    public CommandeDTOResponse updateStatus(Long id, String status) {
        Commande commande = commandeRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
            
        commande.setStatut(StatutCommande.valueOf(status.toUpperCase()));
        commandeRepo.save(commande);
        
        List<LigneDeCommande> lignes = ligneRepo.findAll().stream()
            .filter(l -> l.getCommande() != null && l.getCommande().getId().equals(commande.getId()))
            .collect(Collectors.toList());
            
        return mapper.toResponse(commande, lignes);
    }
}
