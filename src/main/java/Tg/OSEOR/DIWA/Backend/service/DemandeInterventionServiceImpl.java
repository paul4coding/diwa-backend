package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.DemandeDTO.DemandeCreateRequest;
import Tg.OSEOR.DIWA.Backend.dto.DemandeDTO.DemandeDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.DemandeDTO.EnregistrementVehiculeRequest;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.entity.HistoriqueStatutDemande;
import Tg.OSEOR.DIWA.Backend.entity.ParametreAtelier;
import Tg.OSEOR.DIWA.Backend.mappers.DemandeInterventionMapper;
import Tg.OSEOR.DIWA.Backend.repository.DemandeInterventionRepository;
import Tg.OSEOR.DIWA.Backend.repository.atelier.HistoriqueStatutDemandeRepository;
import Tg.OSEOR.DIWA.Backend.repository.ParametreAtelierRepository;
import Tg.OSEOR.DIWA.Backend.repository.VehiculeRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DemandeInterventionServiceImpl implements DemandeInterventionService {

    private final DemandeInterventionRepository demandeRepository;
    private final HistoriqueStatutDemandeRepository historiqueRepository;
    private final UserRepository userRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ParametreAtelierRepository parametreRepo;
    private final EmailService emailService;
    private final DemandeInterventionMapper demandeMapper;
    private final Tg.OSEOR.DIWA.Backend.repository.PlageHoraireDIWARepository plageRepo;
    private final NotificationService notificationService;
    private final Tg.OSEOR.DIWA.Backend.repository.atelier.MissionChauffeurRepository missionChauffeurRepository;
    // Injection tardive pour éviter un cycle potentiel (FactureServiceImpl → demandeRepo)
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private FactureService factureService;

    public DemandeInterventionServiceImpl(DemandeInterventionRepository demandeRepository,
                                           HistoriqueStatutDemandeRepository historiqueRepository,
                                           UserRepository userRepository,
                                           VehiculeRepository vehiculeRepository,
                                           ParametreAtelierRepository parametreRepo,
                                           EmailService emailService,
                                           DemandeInterventionMapper demandeMapper,
                                           Tg.OSEOR.DIWA.Backend.repository.PlageHoraireDIWARepository plageRepo,
                                           NotificationService notificationService,
                                           Tg.OSEOR.DIWA.Backend.repository.atelier.MissionChauffeurRepository missionChauffeurRepository) {
        this.demandeRepository = demandeRepository;
        this.historiqueRepository = historiqueRepository;
        this.userRepository = userRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.parametreRepo = parametreRepo;
        this.emailService = emailService;
        this.demandeMapper = demandeMapper;
        this.plageRepo = plageRepo;
        this.notificationService = notificationService;
        this.missionChauffeurRepository = missionChauffeurRepository;
    }

    @Override
    public DemandeDTOResponse createDemande(DemandeCreateRequest request, String userEmail) {
        User client = userRepository.findByIdentifier(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        DemandeIntervention demande = new DemandeIntervention();
        demande.setClient(client);
        demande.setDescriptionProbleme(request.getDescriptionProbleme());
        demande.setVehiculeImmatriculation(request.getVehiculeImmatriculation() != null ? request.getVehiculeImmatriculation().toUpperCase() : null);
        demande.setVehiculeNumeroChassis(request.getVehiculeNumeroChassis() != null ? request.getVehiculeNumeroChassis().toUpperCase() : null);
        demande.setVehiculeMarque(request.getVehiculeMarque());
        demande.setVehiculeModele(request.getVehiculeModele());
        demande.setVehiculeAnnee(request.getVehiculeAnnee());
        demande.setVehiculeKilometrage(request.getVehiculeKilometrage());
        demande.setPhotosUrlsClient(request.getPhotosUrlsClient());

        try {
            demande.setUrgence(DemandeIntervention.UrgenceDemande.valueOf(
                    request.getUrgence() != null ? request.getUrgence().toUpperCase() : "NORMALE"
            ));
        } catch (Exception e) {
            demande.setUrgence(DemandeIntervention.UrgenceDemande.NORMALE);
        }

        if (Boolean.TRUE.equals(request.getDemandeRecuperation())) {
            demande.setDemandeRecuperation(true);
            demande.setAdresseRecuperation(request.getAdresseRecuperation());
            demande.setContactRecuperation(request.getContactRecuperation());
            
            if (request.getDateRecuperation() != null) {
                demande.setDateRecuperation(java.time.LocalDate.parse(request.getDateRecuperation()));
            }
            if (request.getCreneauId() != null) {
                demande.setCreneauSouhaite(plageRepo.findById(request.getCreneauId()).orElse(null));
            }
            demande.setNoteDisponibiliteClient(request.getNoteDisponibiliteClient());
            
            demande.setStatut(DemandeIntervention.StatutDemande.SOUMISE);
        } else {
            demande.setStatut(DemandeIntervention.StatutDemande.SOUMISE);
        }

        demande.setReference(generateReference());
        DemandeIntervention saved = demandeRepository.save(demande);

        ajouterHistorique(saved, null, DemandeIntervention.StatutDemande.SOUMISE, client, "Demande soumise par le client");

        return demandeMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public DemandeDTOResponse enregistrerVehicule(String identifier, EnregistrementVehiculeRequest request, String chefTechEmail) {
        DemandeIntervention demande;
        if (identifier.matches("^\\d+$")) {
            demande = demandeRepository.findById(Long.parseLong(identifier))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
        } else {
            demande = demandeRepository.findByUuid(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid)"));
        }
        User chefTech = userRepository.findByIdentifier(chefTechEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chef Technicien introuvable"));
        ParametreAtelier params = parametreRepo.findParametre()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Configuration atelier manquante"));

        // Compléter les infos véhicule
        if (request.getVehiculeImmatriculation() != null) {
            demande.setVehiculeImmatriculation(request.getVehiculeImmatriculation().toUpperCase());
        }
        if (request.getVehiculeNumeroChassis() != null) {
            demande.setVehiculeNumeroChassis(request.getVehiculeNumeroChassis().toUpperCase());
        }
        demande.setVehiculeMarque(request.getVehiculeMarque());
        demande.setVehiculeModele(request.getVehiculeModele());
        demande.setVehiculeAnnee(request.getVehiculeAnnee());
        demande.setVehiculeKilometrage(request.getVehiculeKilometrage());
        demande.setVehiculeCouleur(request.getVehiculeCouleur());
        demande.setVehiculeCarburant(request.getVehiculeCarburant());
        demande.setVehiculeBoiteVitesse(request.getVehiculeBoiteVitesse());
        demande.setPhotoCarteGriseUrl(request.getPhotoCarteGriseUrl());
        demande.setPhotoPlaqueUrl(request.getPhotoPlaqueUrl());
        demande.setObservationsArrivee(request.getObservationsArrivee());
        demande.setChefTechnicien(chefTech);

        // Assignation du technicien
        if (request.getTechnicienId() != null) {
            User tech = userRepository.findById(request.getTechnicienId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Technicien introuvable"));
            demande.setTechnicien(tech);
        }

        // Vérification véhicule DIWA pour diagnostic gratuit
        vehiculeRepository.findByImmatriculation(request.getVehiculeImmatriculation()).ifPresent(v -> {
            demande.setVehiculeDiwa(true);
            demande.setKilometrageAchat(v.getKilometrageAchat() != null ? v.getKilometrageAchat() : 0);
            if (request.getVehiculeKilometrage() <= params.getSeuilKmDiagnosticGratuit()) {
                demande.setDiagnosticGratuit(true);
            }
        });

        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setStatut(DemandeIntervention.StatutDemande.EN_ENREGISTREMENT);
        demandeRepository.saveAndFlush(demande);

        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.EN_ENREGISTREMENT, chefTech, 
            "Fiche technique complétée — Attente validation réception");

        return demandeMapper.toDTO(demande);
    }

    @Override
    @Transactional
    public DemandeDTOResponse validerEnregistrement(String identifier, String receptionnisteEmail) {
        DemandeIntervention demande;
        if (identifier.matches("^\\d+$")) {
            demande = demandeRepository.findById(Long.parseLong(identifier))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
        } else {
            demande = demandeRepository.findByUuid(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid)"));
        }
        User receptionniste = userRepository.findByIdentifier(receptionnisteEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réceptionniste introuvable"));

        demande.setReceptionniste(receptionniste);
        demande.setDateHeureReception(LocalDateTime.now());

        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setStatut(DemandeIntervention.StatutDemande.EN_RECEPTION); 
        demandeRepository.saveAndFlush(demande);

        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.EN_RECEPTION, receptionniste, "Réception validée - Véhicule officiellement enregistré");

        // Notification client
        notificationService.sendPushToClient(demande.getClient(), 
            "🚗 Véhicule en atelier", 
            "Votre véhicule " + demande.getVehiculeImmatriculation() + " est pris en charge pour diagnostic.", 
            Map.of("targetId", demande.getUuid()));

        return demandeMapper.toDTO(demande);
    }

    @Override
    public DemandeDTOResponse assignerTechnicien(String identifier, Long technicienId, String chefTechEmail) {
        DemandeIntervention demande;
        if (identifier.matches("^\\d+$")) {
            demande = demandeRepository.findById(Long.parseLong(identifier))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
        } else {
            demande = demandeRepository.findByUuid(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid)"));
        }
        User technician = userRepository.findById(technicienId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Technicien introuvable"));
        User chefTech = userRepository.findByIdentifier(chefTechEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chef Technicien introuvable"));

        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setTechnicien(technician);
        demande.setStatut(DemandeIntervention.StatutDemande.EN_DIAGNOSTIC);
        demandeRepository.save(demande);

        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.EN_DIAGNOSTIC, chefTech, "Technicien assigné pour diagnostic : " + technician.getPrenom() + " " + technician.getNom());

        return demandeMapper.toDTO(demande);
    }

    @Override
    public DemandeDTOResponse assignerChauffeurRecuperation(String identifier, Long chauffeurId, String receptionnisteEmail) {
        try {
            DemandeIntervention demande;
            if (identifier.matches("^\\d+$")) {
                demande = demandeRepository.findById(Long.parseLong(identifier))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
            } else {
                demande = demandeRepository.findByUuid(identifier)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid)"));
            }
            User nouveauChauffeur = userRepository.findById(chauffeurId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chauffeur introuvable"));
            User receptionniste = userRepository.findByIdentifier(receptionnisteEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réceptionniste introuvable"));

            User ancienChauffeur = demande.getChauffeurRecuperation();
            boolean estModification = ancienChauffeur != null;

            DemandeIntervention.StatutDemande ancienStatut = demande.getStatut();
            demande.setChauffeurRecuperation(nouveauChauffeur);
            demande.setStatut(DemandeIntervention.StatutDemande.CHAUFFEUR_ASSIGNE);
            demandeRepository.saveAndFlush(demande);

            String nomNouveau = (nouveauChauffeur.getPrenom() != null ? nouveauChauffeur.getPrenom() : "") + " " + (nouveauChauffeur.getNom() != null ? nouveauChauffeur.getNom() : "");
            if (nomNouveau.trim().isEmpty()) nomNouveau = nouveauChauffeur.getUsername();

            String msgHist = estModification 
                ? "Chauffeur modifié : de " + (ancienChauffeur.getPrenom() != null ? ancienChauffeur.getPrenom() : ancienChauffeur.getUsername()) + " à " + nomNouveau.trim()
                : "Chauffeur assigné pour récupération : " + nomNouveau.trim();

            ajouterHistorique(demande, ancienStatut, DemandeIntervention.StatutDemande.CHAUFFEUR_ASSIGNE, receptionniste, msgHist);

            try {
                System.out.println("SYNC MOBILE: Recherche mission existante pour demande " + demande.getId());
                MissionChauffeur mission = missionChauffeurRepository.findByDemandeIdOrderByCreateDateDesc(demande.getId())
                        .stream()
                        .filter(m -> m.getStatut() != MissionChauffeur.StatutMission.TERMINEE)
                        .findFirst()
                        .orElse(new MissionChauffeur());

                mission.setDemande(demande);
                mission.setChauffeur(nouveauChauffeur);
                mission.setType(MissionChauffeur.TypeMission.RECUPERATION);
                mission.setStatut(MissionChauffeur.StatutMission.ASSIGNEE);
                mission.setCreneau(demande.getCreneauSouhaite());
                
                missionChauffeurRepository.saveAndFlush(mission);
                System.out.println("SYNC MOBILE: Mission chauffeur sauvegardée avec succès ID=" + mission.getId() + " pour chauffeur=" + nouveauChauffeur.getUsername());
            } catch (Exception e) {
                System.err.println("SYNC MOBILE ERROR: Impossible de synchroniser la mission chauffeur: " + e.getMessage());
                e.printStackTrace();
            }

            // Notifications
            if (estModification && !ancienChauffeur.getId().equals(nouveauChauffeur.getId())) {
                notificationService.saveAndSendPush(ancienChauffeur, "Mission Annulée", "Mission réassignée.", "MISSION_ANNULEE", demande.getUuid());
            }
            notificationService.saveAndSendPush(nouveauChauffeur, "Nouvelle Mission", "Récupération véhicule " + demande.getVehiculeImmatriculation(), "NOUVELLE_MISSION", demande.getUuid());
            notificationService.saveAndSendPush(demande.getClient(), "Chauffeur Assigné", "Le chauffeur " + nomNouveau.trim() + " arrive.", "CHAUFFEUR_ASSIGNE", demande.getUuid());

            return demandeMapper.toDTO(demande);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur assignation: " + e.getMessage());
        }
    }

    @Override
    public DemandeDTOResponse assignerChauffeurLivraison(String identifier, Long chauffeurId, String receptionnisteEmail) {
        try {
            DemandeIntervention demande;
            if (identifier.matches("^\\d+$")) {
                demande = demandeRepository.findById(Long.parseLong(identifier))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
            } else {
                demande = demandeRepository.findByUuid(identifier)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid)"));
            }
            User chauffeur = userRepository.findById(chauffeurId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chauffeur introuvable"));
            User receptionniste = userRepository.findByIdentifier(receptionnisteEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réceptionniste introuvable"));

            DemandeIntervention.StatutDemande ancienStatut = demande.getStatut();
            demande.setChauffeurLivraison(chauffeur);
            demande.setStatut(DemandeIntervention.StatutDemande.EN_LIVRAISON);
            demandeRepository.saveAndFlush(demande);

            String nomChauffeur = (chauffeur.getPrenom() != null ? chauffeur.getPrenom() : "") + " " + (chauffeur.getNom() != null ? chauffeur.getNom() : "");
            if (nomChauffeur.trim().isEmpty()) nomChauffeur = chauffeur.getUsername();

            ajouterHistorique(demande, ancienStatut, DemandeIntervention.StatutDemande.EN_LIVRAISON, receptionniste, "Chauffeur assigné pour livraison : " + nomChauffeur.trim());

            // Sync Mobile
            try {
                MissionChauffeur mission = new MissionChauffeur();
                mission.setDemande(demande);
                mission.setChauffeur(chauffeur);
                mission.setType(MissionChauffeur.TypeMission.LIVRAISON);
                mission.setStatut(MissionChauffeur.StatutMission.ASSIGNEE);
                missionChauffeurRepository.saveAndFlush(mission);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Notifications
            notificationService.saveAndSendPush(chauffeur, "Nouvelle Livraison", "Livraison du véhicule " + demande.getVehiculeImmatriculation(), "NOUVELLE_MISSION", demande.getUuid());
            notificationService.saveAndSendPush(demande.getClient(), "Véhicule en route", "Le chauffeur " + nomChauffeur.trim() + " vous livre votre véhicule.", "EN_LIVRAISON", demande.getUuid());

            return demandeMapper.toDTO(demande);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur livraison: " + e.getMessage());
        }
    }

    @Override
    public DemandeDTOResponse confirmerRecuperation(String identifier, String chauffeurEmail) {
        DemandeIntervention demande;
        if (identifier.matches("^\\d+$")) {
            demande = demandeRepository.findById(Long.parseLong(identifier))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
        } else {
            demande = demandeRepository.findByUuid(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid)"));
        }
        User chauffeur = userRepository.findByIdentifier(chauffeurEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chauffeur introuvable"));

        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setStatut(DemandeIntervention.StatutDemande.VEHICULE_EN_TRANSIT);
        demandeRepository.save(demande);

        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.VEHICULE_EN_TRANSIT, chauffeur, "Le chauffeur a récupéré le véhicule");

        notificationService.saveAndSendPush(
            demande.getClient(),
            "Chauffeur en route",
            "Le chauffeur a récupéré votre véhicule et fait route vers le garage DIWA.",
            "VEHICULE_EN_TRANSIT",
            demande.getUuid()
        );

        return demandeMapper.toDTO(demande);
    }

    @Override
    public DemandeDTOResponse confirmerArriveeGarage(String identifier, String chauffeurEmail) {
        DemandeIntervention demande;
        if (identifier.matches("^\\d+$")) {
            demande = demandeRepository.findById(Long.parseLong(identifier))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
        } else {
            demande = demandeRepository.findByUuid(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid)"));
        }
        User chauffeur = userRepository.findByIdentifier(chauffeurEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chauffeur introuvable"));

        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setStatut(DemandeIntervention.StatutDemande.VEHICULE_RECU);
        demandeRepository.save(demande);

        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.VEHICULE_RECU, chauffeur, "Le véhicule est arrivé à DIWA");

        notificationService.saveAndSendPush(
            demande.getClient(),
            "Arrivé à DIWA",
            "Votre véhicule " + demande.getVehiculeImmatriculation() + " est bien arrivé au garage DIWA.",
            "VEHICULE_RECU",
            demande.getUuid()
        );

        return demandeMapper.toDTO(demande);
    }

    @Override
    @Transactional(readOnly = true)
    public DemandeDTOResponse getByUuid(String identifier, String userEmail) {
        DemandeIntervention demande;
        
        // On essaie d'abord si c'est un ID numérique
        if (identifier.matches("^\\d+$")) {
            demande = demandeRepository.findById(Long.parseLong(identifier))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable avec l'ID: " + identifier));
        } else {
            // Sinon on cherche par UUID
            demande = demandeRepository.findByUuid(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable avec l'UUID: " + identifier));
        }
        
        return demandeMapper.toDTO(demande);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandeDTOResponse> getMesDemandes(String userEmail) {
        User client = userRepository.findByIdentifier(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        return demandeRepository.findByClientIdOrderByCreateDateDesc(client.getId())
                .stream().map(demandeMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandeDTOResponse> getAllFiltrees(String statut, String urgence) {
        DemandeIntervention.StatutDemande s = (statut != null && !statut.isEmpty()) ? DemandeIntervention.StatutDemande.valueOf(statut) : null;
        DemandeIntervention.UrgenceDemande u = (urgence != null && !urgence.isEmpty()) ? DemandeIntervention.UrgenceDemande.valueOf(urgence) : null;
        
        return demandeRepository.findAllFiltrees(s, u)
                .stream().map(demandeMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public DemandeDTOResponse enregistrerSortie(String identifier, String receptionnisteEmail) {
        DemandeIntervention demande;
        if (identifier.matches("^\\d+$")) {
            demande = demandeRepository.findById(Long.parseLong(identifier))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
        } else {
            demande = demandeRepository.findByUuid(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid)"));
        }
        User receptionniste = userRepository.findByIdentifier(receptionnisteEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réceptionniste introuvable"));

        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setDateHeureSortie(LocalDateTime.now());
        demande.setStatut(DemandeIntervention.StatutDemande.CLOTURE);
        demandeRepository.save(demande);

        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.CLOTURE, receptionniste, "Véhicule sorti — Dossier clôturé");

        // Génération automatique de la facture finale (Sprint 6)
        if (factureService != null) {
            try {
                factureService.generer(demande.getId());
            } catch (Exception e) {
                System.err.println("[DIWA Facture] Génération ignorée (sortie) : " + e.getMessage());
            }
        }

        return demandeMapper.toDTO(demande);
    }

    @Override
    public DemandeDTOResponse createDemandeDirecte(DemandeCreateRequest request, String auteurEmail) {
        User auteur = userRepository.findByIdentifier(auteurEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auteur introuvable"));

        DemandeIntervention demande = new DemandeIntervention();
        demande.setVehiculeImmatriculation(request.getVehiculeImmatriculation() != null ? request.getVehiculeImmatriculation().toUpperCase() : null);
        demande.setVehiculeNumeroChassis(request.getVehiculeNumeroChassis() != null ? request.getVehiculeNumeroChassis().toUpperCase() : null);
        demande.setVehiculeMarque(request.getVehiculeMarque());
        demande.setVehiculeModele(request.getVehiculeModele());
        demande.setVehiculeAnnee(request.getVehiculeAnnee());
        demande.setVehiculeKilometrage(request.getVehiculeKilometrage());
        demande.setDescriptionProbleme(request.getDescriptionProbleme());
        demande.setStatut(DemandeIntervention.StatutDemande.VEHICULE_RECU); // Déjà au garage
        demande.setReference(generateReference());
        
        if (request.getClientId() != null) {
            User client = userRepository.findById(request.getClientId()).orElse(null);
            demande.setClient(client);
        }

        DemandeIntervention saved = demandeRepository.save(demande);
        String clientInfo = (saved.getClient() != null) ? " (Client: " + saved.getClient().getPrenom() + " " + saved.getClient().getNom() + ")" : "";
        ajouterHistorique(saved, null, DemandeIntervention.StatutDemande.VEHICULE_RECU, auteur, "Arrivée physique du véhicule au garage" + clientInfo);

        return demandeMapper.toDTO(saved);
    }

    @Override
    public DemandeDTOResponse associerClient(String identifier, Long clientId, String receptionnisteEmail) {
        DemandeIntervention demande;
        if (identifier.matches("^\\d+$")) {
            demande = demandeRepository.findById(Long.parseLong(identifier))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
        } else {
            demande = demandeRepository.findByUuid(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid)"));
        }
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable"));
        User receptionniste = userRepository.findByIdentifier(receptionnisteEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réceptionniste introuvable"));

        demande.setClient(client);
        DemandeIntervention saved = demandeRepository.save(demande);
        
        ajouterHistorique(saved, saved.getStatut(), saved.getStatut(), receptionniste, "Demande associée au client : " + client.getPrenom() + " " + client.getNom());

        return demandeMapper.toDTO(saved);
    }

    @Override
    public void ajouterHistorique(DemandeIntervention demande,
                                    DemandeIntervention.StatutDemande ancien,
                                    DemandeIntervention.StatutDemande nouveau,
                                    User auteur, String commentaire) {
        HistoriqueStatutDemande h = new HistoriqueStatutDemande();
        h.setDemande(demande);
        h.setAncienStatut(ancien);
        h.setNouveauStatut(nouveau);
        h.setAuteur(auteur);
        h.setCommentaire(commentaire);
        historiqueRepository.save(h);
    }

    @Override
    public DemandeDTOResponse planifierRecuperation(String identifier, LocalDate date, Long creneauId, String receptionnisteEmail) {
        DemandeIntervention demande;
        if (identifier.matches("^\\d+$")) {
            demande = demandeRepository.findById(Long.parseLong(identifier))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
        } else {
            demande = demandeRepository.findByUuid(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid)"));
        }
        User receptionniste = userRepository.findByIdentifier(receptionnisteEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réceptionniste introuvable"));

        if (date != null) {
            demande.setDateRecuperation(date);
        }
        if (creneauId != null) {
            demande.setCreneauSouhaite(plageRepo.findById(creneauId).orElse(null));
        }

        demandeRepository.save(demande);
        
        String infoPlanif = (date != null ? "le " + date : "") + (creneauId != null ? " (créneau id: " + creneauId + ")" : "");
        ajouterHistorique(demande, demande.getStatut(), demande.getStatut(), receptionniste, "Planification modifiée/définie par la réception : " + infoPlanif);

        return demandeMapper.toDTO(demande);
    }

    private String generateReference() {
        long count = demandeRepository.count() + 1;
        return String.format("DEM-%d-%05d", LocalDate.now().getYear(), count);
    }

    @Override
    public DemandeDTOResponse annulerMissionChauffeur(String identifier, String auteurEmail) {
        DemandeIntervention demande;
        if (identifier.matches("^\\d+$")) {
            demande = demandeRepository.findById(Long.parseLong(identifier))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
        } else {
            demande = demandeRepository.findByUuid(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid)"));
        }
        User auteur = userRepository.findByIdentifier(auteurEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setChauffeurRecuperation(null);
        demande.setStatut(DemandeIntervention.StatutDemande.SOUMISE);
        demandeRepository.save(demande);

        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.SOUMISE, auteur, "Mission annulée manuellement — Chauffeur libéré");

        return demandeMapper.toDTO(demande);
    }

    @Override
    @Transactional
    public void deleteDemande(String identifier, String userEmail) {
        DemandeIntervention demande;
        if (identifier.matches("^\\d+$")) {
            demande = demandeRepository.findById(Long.parseLong(identifier))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
        } else {
            demande = demandeRepository.findByUuid(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid)"));
        }
        Long id = demande.getId();

        // On vérifie les droits (Admin ou Receptionniste)
        User user = userRepository.findByIdentifier(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().name().equals("ROLE_ADMIN"));
        boolean isReception = user.getRoles().stream().anyMatch(r -> r.getName().name().equals("ROLE_RECEPTIONNISTE"));

        if (!isAdmin && !isReception) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seul l'administrateur ou la réception peut supprimer un ticket");
        }

        // Supprimer les missions chauffeur associées (car pas de cascade automatique configurée dans l'entité MissionChauffeur)
        List<MissionChauffeur> missions = missionChauffeurRepository.findByDemandeIdOrderByCreateDateDesc(id);
        if (!missions.isEmpty()) {
            missionChauffeurRepository.deleteAll(missions);
        }

        // Les historiques et pro-formas sont supprimés via CascadeType.ALL dans l'entité DemandeIntervention
        demandeRepository.delete(demande);
    }
}
