package Tg.OSEOR.DIWA.Backend.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import Tg.OSEOR.DIWA.Backend.dto.CreneauDTO;
import Tg.OSEOR.DIWA.Backend.dto.GlobalPlanningDTO;
import Tg.OSEOR.DIWA.Backend.entity.PlageHoraire;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.entity.TicketSAV;
import Tg.OSEOR.DIWA.Backend.entity.RendezVous;
import Tg.OSEOR.DIWA.Backend.entity.ServiceSAV;
import Tg.OSEOR.DIWA.Backend.entity.Technicien;
import Tg.OSEOR.DIWA.Backend.entity.enums.StatutRDV;
import Tg.OSEOR.DIWA.Backend.mappers.RendezVousMapper;
import Tg.OSEOR.DIWA.Backend.mappers.TicketSAVMapper;
import Tg.OSEOR.DIWA.Backend.mappers.DemandeInterventionMapper;
import Tg.OSEOR.DIWA.Backend.repository.PlageHoraireRepository;
import Tg.OSEOR.DIWA.Backend.repository.RendezVousRepository;
import Tg.OSEOR.DIWA.Backend.repository.ServiceSAVRepository;
import Tg.OSEOR.DIWA.Backend.repository.TechnicienRepository;
import Tg.OSEOR.DIWA.Backend.repository.TicketSAVRepository;
import Tg.OSEOR.DIWA.Backend.repository.DemandeInterventionRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;

@Service
@Transactional
public class GarageServiceImpl implements GarageService {

    @Autowired
    private TechnicienRepository technicienRepository;

    @Autowired
    private PlageHoraireRepository plageHoraireRepository;

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private ServiceSAVRepository serviceSAVRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketSAVRepository ticketSAVRepository;
    
    @Autowired
    private DemandeInterventionRepository demandeRepository;
 
    @Autowired
    private RendezVousMapper rdvMapper;
 
    @Autowired
    private TicketSAVMapper ticketMapper;

    @Autowired
    private DemandeInterventionMapper demandeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CreneauDTO> getDisponibilites(LocalDate date, String typeService) {
        DayOfWeek jourSemaine = date.getDayOfWeek();
        List<Technicien> techniciens = technicienRepository.findByActifTrue();
        List<CreneauDTO> creneaux = new ArrayList<>();

        for (Technicien tech : techniciens) {
            List<PlageHoraire> plages = plageHoraireRepository
                    .findByTechnicienIdAndJourSemaine(tech.getId(), jourSemaine);

            for (PlageHoraire plage : plages) {
                // Vérifier qu'aucun RDV n'existe déjà sur cette plage ce jour-là
                boolean rdvExiste = rendezVousRepository.existsByCreneauIdAndDate(plage.getId(), date);

                if (!rdvExiste && plage.isEstDisponible()) {
                    creneaux.add(new CreneauDTO(
                            plage.getId(),
                            tech.getId(),
                            tech.getNom() + " " + (tech.getPrenom() != null ? tech.getPrenom() : ""),
                            tech.getSpecialite(),
                            plage.getHeureDebut(),
                            plage.getHeureFin(),
                            true
                    ));
                }
            }
        }

        return creneaux;
    }

    @Override
    @Transactional
    public RendezVous createRendezVous(Long plageHoraireId, Long userId, Long serviceId,
                                       String typeIntervention, LocalDate date) {
        // 1. Vérifier disponibilité atomiquement
        PlageHoraire plage = plageHoraireRepository.findById(plageHoraireId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Créneau introuvable"));

        if (!plage.isEstDisponible()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce créneau est déjà pris");
        }

        boolean rdvExiste = rendezVousRepository.existsByCreneauIdAndDate(plageHoraireId, date);
        if (rdvExiste) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un rendez-vous existe déjà sur ce créneau à cette date");
        }

        // 2. Récupérer les entités liées
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        ServiceSAV service = null;
        if (serviceId != null) {
            service = serviceSAVRepository.findById(serviceId).orElse(null);
        }

        // 3. Créer le rendez-vous
        RendezVous rdv = new RendezVous();
        rdv.setDate(date);
        rdv.setStatut(StatutRDV.PLANIFIE);
        rdv.setUser(user);
        rdv.setTechnicien(plage.getTechnicien());
        rdv.setService(service);

        return rendezVousRepository.save(rdv);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<DayOfWeek, List<RendezVous>> getPlanningTechnicien(Long techId, LocalDate semaine) {
        // semaine = date du lundi
        LocalDate lundi = semaine.with(java.time.DayOfWeek.MONDAY);
        LocalDate dimanche = lundi.plusDays(6);

        List<RendezVous> rdvs = rendezVousRepository
                .findByTechnicienIdAndDateBetween(techId, lundi, dimanche);

        Map<DayOfWeek, List<RendezVous>> planning = new HashMap<>();
        for (DayOfWeek jour : DayOfWeek.values()) {
            planning.put(jour, new ArrayList<>());
        }

        for (RendezVous rdv : rdvs) {
            DayOfWeek jour = rdv.getDate().getDayOfWeek();
            planning.get(jour).add(rdv);
        }

        return planning;
    }


    @Override
    @Transactional
    public RendezVous updateStatutRdv(Long rdvId, String statut) {
        RendezVous rdv = rendezVousRepository.findById(rdvId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rendez-vous introuvable"));
        rdv.setStatut(StatutRDV.valueOf(statut.toUpperCase()));
        return rendezVousRepository.save(rdv);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getMesRendezVous(Long userId) {
        return rendezVousRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalPlanningDTO getGlobalPlanning(LocalDate debut, LocalDate fin) {
        List<RendezVous> rdvs = rendezVousRepository.findByDateBetweenWithEverything(debut, fin);
        List<TicketSAV> tickets = ticketSAVRepository.findAll(); 
        
        // On récupère aussi les demandes avec visite planifiée
        List<DemandeIntervention> visites = demandeRepository.findAll().stream()
            .filter(d -> d.getDemandeVisite() != null && d.getDemandeVisite() && d.getCreneauVisite() != null)
            .filter(d -> !d.getCreneauVisite().getDate().isBefore(debut) && !d.getCreneauVisite().getDate().isAfter(fin))
            .collect(Collectors.toList());

        return new GlobalPlanningDTO(
            rdvs.stream().map(rdvMapper::toResponse).collect(Collectors.toList()),
            tickets.stream()
                .filter(t -> t.getTechnicien() != null)
                .map(ticketMapper::toResponse)
                .collect(Collectors.toList()),
            visites.stream().map(demandeMapper::toDTO).collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public void assignTicketToTechnician(Long ticketId, Long techId) {
        Tg.OSEOR.DIWA.Backend.entity.TicketSAV ticket = ticketSAVRepository.findById(ticketId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket SAV introuvable"));
            
        Technicien tech = technicienRepository.findById(techId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Technicien introuvable"));
            
        ticket.setTechnicien(tech);
        ticket.setStatut(Tg.OSEOR.DIWA.Backend.entity.enums.StatutTicket.EN_COURS);
        ticketSAVRepository.save(ticket);
    }
}
