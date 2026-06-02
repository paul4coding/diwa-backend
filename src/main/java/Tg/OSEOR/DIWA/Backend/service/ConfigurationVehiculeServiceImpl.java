package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.ConfigurationVehiculeDTO.ConfigurationVehiculeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.ConfigurationVehiculeDTO.ConfigurationVehiculeDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.*;
import Tg.OSEOR.DIWA.Backend.mappers.ConfigurationVehiculeMapper;
import Tg.OSEOR.DIWA.Backend.repository.*;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;

@Service
@Transactional
public class ConfigurationVehiculeServiceImpl implements ConfigurationVehiculeService {

    private final ConfigurationVehiculeRepository repo;
    private final UserRepository userRepo;
    private final VehiculeRepository vehiculeRepo;
    private final FinitionRepository finitionRepo;
    private final MotorisationRepository motorisationRepo;
    private final OptionVehiculeRepository optionRepo;
    private final ConfigurationVehiculeMapper mapper;

    public ConfigurationVehiculeServiceImpl(
            ConfigurationVehiculeRepository repo, UserRepository userRepo,
            VehiculeRepository vehiculeRepo, FinitionRepository finitionRepo,
            MotorisationRepository motorisationRepo, OptionVehiculeRepository optionRepo,
            ConfigurationVehiculeMapper mapper) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.vehiculeRepo = vehiculeRepo;
        this.finitionRepo = finitionRepo;
        this.motorisationRepo = motorisationRepo;
        this.optionRepo = optionRepo;
        this.mapper = mapper;
    }

    @Override
    public ConfigurationVehiculeDTOResponse create(ConfigurationVehiculeDTORequest request, String userEmail) {
        User user = userRepo.findByIdentifier(userEmail)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Vehicule vehicule = vehiculeRepo.findById(request.getVehiculeId())
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

        ConfigurationVehicule config = mapper.toEntity(request);
        config.setUser(user);
        config.setVehicule(vehicule);

        Double prixTotal = vehicule.getPrixBase();

        // Récupérer et lier la finition si spécifiée
        if (request.getFinitionId() != null) {
            Finition finition = finitionRepo.findById(request.getFinitionId())
                .orElseThrow(() -> new RuntimeException("Finition non trouvée"));
            config.setFinition(finition);
            prixTotal += finition.getPrixSupplement();
        }

        // Récupérer et lier la motorisation si spécifiée
        if (request.getMotorisationId() != null) {
            Motorisation moteur = motorisationRepo.findById(request.getMotorisationId())
                .orElseThrow(() -> new RuntimeException("Motorisation non trouvée"));
            config.setMotorisation(moteur);
            prixTotal += moteur.getPrix();
        }

        // Récupérer et lier les options
        List<OptionVehicule> optionsChoisies = new ArrayList<>();
        if (request.getOptionsIds() != null && !request.getOptionsIds().isEmpty()) {
            for (Long optId : request.getOptionsIds()) {
                OptionVehicule opt = optionRepo.findById(optId)
                    .orElseThrow(() -> new RuntimeException("Option non trouvée: " + optId));
                optionsChoisies.add(opt);
                prixTotal += opt.getPrixSupplement();
            }
        }
        config.setOptionsChoisies(optionsChoisies);
        
        // Sécurité critique : recalcul du prix final côté serveur
        config.setPrixTotal(prixTotal);

        return mapper.toResponse(repo.save(config));
    }

    @Override
    @Transactional(readOnly = true)
    public ConfigurationVehiculeDTOResponse getById(Long id) {
        ConfigurationVehicule config = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Configuration non trouvée : " + id));
            
        // Forcer le lazy loading
        if(config.getOptionsChoisies() != null) config.getOptionsChoisies().size();
        
        return mapper.toResponse(config);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationVehiculeDTOResponse> getMyConfigurations(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) return new ArrayList<>();
        
        java.util.Optional<User> userOpt = userRepo.findByIdentifier(userEmail);
        if (userOpt.isEmpty()) return new ArrayList<>();
            
        return repo.findByUser(userOpt.get()).stream()
            .peek(c -> {
                if(c.getOptionsChoisies() != null) c.getOptionsChoisies().size();
            })
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id, String userEmail) {
        ConfigurationVehicule config = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Configuration non trouvée : " + id));
            
        User user = userRepo.findByIdentifier(userEmail)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
        if (!config.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Action non autorisée");
        }
        
        repo.delete(config);
    }
}
