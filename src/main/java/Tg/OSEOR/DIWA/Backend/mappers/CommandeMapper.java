package Tg.OSEOR.DIWA.Backend.mappers;

import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.CommandeDTO.*;
import Tg.OSEOR.DIWA.Backend.entity.*;
import Tg.OSEOR.DIWA.Backend.entity.enums.TypeLigneCommande;
import Tg.OSEOR.DIWA.Backend.repository.ConfigurationVehiculeRepository;
import Tg.OSEOR.DIWA.Backend.repository.PieceDetacheeRepository;

@Component
public class CommandeMapper {

    private final ConfigurationVehiculeRepository configRepo;
    private final PieceDetacheeRepository pieceRepo;

    public CommandeMapper(ConfigurationVehiculeRepository configRepo, PieceDetacheeRepository pieceRepo) {
        this.configRepo = configRepo;
        this.pieceRepo = pieceRepo;
    }

    public CommandeDTOResponse toResponse(Commande entity, List<LigneDeCommande> lignes) {
        if (entity == null) return null;
        
        CommandeDTOResponse dto = new CommandeDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setDateCommande(entity.getDateCommande());
        
        if (entity.getStatut() != null) {
            dto.setStatut(entity.getStatut().name());
        }
        
        dto.setPrixTotalTTC(entity.getPrixTotalTTC());
        
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }
        
        if (lignes != null) {
            dto.setLignes(lignes.stream().map(this::toLigneResponse).collect(Collectors.toList()));
        }
        
        dto.setCreateDate(entity.getCreateDate());
        dto.setUpdateDate(entity.getUpdateDate());
        
        return dto;
    }

    public LigneDeCommandeDTOResponse toLigneResponse(LigneDeCommande entity) {
        if (entity == null) return null;
        
        LigneDeCommandeDTOResponse dto = new LigneDeCommandeDTOResponse();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        
        if (entity.getType() != null) {
            dto.setType(entity.getType().name());
        }
        
        dto.setQuantite(entity.getQuantite());
        dto.setReferenceId(entity.getReferenceId());
        
        // Résolution dynamique du libellé et du prix pour l'affichage
        if (entity.getType() == TypeLigneCommande.CONFIG) {
            configRepo.findById(entity.getReferenceId()).ifPresent(config -> {
                dto.setLibelleArticle("Configuration: " + config.getNomConfig());
                dto.setPrixUnitaireCalcule(config.getPrixTotal());
                dto.setSousTotal(config.getPrixTotal() * entity.getQuantite());
            });
        } else if (entity.getType() == TypeLigneCommande.PIECE) {
            pieceRepo.findById(entity.getReferenceId()).ifPresent(piece -> {
                dto.setLibelleArticle("Pièce: " + piece.getNom() + " (Ref: " + piece.getReference() + ")");
                dto.setPrixUnitaireCalcule(piece.getPrixUnitaire());
                dto.setSousTotal(piece.getPrixUnitaire() * entity.getQuantite());
            });
        }
        
        return dto;
    }

    public LigneDeCommande toLigneEntity(LigneDeCommandeDTORequest request) {
        if (request == null) return null;
        
        LigneDeCommande entity = new LigneDeCommande();
        entity.setType(request.getType());
        entity.setQuantite(request.getQuantite());
        entity.setReferenceId(request.getReferenceId());
        
        return entity;
    }
}
