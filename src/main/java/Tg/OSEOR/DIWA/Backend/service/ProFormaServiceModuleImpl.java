package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.*;
import Tg.OSEOR.DIWA.Backend.dto.AtelierDTO.AffectationDTO;
import Tg.OSEOR.DIWA.Backend.entity.*;
import Tg.OSEOR.DIWA.Backend.mappers.ProFormaMapper;
import Tg.OSEOR.DIWA.Backend.repository.*;
import Tg.OSEOR.DIWA.Backend.repository.atelier.GammePrixRepository;
import Tg.OSEOR.DIWA.Backend.repository.atelier.HistoriqueStatutDemandeRepository;
import Tg.OSEOR.DIWA.Backend.service.document.DocumentGenerationService;
import Tg.OSEOR.DIWA.Backend.service.media.FileStorageService;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProFormaServiceModuleImpl implements ProFormaServiceModule {

    private final ProFormaRepository proFormaRepository;
    private final DemandeInterventionRepository demandeRepository;
    private final LigneProFormaTravailRepository ligneTravauxRepository;
    private final LigneProFormaMainOeuvreRepository ligneMainOeuvreRepository;
    private final GammePrixRepository gammePrixRepository;
    private final PieceDetacheeRepository pieceDetacheeRepository;
    private final ParametreAtelierRepository parametreRepo;
    private final UserRepository userRepository;
    private final ProFormaMapper proFormaMapper;
    private final DocumentGenerationService documentGenerationService;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;
    private final HistoriqueStatutDemandeRepository historiqueRepository;
    private final AffectationTravailRepository affectationTravailRepository;
    private final TechnicienRepository technicienRepository;

    public ProFormaServiceModuleImpl(ProFormaRepository proFormaRepository,
                                      DemandeInterventionRepository demandeRepository,
                                      LigneProFormaTravailRepository ligneTravauxRepository,
                                      LigneProFormaMainOeuvreRepository ligneMainOeuvreRepository,
                                      GammePrixRepository gammePrixRepository,
                                      PieceDetacheeRepository pieceDetacheeRepository,
                                      ParametreAtelierRepository parametreRepo,
                                      UserRepository userRepository,
                                      ProFormaMapper proFormaMapper,
                                      DocumentGenerationService documentGenerationService,
                                      FileStorageService fileStorageService,
                                      EmailService emailService,
                                      HistoriqueStatutDemandeRepository historiqueRepository,
                                      AffectationTravailRepository affectationTravailRepository,
                                      TechnicienRepository technicienRepository) {
        this.proFormaRepository = proFormaRepository;
        this.demandeRepository = demandeRepository;
        this.ligneTravauxRepository = ligneTravauxRepository;
        this.ligneMainOeuvreRepository = ligneMainOeuvreRepository;
        this.gammePrixRepository = gammePrixRepository;
        this.pieceDetacheeRepository = pieceDetacheeRepository;
        this.parametreRepo = parametreRepo;
        this.userRepository = userRepository;
        this.proFormaMapper = proFormaMapper;
        this.documentGenerationService = documentGenerationService;
        this.fileStorageService = fileStorageService;
        this.emailService = emailService;
        this.historiqueRepository = historiqueRepository;
        this.affectationTravailRepository = affectationTravailRepository;
        this.technicienRepository = technicienRepository;
    }

    @Override
    @Transactional
    public ProFormaDTOResponse creerV1(String identifier, ProFormaV1Request request, String chefTechEmail) {
        System.out.println("[DIWA] Début creerV1 pour: " + identifier);
        try {
            // 1. Résolution de la demande
            DemandeIntervention demande;
            if (identifier != null && identifier.matches("^\\d+$")) {
                demande = demandeRepository.findById(Long.parseLong(identifier))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (id)"));
            } else {
                demande = demandeRepository.findByUuid(identifier)
                        .or(() -> demandeRepository.findByReference(identifier))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable (uuid/ref)"));
            }
            System.out.println("[DIWA] Demande trouvée: " + demande.getReference());

            // 2. Gestion de l'existence
            Optional<ProForma> existingPF = proFormaRepository.findByDemandeId(demande.getId());
            
            if (demande.getStatut() == DemandeIntervention.StatutDemande.SOUMISE || 
                demande.getStatut() == DemandeIntervention.StatutDemande.CLOTURE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le statut actuel (" + demande.getStatut() + ") ne permet pas la création d'un Pro Forma.");
            }

            ProForma proForma;
            if (existingPF.isPresent()) {
                proForma = existingPF.get();
                System.out.println("[DIWA] Mise à jour Pro Forma existante: " + proForma.getReference());
                if (proForma.getStatut() != ProForma.StatutProForma.BROUILLON && 
                    proForma.getStatut() != ProForma.StatutProForma.SOUMIS_RECEPTIONNISTE) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Un Pro Forma validé existe déjà.");
                }
                
                // Suppression explicite des anciennes lignes pour éviter les conflits Hibernate
                ligneTravauxRepository.deleteAll(proForma.getLignesTravaux());
                ligneMainOeuvreRepository.deleteAll(proForma.getLignesMainOeuvre());
                proForma.getLignesTravaux().clear();
                proForma.getLignesMainOeuvre().clear();
                // On force un petit flush pour que les suppressions soient effectives avant les réinsertions
                proFormaRepository.saveAndFlush(proForma);
            } else {
                proForma = new ProForma();
                proForma.setDemande(demande);
                proForma.setReference(generateReference());
                System.out.println("[DIWA] Création nouvelle Pro Forma: " + proForma.getReference());
            }

            // 3. Chef Tech
            User chefTech = userRepository.findByIdentifier(chefTechEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chef technicien introuvable"));
            proForma.setChefTechnicien(chefTech);
            proForma.setStatut(ProForma.StatutProForma.BROUILLON);

            // 4. Lignes Travaux
            // On conserve la liste des requêtes en parallèle pour récupérer les MO associées
            // après le premier saveAndFlush (nécessaire pour avoir les IDs des travaux).
            AtomicInteger pos = new AtomicInteger(1);
            List<ProFormaV1Request.LigneTravailRequest> travailRequests =
                    request.getLignesTravaux() != null ? request.getLignesTravaux() : new ArrayList<>();
            for (ProFormaV1Request.LigneTravailRequest lt : travailRequests) {
                LigneProFormaTravail ligne = new LigneProFormaTravail();
                ligne.setProForma(proForma);
                ligne.setPosition(pos.getAndIncrement());
                ligne.setDesignation(lt.getDesignation() != null ? lt.getDesignation() : "Intervention technique");
                ligne.setQuantite(lt.getQuantite() != null ? lt.getQuantite() : BigDecimal.ONE);
                ligne.setMarquePiece(lt.getMarquePiece());
                ligne.setModelePiece(lt.getModelePiece());
                ligne.setReferencePieceLibre(lt.getReferencePieceLibre());
                if (lt.getPieceDetacheeId() != null) {
                    ligne.setPieceDetachee(pieceDetacheeRepository.findById(lt.getPieceDetacheeId()).orElse(null));
                }
                ligne.setCocheeParClient(true);
                ligne.setStatut(LigneProFormaTravail.StatutLigne.NON_DEBUTE);
                proForma.getLignesTravaux().add(ligne);
            }

            // 5a. Lignes Main d'Oeuvre autonomes (non liées à une pièce)
            AtomicInteger posMO = new AtomicInteger(1);
            AtomicInteger totalMinutes = new AtomicInteger(0);
            if (request.getLignesMainOeuvre() != null) {
                for (ProFormaV1Request.LigneMainOeuvreRequest lm : request.getLignesMainOeuvre()) {
                    LigneProFormaMainOeuvre ligne = new LigneProFormaMainOeuvre();
                    ligne.setProForma(proForma);
                    ligne.setPosition(posMO.getAndIncrement());
                    ligne.setTypeIntervention(lm.getTypeIntervention() != null ? lm.getTypeIntervention() : "Main d'œuvre");
                    ligne.setCommentaireTechnicien(lm.getCommentaireTechnicien());
                    ligne.setCocheeParClient(true);
                    // Priorité à dureeMinutes, fallback sur heures (ancienne API)
                    if (lm.getDureeMinutes() != null && lm.getDureeMinutes() > 0) {
                        ligne.setDureeMinutes(lm.getDureeMinutes());
                        totalMinutes.addAndGet(lm.getDureeMinutes());
                    } else {
                        BigDecimal h = lm.getHeures() != null ? lm.getHeures() : BigDecimal.ZERO;
                        if (h.compareTo(new BigDecimal("999")) > 0) h = new BigDecimal("999");
                        ligne.setHeures(h);
                        totalMinutes.addAndGet(h.multiply(new BigDecimal("60")).intValue());
                    }
                    proForma.getLignesMainOeuvre().add(ligne);
                }
            }

            proForma.setStatut(ProForma.StatutProForma.SOUMIS_RECEPTIONNISTE);

            System.out.println("[DIWA] Sauvegarde Pro Forma (1ère passe)...");
            ProForma saved = proFormaRepository.saveAndFlush(proForma);
            System.out.println("[DIWA] Pro Forma sauvegardée ID: " + saved.getId());

            // 5b. MO liées aux pièces — créées après saveAndFlush pour avoir les IDs des travaux.
            // On requête directement pour avoir les entités avec IDs dans l'ordre position ASC.
            List<LigneProFormaTravail> savedTravaux =
                    ligneTravauxRepository.findByProFormaIdOrderByPositionAsc(saved.getId());
            for (int i = 0; i < travailRequests.size(); i++) {
                ProFormaV1Request.LigneTravailRequest req = travailRequests.get(i);
                if (req.getMainOeuvreAssociee() == null) continue;
                if (i >= savedTravaux.size()) break;

                ProFormaV1Request.LigneMainOeuvreAssocieeRequest moReq = req.getMainOeuvreAssociee();
                LigneProFormaTravail savedTravail = savedTravaux.get(i);

                LigneProFormaMainOeuvre moLiee = new LigneProFormaMainOeuvre();
                moLiee.setProForma(saved);
                moLiee.setPosition(posMO.getAndIncrement());
                moLiee.setTypeIntervention(moReq.getTypeIntervention() != null
                        ? moReq.getTypeIntervention()
                        : "Pose — " + req.getDesignation());
                moLiee.setCommentaireTechnicien(moReq.getCommentaireTechnicien());
                moLiee.setCocheeParClient(true);
                moLiee.setLigneTravailId(savedTravail.getId());

                int dm = moReq.getDureeMinutes() != null ? moReq.getDureeMinutes() : 0;
                moLiee.setDureeMinutes(dm);
                totalMinutes.addAndGet(dm);

                ligneMainOeuvreRepository.save(moLiee);
            }

            saved.setDureeTotaleMinutes(totalMinutes.get());
            proFormaRepository.save(saved);
            // 6. Demande
            DemandeIntervention.StatutDemande ancien = demande.getStatut();
            demande.setStatut(DemandeIntervention.StatutDemande.PROFORMA_V1);
            demandeRepository.save(demande);

            ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.PROFORMA_V1, chefTech, "Génération du Pro Forma Technique (V1)");
            System.out.println("[DIWA] Historique ajouté, fin creerV1.");

            return proFormaMapper.toDTO(saved);
        } catch (ResponseStatusException e) {
            System.err.println("[DIWA] Erreur métier: " + e.getReason());
            throw e;
        } catch (Exception e) {
            System.err.println("[DIWA] Erreur CRITIQUE: ");
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur [ " + e.getClass().getSimpleName() + " ]: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<ProFormaDTOResponse> getAwaitingAssignment() {
        return proFormaRepository.findByStatut(ProForma.StatutProForma.VALIDE_FINAL).stream()
                .filter(pf -> affectationTravailRepository.findByProFormaId(pf.getId()).isEmpty())
                .map(proFormaMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProFormaDTOResponse getLignesTechnicien(Long proFormaId) {
        ProForma pf = proFormaRepository.findById(proFormaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pro forma introuvable"));
        return proFormaMapper.toDTOTechnicien(pf);
    }

    @Override
    @Transactional(readOnly = true)
    public ProFormaDTOResponse getVersionReceptionniste(Long proFormaId) {
        ProForma pf = proFormaRepository.findById(proFormaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pro forma introuvable"));
        return proFormaMapper.toDTO(pf);
    }

    @Override
    @Transactional
    public ProFormaDTOResponse ajouterPrix(Long proFormaId, AjoutPrixRequest request, String identifier) {
        ProForma proForma = proFormaRepository.findById(proFormaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pro forma introuvable"));
        
        User receptionniste = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réceptionniste introuvable"));

        ParametreAtelier params = parametreRepo.findParametre().orElse(null);
        BigDecimal diagnosticDefaut = (params != null) ? params.getFraisDiagnosticStandard() : new BigDecimal("25000");
        BigDecimal tauxDefault = (params != null) ? params.getTauxHoraireDefaut() : new BigDecimal("15000");

        DemandeIntervention demande = proForma.getDemande();

        // 1. Frais de diagnostic
        if (Boolean.TRUE.equals(demande.getDiagnosticGratuit())) {
            proForma.setFraisDiagnostic(BigDecimal.ZERO);
        } else {
            proForma.setFraisDiagnostic(request.getFraisDiagnostic() != null ? request.getFraisDiagnostic() : diagnosticDefaut);
        }

        // 2. Frais de logistique
        if (request.getFraisRecuperation() != null) proForma.setFraisRecuperation(request.getFraisRecuperation());
        if (request.getFraisLivraison() != null) proForma.setFraisLivraison(request.getFraisLivraison());

        // 3. Pièces
        if (request.getLignesTravaux() != null) {
            for (AjoutPrixRequest.LignePrixDTO lp : request.getLignesTravaux()) {
                proForma.getLignesTravaux().stream()
                        .filter(l -> l.getId().equals(lp.getLigneId()))
                        .findFirst()
                        .ifPresent(l -> l.setPrixUnitaire(lp.getPrixUnitaire()));
            }
        }

        // 4. Main d'œuvre
        if (request.getLignesMainOeuvre() != null) {
            for (AjoutPrixRequest.LigneMOPrixDTO lmoReq : request.getLignesMainOeuvre()) {
                proForma.getLignesMainOeuvre().stream()
                        .filter(l -> l.getId().equals(lmoReq.getLigneId()))
                        .findFirst()
                        .ifPresent(lmo -> lmo.setTauxHoraire(lmoReq.getTauxHoraire() != null ? lmoReq.getTauxHoraire() : tauxDefault));
            }
        }

        // 5. Finalisation
        Integer minutes = proForma.getDureeTotaleMinutes() != null ? proForma.getDureeTotaleMinutes() : 0;
        proForma.setDatePrevRestitution(LocalDateTime.now().plusMinutes(minutes + 120));
        recalculerTotaux(proForma);
        proForma.setReceptionniste(receptionniste);
        proForma.setStatut(ProForma.StatutProForma.PRIX_AJOUTES);
        
        // Faire avancer le statut de la demande
        demande.setStatut(DemandeIntervention.StatutDemande.EN_ATTENTE_CLIENT);
        demandeRepository.save(demande);
        
        ProForma saved = proFormaRepository.save(proForma);
        ajouterHistorique(demande, demande.getStatut(), DemandeIntervention.StatutDemande.EN_ATTENTE_CLIENT, receptionniste, "Prix ajoutés - En attente validation client");

        return proFormaMapper.toDTO(saved);
    }

    @Override
    public ProFormaDTOResponse envoyerAuClient(Long proFormaId, String receptionnisteEmail) {
        ProForma pf = proFormaRepository.findById(proFormaId).orElseThrow();
        pf.setStatut(ProForma.StatutProForma.ENVOYE_CLIENT);
        proFormaRepository.save(pf);

        DemandeIntervention demande = pf.getDemande();
        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setStatut(DemandeIntervention.StatutDemande.EN_ATTENTE_CLIENT);
        demandeRepository.save(demande);

        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.EN_ATTENTE_CLIENT, pf.getReceptionniste(), "Pro Forma envoyé pour validation client");

        // Notification par email ici possible
        return proFormaMapper.toDTO(pf);
    }

    @Override
    @Transactional(readOnly = true)
    public ProFormaDTOResponse getVersionClient(Long proFormaId, String userEmail) {
        ProForma pf = proFormaRepository.findById(proFormaId).orElseThrow();
        return proFormaMapper.toDTOClient(pf);
    }

    @Override
    public ProFormaDTOResponse soumettreSelection(Long proFormaId, SelectionClientRequest request, String userEmail) {
        ProForma pf = proFormaRepository.findById(proFormaId).orElseThrow();
        
        if (request.getLignesTravaux() != null) {
            request.getLignesTravaux().forEach(s -> {
                LigneProFormaTravail l = ligneTravauxRepository.findById(s.getLigneId()).orElseThrow();
                l.setCocheeParClient(s.getCochee());
                ligneTravauxRepository.save(l);
            });
        }
        if (request.getLignesMainOeuvre() != null) {
            request.getLignesMainOeuvre().forEach(s -> {
                LigneProFormaMainOeuvre l = ligneMainOeuvreRepository.findById(s.getLigneId()).orElseThrow();
                l.setCocheeParClient(s.getCochee());
                ligneMainOeuvreRepository.save(l);
            });
        }

        if (request.getDemandeLivraison() != null) {
            pf.getDemande().setDemandeLivraison(request.getDemandeLivraison());
            pf.getDemande().setAdresseLivraison(request.getAdresseLivraison());
            // Frais livraison à fixer par réceptionniste ou auto
        }

        recalculerTotauxSelection(pf);
        pf.setStatut(ProForma.StatutProForma.SELECTION_RECUE);
        proFormaRepository.save(pf);
        
        pf.getDemande().setStatut(DemandeIntervention.StatutDemande.SELECTION_RECUE);
        demandeRepository.save(pf.getDemande());

        return proFormaMapper.toDTO(pf);
    }

    @Override
    public ProFormaDTOResponse validerSelectionClient(Long proFormaId, String receptionnisteEmail) {
        ProForma pf = proFormaRepository.findById(proFormaId).orElseThrow();
        DemandeIntervention demande = pf.getDemande();

        // If client requested delivery but no fee set → ask receptionist to add fee first
        if (Boolean.TRUE.equals(demande.getDemandeLivraison()) &&
                (pf.getFraisLivraison() == null || pf.getFraisLivraison().compareTo(BigDecimal.ZERO) == 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Le client a demandé la livraison à domicile. Veuillez d'abord renseigner les frais de livraison via /mettre-a-jour-livraison.");
        }

        User receptionniste = userRepository.findByIdentifier(receptionnisteEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réceptionniste introuvable"));

        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        pf.setStatut(ProForma.StatutProForma.VALIDE_FINAL);
        proFormaRepository.save(pf);

        demande.setStatut(DemandeIntervention.StatutDemande.PROFORMA_VALIDE);
        demandeRepository.save(demande);
        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.PROFORMA_VALIDE, receptionniste, "Sélection client validée par la réception");

        return proFormaMapper.toDTO(pf);
    }

    @Override
    public ProFormaDTOResponse mettreAJourLivraison(Long proFormaId, BigDecimal fraisLivraison, String receptionnisteEmail) {
        ProForma pf = proFormaRepository.findById(proFormaId).orElseThrow();
        DemandeIntervention demande = pf.getDemande();

        if (!Boolean.TRUE.equals(demande.getDemandeLivraison())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce dossier ne demande pas de livraison à domicile.");
        }

        User receptionniste = userRepository.findByIdentifier(receptionnisteEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réceptionniste introuvable"));

        pf.setFraisLivraison(fraisLivraison);
        demande.setFraisLivraison(fraisLivraison);

        // Recompute totals without touching existing totalPieces/totalMO (already set during selection)
        BigDecimal pieces = pf.getTotalPieces() != null ? pf.getTotalPieces() : BigDecimal.ZERO;
        BigDecimal mo = pf.getTotalMainOeuvre() != null ? pf.getTotalMainOeuvre() : BigDecimal.ZERO;
        BigDecimal annexes = (pf.getFraisDiagnostic() != null ? pf.getFraisDiagnostic() : BigDecimal.ZERO)
                .add(pf.getFraisRecuperation() != null ? pf.getFraisRecuperation() : BigDecimal.ZERO)
                .add(fraisLivraison);
        pf.setTotalFraisAnnexes(annexes);
        pf.setTotalGeneral(pieces.add(mo).add(annexes));

        proFormaRepository.save(pf);

        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setStatut(DemandeIntervention.StatutDemande.EN_ATTENTE_CONFIRMATION_FINALE);
        demandeRepository.save(demande);
        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.EN_ATTENTE_CONFIRMATION_FINALE, receptionniste,
            "Frais de livraison ajoutés : " + fraisLivraison + " FCFA — En attente de confirmation finale du client");

        // Notify client
        try {
            if (demande.getClient() != null) {
                emailService.sendLivraisonFraisAjoutes(demande.getClient().getEmail(), demande.getClient().getPrenom(),
                    demande.getVehiculeMarque(), demande.getVehiculeImmatriculation(), fraisLivraison, pf.getTotalGeneral());
            }
        } catch (Exception e) {
            System.err.println("[DIWA] Email notification failed (ignored): " + e.getMessage());
        }

        return proFormaMapper.toDTO(pf);
    }

    @Override
    public ProFormaDTOResponse confirmerFinal(Long proFormaId, String userEmail) {
        ProForma pf = proFormaRepository.findById(proFormaId).orElseThrow();
        DemandeIntervention demande = pf.getDemande();

        if (demande.getStatut() != DemandeIntervention.StatutDemande.EN_ATTENTE_CONFIRMATION_FINALE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Ce pro forma n'est pas en attente de confirmation finale. Statut actuel : " + demande.getStatut());
        }

        User client = userRepository.findByIdentifier(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        pf.setStatut(ProForma.StatutProForma.VALIDE_FINAL);
        proFormaRepository.save(pf);

        demande.setStatut(DemandeIntervention.StatutDemande.PROFORMA_VALIDE);
        demandeRepository.save(demande);
        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.PROFORMA_VALIDE, client,
            "Confirmation finale du client avec frais de livraison inclus");

        return proFormaMapper.toDTO(pf);
    }

    @Override
    public ProFormaDTOResponse confirmerProForma(Long proFormaId, String userEmail) {
        ProForma pf = proFormaRepository.findById(proFormaId).orElseThrow();
        
        // Générer les 3 PDFs
        byte[] pdfClient = genererPdfClient(proFormaId);
        byte[] pdfTech = genererPdfTechnicien(proFormaId);
        byte[] pdfRecep = genererPdfReceptionniste(proFormaId);

        pf.setPdfUrlClient(fileStorageService.storePdf(pdfClient, "PF_CLIENT_" + pf.getReference() + ".pdf"));
        pf.setPdfUrlTechnicien(fileStorageService.storePdf(pdfTech, "PF_TECH_" + pf.getReference() + ".pdf"));
        pf.setPdfUrlReceptionniste(fileStorageService.storePdf(pdfRecep, "PF_RECEP_" + pf.getReference() + ".pdf"));

        // Décrémenter stock
        pf.getLignesTravaux().stream()
                .filter(l -> Boolean.TRUE.equals(l.getCocheeParClient()) && l.getPieceDetachee() != null)
                .forEach(l -> {
                    PieceDetachee p = l.getPieceDetachee();
                    p.setQuantiteStock(p.getQuantiteStock() - l.getQuantite().intValue());
                    pieceDetacheeRepository.save(p);
                });

        pf.setStatut(ProForma.StatutProForma.CONFIRME_CLIENT);
        proFormaRepository.save(pf);

        DemandeIntervention demandePF = pf.getDemande();
        DemandeIntervention.StatutDemande ancienPF = demandePF.getStatut();
        demandePF.setStatut(DemandeIntervention.StatutDemande.CONFIRME);
        demandeRepository.save(demandePF);

        ajouterHistorique(demandePF, ancienPF, DemandeIntervention.StatutDemande.CONFIRME, demandePF.getClient(), "Pro Forma confirmé par le client — En attente de traitement par la réception");

        return proFormaMapper.toDTO(pf);
    }

    @Override
    public void updateStatutLigne(Long ligneId, String nouveauStatut, String techEmail) {
        LigneProFormaTravail ligne = ligneTravauxRepository.findById(ligneId).orElseThrow();
        ligne.setStatut(LigneProFormaTravail.StatutLigne.valueOf(nouveauStatut));
        ligneTravauxRepository.save(ligne);

        ProForma pf = ligne.getProForma();
        boolean toutTermine = pf.getLignesTravaux().stream()
                .filter(l -> Boolean.TRUE.equals(l.getCocheeParClient()))
                .allMatch(l -> l.getStatut() == LigneProFormaTravail.StatutLigne.TERMINE);

        if (toutTermine) {
            DemandeIntervention d = pf.getDemande();
            d.setStatut(DemandeIntervention.StatutDemande.PRET);
            demandeRepository.save(d);
            // Email auto client "Prêt" via emailService
            emailService.sendVehiculePret(d.getClient().getEmail(), d.getClient().getPrenom(), d.getVehiculeMarque(), d.getVehiculeImmatriculation());
        }
    }

    @Override
    public byte[] genererPdfClient(Long proFormaId) {
        return documentGenerationService.genererPdf(proFormaRepository.findById(proFormaId).orElseThrow(), "CLIENT");
    }

    @Override
    public byte[] genererPdfTechnicien(Long proFormaId) {
        return documentGenerationService.genererPdf(proFormaRepository.findById(proFormaId).orElseThrow(), "TECHNICIEN");
    }

    @Override
    public byte[] genererPdfReceptionniste(Long proFormaId) {
        return documentGenerationService.genererPdf(proFormaRepository.findById(proFormaId).orElseThrow(), "RECEPTIONNISTE");
    }

    @Override
    public byte[] genererExcel(Long proFormaId) {
        return documentGenerationService.genererExcel(proFormaRepository.findById(proFormaId).orElseThrow());
    }

    @Override
    @Transactional
    public void affecterTravail(Long proFormaId, Long technicienId, LocalDateTime dateDebut, LocalDateTime dateFinPrevue, String notes, String chefTechEmail) {
        ProForma pf = proFormaRepository.findById(proFormaId).orElseThrow();
        Technicien tech = technicienRepository.findById(technicienId).orElseThrow();
        User chefTech = userRepository.findByIdentifier(chefTechEmail).orElseThrow();

        AffectationTravail aff = new AffectationTravail();
        aff.setProForma(pf);
        aff.setDemande(pf.getDemande());
        aff.setTechnicien(tech);
        aff.setDateDebut(dateDebut);
        aff.setDateFinPrevue(dateFinPrevue);
        aff.setNotes(notes);
        aff.setStatut(AffectationTravail.StatutAffectation.EN_COURS);
        affectationTravailRepository.save(aff);

        // Mise à jour de la demande
        DemandeIntervention demande = pf.getDemande();
        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setStatut(DemandeIntervention.StatutDemande.EN_COURS_REPARATION);
        demandeRepository.save(demande);

        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.EN_COURS_REPARATION, chefTech, 
            "Travaux affectés à " + tech.getPrenom() + " " + tech.getNom() + ". Fin prévue : " + dateFinPrevue);

        // Alerter le client
        emailService.sendReparationEnCours(demande, dateFinPrevue);
    }

    @Override
    @Transactional
    public void modifierAffectation(Long proFormaId, Long technicienId, LocalDateTime dateDebut, LocalDateTime dateFinPrevue, String notes, String chefTechEmail) {
        AffectationTravail aff = affectationTravailRepository.findByProFormaId(proFormaId).stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Affectation introuvable"));
        
        Technicien tech = technicienRepository.findById(technicienId).orElseThrow();
        User chefTech = userRepository.findByIdentifier(chefTechEmail).orElseThrow();

        aff.setTechnicien(tech);
        aff.setDateDebut(dateDebut);
        aff.setDateFinPrevue(dateFinPrevue);
        aff.setNotes(notes);
        affectationTravailRepository.save(aff);

        DemandeIntervention demande = aff.getDemande();
        ajouterHistorique(demande, demande.getStatut(), demande.getStatut(), chefTech, 
            "Planning mis à jour. Nouvelle fin prévue : " + dateFinPrevue + " (Expert: " + tech.getPrenom() + ")");

        // Alerter le client du changement
        emailService.sendPlanningUpdate(demande, dateFinPrevue);
    }

    @Override
    @Transactional
    public void confirmerFinTravaux(Long proFormaId, String chefTechEmail) {
        ProForma pf = proFormaRepository.findById(proFormaId).orElseThrow();
        User chefTech = userRepository.findByIdentifier(chefTechEmail).orElseThrow();

        // Marquer les affectations comme terminées
        List<AffectationTravail> affs = affectationTravailRepository.findByProFormaId(proFormaId);
        for (AffectationTravail a : affs) {
            if (a.getStatut() != AffectationTravail.StatutAffectation.TERMINE) {
                a.setStatut(AffectationTravail.StatutAffectation.TERMINE);
                a.setDateFinReelle(LocalDateTime.now());
                affectationTravailRepository.save(a);
            }
        }

        // Mise à jour de la demande
        DemandeIntervention demande = pf.getDemande();
        DemandeIntervention.StatutDemande ancien = demande.getStatut();
        demande.setStatut(DemandeIntervention.StatutDemande.PRET);
        demandeRepository.save(demande);

        ajouterHistorique(demande, ancien, DemandeIntervention.StatutDemande.PRET, chefTech, "Travaux terminés - Véhicule prêt");

        // Alerter le client
        emailService.sendVehiculePret(demande);
    }

    private void recalculerTotaux(ProForma pf) {
        BigDecimal totalPieces = pf.getLignesTravaux().stream()
                .filter(l -> l.getPrixUnitaire() != null && l.getQuantite() != null)
                .map(l -> l.getPrixUnitaire().multiply(l.getQuantite()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pf.setTotalPieces(totalPieces);
        
        BigDecimal totalMO = pf.getLignesMainOeuvre().stream()
                .filter(l -> l.getTauxHoraire() != null && l.getHeures() != null)
                .map(l -> l.getTauxHoraire().multiply(l.getHeures()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pf.setTotalMainOeuvre(totalMO);
        
        BigDecimal annexes = (pf.getFraisDiagnostic() != null ? pf.getFraisDiagnostic() : BigDecimal.ZERO)
                .add(pf.getFraisRecuperation() != null ? pf.getFraisRecuperation() : BigDecimal.ZERO)
                .add(pf.getFraisLivraison() != null ? pf.getFraisLivraison() : BigDecimal.ZERO);
        
        pf.setTotalFraisAnnexes(annexes);
        pf.setTotalGeneral(totalPieces.add(totalMO).add(annexes));
    }

    private void recalculerTotauxSelection(ProForma pf) {
        BigDecimal totalPieces = pf.getLignesTravaux().stream()
                .filter(l -> Boolean.TRUE.equals(l.getCocheeParClient()) && l.getPrixTotal() != null)
                .map(LigneProFormaTravail::getPrixTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalMO = pf.getLignesMainOeuvre().stream()
                .filter(l -> Boolean.TRUE.equals(l.getCocheeParClient()) && l.getTotal() != null)
                .map(LigneProFormaMainOeuvre::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pf.setTotalPieces(totalPieces);
        pf.setTotalMainOeuvre(totalMO);
        
        BigDecimal annexes = (pf.getFraisDiagnostic() != null ? pf.getFraisDiagnostic() : BigDecimal.ZERO)
                .add(pf.getFraisRecuperation() != null ? pf.getFraisRecuperation() : BigDecimal.ZERO);
        
        if (Boolean.TRUE.equals(pf.getDemande().getDemandeLivraison()) && pf.getFraisLivraison() != null) {
            annexes = annexes.add(pf.getFraisLivraison());
        }
        
        pf.setTotalFraisAnnexes(annexes);
        pf.setTotalGeneral(totalPieces.add(totalMO).add(annexes));
    }

    @Override
    public List<AffectationDTO> getAllAffectations() {
        return affectationTravailRepository.findAll().stream().map(a -> {
            AffectationDTO dto = new AffectationDTO();
            dto.setId(a.getId());
            dto.setProFormaId(a.getProForma().getId());
            dto.setReferenceDossier(a.getDemande().getReference());
            dto.setVehicule(a.getDemande().getVehiculeMarque() + " " + a.getDemande().getVehiculeModele());
            dto.setTechnicienId(a.getTechnicien().getId());
            dto.setTechnicienNom(a.getTechnicien().getPrenom() + " " + a.getTechnicien().getNom());
            dto.setDateDebut(a.getDateDebut());
            dto.setDateFinPrevue(a.getDateFinPrevue());
            dto.setStatut(a.getStatut());
            dto.setNotes(a.getNotes());
            return dto;
        }).collect(Collectors.toList());
    }

    private String generateReference() {
        long count = proFormaRepository.count() + 1;
        String random = java.util.UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return String.format("PF-%d-%05d-%s", LocalDate.now().getYear(), count, random);
    }

    private void ajouterHistorique(DemandeIntervention demande, DemandeIntervention.StatutDemande ancien, DemandeIntervention.StatutDemande nouveau, User auteur, String comm) {
        HistoriqueStatutDemande h = new HistoriqueStatutDemande();
        h.setDemande(demande);
        h.setAncienStatut(ancien);
        h.setNouveauStatut(nouveau);
        h.setAuteur(auteur);
        h.setCommentaire(comm);
        historiqueRepository.save(h);
    }
}
