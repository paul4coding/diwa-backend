package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO.RendezVousDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO.RendezVousDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.*;
import Tg.OSEOR.DIWA.Backend.entity.enums.StatutRDV;
import Tg.OSEOR.DIWA.Backend.entity.enums.UrgenceRDV;
import Tg.OSEOR.DIWA.Backend.mappers.RendezVousMapper;
import Tg.OSEOR.DIWA.Backend.repository.*;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;

@Service
@Transactional
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository rdvRepo;
    private final VehiculeRepository vehiculeRepo;
    private final ServiceSAVRepository serviceRepo;
    private final CreneauHoraireRepository creneauRepo;
    private final UserRepository userRepo;
    private final TechnicienRepository techRepo;
    private final RendezVousMapper mapper;
    private final EmailService emailService;

    public RendezVousServiceImpl(RendezVousRepository rdvRepo, 
                                 VehiculeRepository vehiculeRepo, 
                                 ServiceSAVRepository serviceRepo,
                                 CreneauHoraireRepository creneauRepo,
                                 UserRepository userRepo,
                                 TechnicienRepository techRepo,
                                 RendezVousMapper mapper,
                                 @Qualifier("savEmailService") EmailService emailService) {
        this.rdvRepo = rdvRepo;
        this.vehiculeRepo = vehiculeRepo;
        this.serviceRepo = serviceRepo;
        this.creneauRepo = creneauRepo;
        this.userRepo = userRepo;
        this.techRepo = techRepo;
        this.mapper = mapper;
        this.emailService = emailService;
    }
    
    @Override
    public RendezVousDTOResponse demanderRendezVous(RendezVousDTORequest request, Long userId) {
        try {
            User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
                
            Vehicule vehicule = null;
            if (request.getVehiculeId() != null) {
                vehicule = vehiculeRepo.findById(request.getVehiculeId())
                    .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));
            }
                
            ServiceSAV service = serviceRepo.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service non trouvé"));
                
            CreneauHoraire creneau = null;
            if (request.getCreneauId() != null) {
                creneau = creneauRepo.findById(request.getCreneauId())
                    .orElseThrow(() -> new RuntimeException("Créneau horaire non trouvé"));
                    
                if(creneau.getEstLibre() == null || !creneau.getEstLibre()) {
                    throw new RuntimeException("Ce créneau horaire n'est plus disponible");
                }
                
                // Bloquer le créneau temporairement
                creneau.setEstLibre(false);
                creneauRepo.save(creneau);
            }
            
            RendezVous rdv = new RendezVous();
            rdv.setDate(request.getDateRdv() != null ? request.getDateRdv() : (creneau != null ? creneau.getDate() : LocalDate.now()));
            rdv.setStatut(StatutRDV.EN_ATTENTE);
            rdv.setUser(user);
            rdv.setVehicule(vehicule);
            rdv.setService(service);
            rdv.setCreneau(creneau);
            rdv.setTechnicien(creneau != null ? creneau.getTechnicien() : null);
            
            // Nouveaux champs
            rdv.setImmatriculation(request.getImmatriculation());
            rdv.setModeleVehicule(request.getModeleVehicule());
            rdv.setVin(request.getVin());
            rdv.setKilometrage(request.getKilometrage());
            rdv.setDetailsSpecifiques(request.getDetailsSpecifiques());
            rdv.setDescription(request.getDescription());
            
            if (request.getUrgence() != null) {
                try {
                    rdv.setUrgence(UrgenceRDV.valueOf(request.getUrgence().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    rdv.setUrgence(UrgenceRDV.LOW);
                }
            }
            
            RendezVous saved = rdvRepo.save(rdv);
            return mapper.toResponse(saved);
        } catch (Exception e) {
            System.err.println("CRASH DEMANDE RDV: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de la demande SAV: " + e.getMessage());
        }
    }

    @Override
    public RendezVousDTOResponse validerRendezVous(Long id, Long creneauId) {
        RendezVous rdv = rdvRepo.findByIdWithEverything(id)
            .orElseThrow(() -> new RuntimeException("Rendez-vous #" + id + " non trouvé"));
            
        if (creneauId != null) {
            CreneauHoraire creneau = creneauRepo.findById(creneauId)
                .orElseThrow(() -> new RuntimeException("Créneau #" + creneauId + " non trouvé"));
            creneau.setEstLibre(false);
            creneauRepo.save(creneau);
            rdv.setCreneau(creneau);
            rdv.setDate(creneau.getDate());
            rdv.setTechnicien(creneau.getTechnicien());
        }
        
        rdv.setStatut(StatutRDV.CONFIRME);
        RendezVous saved = rdvRepo.save(rdv);
        return mapper.toResponse(saved);
    }

    @Override
    public RendezVousDTOResponse planifier(Long id, String dateStr, String heure, Long techId) {
        try {
            RendezVous rdv = rdvRepo.findByIdWithEverything(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous #" + id + " non trouvé"));
            
            rdv.setDate(LocalDate.parse(dateStr));
            rdv.setHeureDebut(heure);
            
            if (techId != null) {
                Technicien tech = techRepo.findById(techId)
                    .orElseThrow(() -> new RuntimeException("Technicien #" + techId + " non trouvé"));
                rdv.setTechnicien(tech);
            }
            
            rdv.setStatut(StatutRDV.PLANIFIE);
            RendezVous saved = rdvRepo.save(rdv);
            return mapper.toResponse(saved);
        } catch (Exception e) {
            System.err.println("CRASH PLANIFICATION: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur Interne lors de la planification: " + e.getMessage());
        }
    }

    @Override
    public RendezVousDTOResponse annulerRendezVous(Long id) {
        RendezVous rdv = rdvRepo.findByIdWithEverything(id)
            .orElseThrow(() -> new RuntimeException("Rendez-vous #" + id + " non trouvé"));
        
        rdv.setStatut(StatutRDV.ANNULE);
        RendezVous saved = rdvRepo.save(rdv);
        
        return mapper.toResponse(saved);
    }

    @Override
    public RendezVousDTOResponse reinitialiserPlanification(Long id) {
        try {
            RendezVous rdv = rdvRepo.findByIdWithEverything(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous #" + id + " non trouvé"));
            
            if (rdv.getCreneau() != null) {
                CreneauHoraire c = rdv.getCreneau();
                c.setEstLibre(true);
                creneauRepo.save(c);
            }
            
            rdv.setStatut(StatutRDV.EN_ATTENTE);
            rdv.setDate(null);
            rdv.setHeureDebut(null);
            rdv.setTechnicien(null);
            rdv.setCreneau(null);
            
            RendezVous saved = rdvRepo.save(rdv);
            System.out.println("LOG: RDV #" + id + " remis en attente et créneau libéré.");
            return mapper.toResponse(saved);
        } catch (Exception e) {
            System.err.println("ERREUR REINITIALISATION RDV #" + id + " : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public RendezVousDTOResponse validerRendezVousAdmin(Long id) {
        return validerRendezVous(id, null);
    }

    @Override
    public RendezVousDTOResponse rejeterRendezVousAdmin(Long id) {
        RendezVous rdv = rdvRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
            
        // Libérer le créneau horaire
        CreneauHoraire creneau = rdv.getCreneau();
        if(creneau != null) {
            creneau.setEstLibre(true);
            creneauRepo.save(creneau);
        }
            
        rdv.setStatut(StatutRDV.ANNULE);
        return mapper.toResponse(rdvRepo.save(rdv));
    }

    @Override
    public List<RendezVousDTOResponse> listerTous() {
        return rdvRepo.findAllWithEverything().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<RendezVousDTOResponse> listerEnAttente() {
        return rdvRepo.findAllWithEverything().stream()
            .filter(r -> r.getStatut() == StatutRDV.EN_ATTENTE)
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<RendezVousDTOResponse> listerMesRendezVous(Long userId) {
        return rdvRepo.findAllWithEverything().stream()
            .filter(r -> r.getUser() != null && r.getUser().getId().equals(userId))
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public RendezVousDTOResponse getById(Long id) {
        return rdvRepo.findByIdWithEverything(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Rendez-vous #" + id + " non trouvé"));
    }

    @Override
    public RendezVous getEntityById(Long id) {
        return rdvRepo.findByIdWithEverything(id)
            .orElseThrow(() -> new RuntimeException("Rendez-vous #" + id + " non trouvé"));
    }
}
