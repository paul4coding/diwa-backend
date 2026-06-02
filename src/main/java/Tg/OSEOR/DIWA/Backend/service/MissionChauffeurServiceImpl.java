package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.MissionChauffeurDTO.*;
import Tg.OSEOR.DIWA.Backend.dto.UserDTO.UserDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.model.atelier.MissionChauffeur;
import Tg.OSEOR.DIWA.Backend.repository.DemandeInterventionRepository;
import Tg.OSEOR.DIWA.Backend.repository.atelier.MissionChauffeurRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import Tg.OSEOR.DIWA.Backend.mappers.MissionMapper;
import Tg.OSEOR.DIWA.Backend.mappers.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MissionChauffeurServiceImpl implements MissionChauffeurService {

    private final MissionChauffeurRepository missionRepo;
    private final DemandeInterventionRepository demandeRepo;
    private final DemandeInterventionService demandeService;
    private final UserRepository userRepository;
    private final MissionMapper missionMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final FactureService factureService;

    public MissionChauffeurServiceImpl(MissionChauffeurRepository missionRepo,
                                       DemandeInterventionRepository demandeRepo,
                                       DemandeInterventionService demandeService,
                                       UserRepository userRepository,
                                       MissionMapper missionMapper,
                                       UserMapper userMapper,
                                       NotificationService notificationService,
                                       EmailService emailService,
                                       FactureService factureService) {
        this.missionRepo = missionRepo;
        this.demandeRepo = demandeRepo;
        this.demandeService = demandeService;
        this.userRepository = userRepository;
        this.missionMapper = missionMapper;
        this.userMapper = userMapper;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.factureService = factureService;
    }

    @Override
    @Transactional
    public MissionDTOResponse creerMission(Long demandeId, Long chauffeurId, MissionChauffeur.TypeMission type, String receptionnisteEmail) {
        DemandeIntervention demande = demandeRepo.findById(demandeId).orElseThrow();
        User chauffeur = userRepository.findById(chauffeurId).orElseThrow();

        if (!missionRepo.findMissionsActives(chauffeurId).isEmpty()) {
            throw new IllegalStateException("Ce chauffeur a déjà une mission en cours");
        }

        MissionChauffeur mission = new MissionChauffeur();
        mission.setChauffeur(chauffeur);
        mission.setDemande(demande);
        mission.setType(type);
        mission.setStatut(MissionChauffeur.StatutMission.ASSIGNEE);

        if (type == MissionChauffeur.TypeMission.RECUPERATION) {
            mission.setCreneau(demande.getCreneauSouhaite());
        }

        missionRepo.save(mission);

        if (type == MissionChauffeur.TypeMission.RECUPERATION) {
            demande.setChauffeurRecuperation(chauffeur);
            demande.setStatut(DemandeIntervention.StatutDemande.CHAUFFEUR_ASSIGNE);
        } else {
            demande.setChauffeurLivraison(chauffeur);
            demande.setStatut(DemandeIntervention.StatutDemande.EN_LIVRAISON);
        }
        demandeRepo.save(demande);

        notificationService.sendPushToChauffeur(chauffeur, "Nouvelle mission assignée",
            "Récupération chez " + demande.getClient().getNom() + " — " + demande.getAdresseRecuperation());

        return missionMapper.toDTO(mission);
    }

    @Override
    public List<UserDTOResponse> getChauffeursDisponibles() {
        return missionRepo.findChauffeursDisponibles().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MissionDTOResponse getMissionActive(String chauffeurEmail) {
        System.out.println("DEBUG MOBILE: getMissionActive pour identifier=" + chauffeurEmail);
        User chauffeur = userRepository.findByIdentifier(chauffeurEmail).orElseThrow();
        List<MissionChauffeur> actives = missionRepo.findMissionsActives(chauffeur.getId());
        System.out.println("DEBUG MOBILE: Missions trouvées pour chauffeur " + chauffeur.getId() + " (" + chauffeur.getUsername() + ") count=" + actives.size());
        
        if (!actives.isEmpty()) {
            System.out.println("DEBUG MOBILE: Mission trouvée ID=" + actives.get(0).getId() + " Statut=" + actives.get(0).getStatut());
        }
        
        return actives.isEmpty() ? null : missionMapper.toDTO(actives.get(0));
    }

    @Override
    @Transactional
    public MissionDTOResponse marquerDepart(Long missionId, String identifier) {
        MissionChauffeur mission = missionRepo.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission introuvable ID=" + missionId));
        
        System.out.println("DEBUG API: marquerDepart MissionID=" + missionId + " liée à DemandeID=" + (mission.getDemande() != null ? mission.getDemande().getId() : "NULL"));
        
        User currentUser = userRepository.findByIdentifier(identifier)
            .orElseThrow(() -> new RuntimeException("Chauffeur introuvable identifier=" + identifier));
        
        System.out.println("DEBUG MISSION: Comparaison mission.chauffeur=" + mission.getChauffeur().getId() + " vs current=" + currentUser.getId());
        if (!mission.getChauffeur().getId().equals(currentUser.getId())) {
            throw new SecurityException("Vous n'êtes pas assigné à cette mission");
        }

        mission.setHeureDepart(LocalDateTime.now());
        mission.setStatut(MissionChauffeur.StatutMission.EN_ROUTE_VERS_CLIENT);
        missionRepo.saveAndFlush(mission);

        Long demandeId = mission.getDemande().getId();
        DemandeIntervention demande = demandeRepo.findById(demandeId)
            .orElseThrow(() -> new RuntimeException("Demande introuvable ID=" + demandeId));

        DemandeIntervention.StatutDemande ancienStatut = demande.getStatut();
        // For delivery missions, keep EN_LIVRAISON status — don't revert to CHAUFFEUR_EN_ROUTE
        if (mission.getType() != MissionChauffeur.TypeMission.LIVRAISON) {
            demande.setStatut(DemandeIntervention.StatutDemande.CHAUFFEUR_EN_ROUTE);
            demandeRepo.saveAndFlush(demande);
        }
        
        System.out.println("DEBUG MISSION: Statut Demande ID=" + demande.getId() + " mis à jour vers " + demande.getStatut());
        
        demandeService.ajouterHistorique(demande, ancienStatut, demande.getStatut(), currentUser, "Chauffeur en route vers le client");
        
        System.out.println("DEBUG MISSION: Statut mission mis à jour à EN_ROUTE_VERS_CLIENT et demande à CHAUFFEUR_EN_ROUTE");
        
        try {
            notificationService.sendPushToClient(mission.getDemande().getClient(), 
                "Chauffeur en route", "Votre chauffeur " + mission.getChauffeur().getPrenom() + " " + mission.getChauffeur().getNom() + " est en route vers vous.");
        } catch (Exception e) {
            System.err.println("DEBUG MISSION: Erreur notification (ignorée): " + e.getMessage());
        }
        
        return missionMapper.toDTO(mission);
    }

    @Override
    @Transactional
    public MissionDTOResponse marquerArriveeClient(Long missionId, String identifier) {
        System.out.println("DEBUG MISSION: marquerArriveeClient ID=" + missionId);
        MissionChauffeur mission = missionRepo.findById(missionId).orElseThrow();
        User currentUser = userRepository.findByIdentifier(identifier).orElseThrow();

        if (!mission.getChauffeur().getId().equals(currentUser.getId())) {
            throw new SecurityException("Accès refusé");
        }

        mission.setHeureArriveeClient(LocalDateTime.now());
        mission.setStatut(MissionChauffeur.StatutMission.ARRIVE_CHEZ_CLIENT);
        missionRepo.saveAndFlush(mission);

        Long demandeId = mission.getDemande().getId();
        DemandeIntervention demande = demandeRepo.findById(demandeId)
            .orElseThrow(() -> new RuntimeException("Demande introuvable ID=" + demandeId));

        DemandeIntervention.StatutDemande ancienStatut = demande.getStatut();
        // For delivery missions, keep EN_LIVRAISON — driver is arriving at client's home
        if (mission.getType() != MissionChauffeur.TypeMission.LIVRAISON) {
            demande.setStatut(DemandeIntervention.StatutDemande.CHAUFFEUR_ARRIVE_CHEZ_CLIENT);
            demandeRepo.saveAndFlush(demande);
        }

        demandeService.ajouterHistorique(demande, ancienStatut, demande.getStatut(), currentUser,
            mission.getType() == MissionChauffeur.TypeMission.LIVRAISON
                ? "Chauffeur arrivé à l'adresse de livraison"
                : "Chauffeur arrivé chez le client");

        System.out.println("DEBUG MISSION: Statut mission mis à jour à ARRIVE_CHEZ_CLIENT et demande à CHAUFFEUR_ARRIVE_CHEZ_CLIENT");
        return missionMapper.toDTO(mission);
    }

    @Override
    @Transactional
    public MissionDTOResponse soumettreChecking(Long missionId, CheckingRequest req, String identifier) {
        MissionChauffeur mission = missionRepo.findById(missionId).orElseThrow();
        User currentUser = userRepository.findByIdentifier(identifier).orElseThrow();

        if (!mission.getChauffeur().getId().equals(currentUser.getId())) {
            throw new SecurityException("Accès refusé");
        }
        
        mission.setCheckingPhotoAvant(req.getPhotoAvantUrl());
        mission.setCheckingPhotoArriere(req.getPhotoArriereUrl());
        mission.setCheckingPhotoGauche(req.getPhotoGaucheUrl());
        mission.setCheckingPhotoDroit(req.getPhotoDroitUrl());
        mission.setCheckingVideoUrl(req.getVideoUrl());
        mission.setCheckingObservations(req.getObservations());
        mission.setCheckingComplet(true);
        mission.setStatut(MissionChauffeur.StatutMission.CHECKING_SOUMIS);
        missionRepo.saveAndFlush(mission);

        DemandeIntervention demande = mission.getDemande();
        if (demande.getStatut() != DemandeIntervention.StatutDemande.CHAUFFEUR_ARRIVE_CHEZ_CLIENT) {
            DemandeIntervention.StatutDemande ancien = demande.getStatut();
            demande.setStatut(DemandeIntervention.StatutDemande.CHAUFFEUR_ARRIVE_CHEZ_CLIENT);
            demandeRepo.saveAndFlush(demande);
            demandeService.ajouterHistorique(demande, ancien, demande.getStatut(), currentUser, "Checking soumis par le chauffeur");
        }

        User client = mission.getDemande().getClient();
        notificationService.sendPushToClient(client,
            "⚠️ Approbation requise",
            "Vérifiez l'état de votre véhicule avant l'enlèvement",
            Map.of("missionId", missionId, "action", "APPROUVER_CHECKING"));

        // emailService.sendCheckingPhotos(...) - À implémenter

        return missionMapper.toDTO(mission);
    }

    @Override
    @Transactional
    public MissionDTOResponse approuverChecking(Long missionId, Boolean approuve, String motifRefus, String identifier) {
        System.out.println(">>> DEBUG APPROBATION START: MissionID=" + missionId + " | User=" + identifier);
        
        if (missionId == null) {
            throw new RuntimeException("L'ID de la mission est manquant");
        }

        MissionChauffeur mission = missionRepo.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission introuvable pour l'ID: " + missionId));

        if (mission.getDemande() == null) {
            throw new RuntimeException("La mission n'est liée à aucune demande");
        }

        User client = mission.getDemande().getClient();
        if (client == null) {
            System.err.println(">>> DEBUG APPROBATION ERROR: Aucun client sur la demande ID=" + mission.getDemande().getId());
            throw new RuntimeException("Aucun client n'est associé à ce dossier");
        }

        System.out.println(">>> DEBUG APPROBATION: Checking identity... ClientEmail=" + client.getEmail() + " | Identifier=" + identifier);

        // Comparaison robuste (insensible à la casse)
        boolean isAuthorized = (client.getEmail() != null && client.getEmail().equalsIgnoreCase(identifier)) 
                            || (client.getUsername() != null && client.getUsername().equalsIgnoreCase(identifier));

        if (!isAuthorized) {
            System.err.println(">>> DEBUG APPROBATION ERROR: Accès refusé pour " + identifier);
            throw new SecurityException("Vous n'êtes pas autorisé à valider ce dossier");
        }

        mission.setClientApprouve(approuve);
        mission.setHeureApprobationClient(LocalDateTime.now());

        if (Boolean.TRUE.equals(approuve)) {
            System.out.println(">>> DEBUG APPROBATION: APPROUVÉ");
            mission.setStatut(MissionChauffeur.StatutMission.APPROUVE_PAR_CLIENT);
            
            DemandeIntervention demande = mission.getDemande();
            DemandeIntervention.StatutDemande ancien = demande.getStatut();
            demande.setStatut(DemandeIntervention.StatutDemande.VEHICULE_EN_TRANSIT);
            demandeRepo.saveAndFlush(demande);
            
            demandeService.ajouterHistorique(demande, ancien, demande.getStatut(), mission.getChauffeur(), "Enlèvement validé par le client");
            notificationService.sendPushToChauffeur(mission.getChauffeur(), "✅ Validé !", "Le client a validé l'état. Vous pouvez partir.");
        } else {
            System.out.println(">>> DEBUG APPROBATION: REFUSÉ");
            mission.setStatut(MissionChauffeur.StatutMission.REFUSE_PAR_CLIENT);
            mission.setMotifRefusClient(motifRefus != null ? motifRefus : "Refusé par le client");
        }

        return missionMapper.toDTO(missionRepo.saveAndFlush(mission));
    }

    @Override
    @Transactional
    public MissionDTOResponse marquerQuitterClient(Long missionId, String identifier) {
        MissionChauffeur mission = missionRepo.findById(missionId).orElseThrow();
        User currentUser = userRepository.findByIdentifier(identifier).orElseThrow();

        if (!mission.getChauffeur().getId().equals(currentUser.getId())) {
            throw new SecurityException("Accès refusé");
        }

        mission.setHeureQuitterClient(LocalDateTime.now());
        mission.setStatut(MissionChauffeur.StatutMission.EN_ROUTE_VERS_DIWA);
        
        DemandeIntervention demande = mission.getDemande();
        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setStatut(DemandeIntervention.StatutDemande.VEHICULE_EN_TRANSIT);
        demandeRepo.saveAndFlush(demande);
        
        demandeService.ajouterHistorique(demande, ancien, demande.getStatut(), currentUser, "Chauffeur a quitté le client, en route vers DIWA");
        
        return missionMapper.toDTO(missionRepo.saveAndFlush(mission));
    }

    @Override
    @Transactional
    public MissionDTOResponse marquerArriveeDiwa(Long missionId, String identifier) {
        MissionChauffeur mission = missionRepo.findById(missionId).orElseThrow();
        User currentUser = userRepository.findByIdentifier(identifier).orElseThrow();

        if (!mission.getChauffeur().getId().equals(currentUser.getId())) {
            throw new SecurityException("Accès refusé");
        }

        mission.setHeureArriveeDiwa(LocalDateTime.now());
        mission.setStatut(MissionChauffeur.StatutMission.ARRIVE_A_DIWA);
        
        DemandeIntervention demande = mission.getDemande();
        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setStatut(DemandeIntervention.StatutDemande.VEHICULE_RECU);
        demandeRepo.saveAndFlush(demande);
        
        demandeService.ajouterHistorique(demande, ancien, demande.getStatut(), currentUser, "Véhicule arrivé au centre DIWA");
        
        return missionMapper.toDTO(missionRepo.saveAndFlush(mission));
    }

    @Override
    @Transactional
    public MissionDTOResponse soumettreReception(Long missionId, ReceptionRequest req, String identifier) {
        MissionChauffeur mission = missionRepo.findById(missionId).orElseThrow();
        User currentUser = userRepository.findByIdentifier(identifier).orElseThrow();

        if (!mission.getChauffeur().getId().equals(currentUser.getId())) {
            throw new SecurityException("Accès refusé");
        }

        mission.setReceptionPhotoAvant(req.getPhotoAvantUrl());
        mission.setReceptionPhotoArriere(req.getPhotoArriereUrl());
        mission.setReceptionPhotoGauche(req.getPhotoGaucheUrl());
        mission.setReceptionPhotoDroit(req.getPhotoDroitUrl());
        mission.setReceptionObservations(req.getObservations());
        mission.setReceptionComplete(true);
        mission.setStatut(MissionChauffeur.StatutMission.TERMINEE);
        
        DemandeIntervention demande = mission.getDemande();
        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setStatut(DemandeIntervention.StatutDemande.EN_RECEPTION);
        demandeRepo.saveAndFlush(demande);

        demandeService.ajouterHistorique(demande, ancien, demande.getStatut(), currentUser, "Véhicule réceptionné au garage par le chauffeur");

        notificationService.sendPushToClient(demande.getClient(),
            "ℹ️ Votre véhicule est arrivé à DIWA",
            "Photos de l'état du véhicule à l'arrivée disponibles dans votre espace",
            null);

        return missionMapper.toDTO(missionRepo.saveAndFlush(mission));
    }

    @Override
    @Transactional
    public MissionDTOResponse confirmerLivraison(Long missionId, ReceptionRequest req, String identifier) {
        MissionChauffeur mission = missionRepo.findById(missionId).orElseThrow();
        User currentUser = userRepository.findByIdentifier(identifier).orElseThrow();

        if (!mission.getChauffeur().getId().equals(currentUser.getId())) {
            throw new SecurityException("Accès refusé");
        }
        if (mission.getType() != MissionChauffeur.TypeMission.LIVRAISON) {
            throw new IllegalStateException("Cette mission n'est pas une mission de livraison");
        }

        // Reuse receptionPhoto fields to store delivery confirmation photos
        mission.setReceptionPhotoAvant(req.getPhotoAvantUrl());
        mission.setReceptionPhotoArriere(req.getPhotoArriereUrl());
        mission.setReceptionPhotoGauche(req.getPhotoGaucheUrl());
        mission.setReceptionPhotoDroit(req.getPhotoDroitUrl());
        mission.setReceptionObservations(req.getObservations());
        mission.setReceptionComplete(true);
        mission.setStatut(MissionChauffeur.StatutMission.TERMINEE);
        missionRepo.saveAndFlush(mission);

        DemandeIntervention demande = mission.getDemande();
        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setStatut(DemandeIntervention.StatutDemande.CLOTURE);
        demandeRepo.saveAndFlush(demande);

        demandeService.ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.CLOTURE, currentUser,
            "Véhicule livré au domicile du client — Photos de livraison enregistrées");

        // Génération automatique de la facture finale (Sprint 6)
        try {
            factureService.generer(demande.getId());
        } catch (Exception e) {
            System.err.println("[DIWA Facture] Génération ignorée (livraison) : " + e.getMessage());
        }

        try {
            if (demande.getClient() != null) {
                notificationService.sendPushToClient(demande.getClient(),
                    "🚗 Votre véhicule est livré !",
                    "Votre " + demande.getVehiculeMarque() + " vous a été livré à domicile. Merci pour votre confiance.",
                    null);
                // Email de confirmation de livraison (sans facture — celle-ci est déjà envoyée par factureService)
                emailService.sendLivraisonConfirmee(demande.getClient().getEmail(),
                    demande.getClient().getPrenom(), demande.getVehiculeMarque(), demande.getVehiculeImmatriculation());
            }
        } catch (Exception e) {
            System.err.println("[DIWA] Notification livraison (ignorée): " + e.getMessage());
        }

        return missionMapper.toDTO(missionRepo.saveAndFlush(mission));
    }

    @Override
    public List<MissionDTOResponse> getParDemande(Long demandeId) {
        return missionRepo.findByDemandeIdOrderByCreateDateDesc(demandeId).stream()
                .map(missionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MissionDTOResponse getLivraisonForClient(Long demandeId, String identifier) {
        User user = userRepository.findByIdentifier(identifier)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + identifier));

        DemandeIntervention demande = demandeRepo.findById(demandeId)
            .orElseThrow(() -> new RuntimeException("Demande introuvable ID=" + demandeId));

        // Vérification d'appartenance : seul le client du dossier peut accéder
        if (!demande.getClient().getId().equals(user.getId())) {
            throw new SecurityException("Accès non autorisé à cette demande");
        }

        return missionRepo.findByDemandeIdOrderByCreateDateDesc(demandeId)
            .stream()
            .filter(m -> m.getType() == MissionChauffeur.TypeMission.LIVRAISON)
            .findFirst()
            .map(missionMapper::toDTO)
            .orElseThrow(() -> new RuntimeException("Aucune mission de livraison pour la demande ID=" + demandeId));
    }
}
