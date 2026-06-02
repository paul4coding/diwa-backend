package Tg.OSEOR.DIWA.Backend.mappers;

import Tg.OSEOR.DIWA.Backend.dto.ProFormaDTO.ProFormaDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.LigneProFormaMainOeuvre;
import Tg.OSEOR.DIWA.Backend.entity.LigneProFormaTravail;
import Tg.OSEOR.DIWA.Backend.entity.ProForma;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProFormaMapper {

    public ProFormaDTOResponse toDTO(ProForma p) {
        if (p == null) return null;
        ProFormaDTOResponse dto = new ProFormaDTOResponse();
        dto.setId(p.getId());
        dto.setReference(p.getReference());
        dto.setStatut(p.getStatut() != null ? p.getStatut().name() : null);
        dto.setFraisDiagnostic(p.getFraisDiagnostic());
        dto.setDiagnosticGratuit(p.getDiagnosticGratuit());
        dto.setMotifGratuite(p.getMotifGratuite());
        dto.setFraisRecuperation(p.getFraisRecuperation());
        dto.setFraisLivraison(p.getFraisLivraison());
        dto.setTotalPieces(p.getTotalPieces());
        dto.setTotalMainOeuvre(p.getTotalMainOeuvre());
        dto.setTotalFraisAnnexes(p.getTotalFraisAnnexes());
        dto.setTotalGeneral(p.getTotalGeneral());
        dto.setCouponCode(p.getCouponCode());
        dto.setMontantRemise(p.getMontantRemise());
        dto.setTotalApresRemise(p.getTotalApresRemise());
        dto.setDatePrevRestitution(p.getDatePrevRestitution());
        dto.setDureeTotaleMinutes(p.getDureeTotaleMinutes());
        dto.setNoteInterne(p.getNoteInterne());
        dto.setPdfUrlClient(p.getPdfUrlClient());
        dto.setPdfUrlTechnicien(p.getPdfUrlTechnicien());
        dto.setPdfUrlReceptionniste(p.getPdfUrlReceptionniste());

        if (p.getChefTechnicien() != null) {
            dto.setChefTechnicienNom(p.getChefTechnicien().getPrenom() + " " + p.getChefTechnicien().getNom());
        }
        if (p.getReceptionniste() != null) {
            dto.setReceptionnisteNom(p.getReceptionniste().getPrenom() + " " + p.getReceptionniste().getNom());
        }

        if (p.getLignesTravaux() != null) {
            dto.setLignesTravaux(p.getLignesTravaux().stream().map(this::toLigneTravailDTO).collect(Collectors.toList()));
        }
        if (p.getLignesMainOeuvre() != null) {
            dto.setLignesMainOeuvre(p.getLignesMainOeuvre().stream().map(this::toLigneMainOeuvreDTO).collect(Collectors.toList()));
        }

        if (p.getDemande() != null) {
            dto.setDemandeId(p.getDemande().getId());
            dto.setDemandeUuid(p.getDemande().getUuid());
            if (p.getDemande().getClient() != null) {
                dto.setClientNom(p.getDemande().getClient().getPrenom() + " " + p.getDemande().getClient().getNom());
            }
            dto.setVehiculeMarque(p.getDemande().getVehiculeMarque());
            dto.setVehiculeModele(p.getDemande().getVehiculeModele());
            dto.setVehiculeImmatriculation(p.getDemande().getVehiculeImmatriculation());
            dto.setDemandeLivraison(p.getDemande().getDemandeLivraison());
            dto.setAdresseLivraison(p.getDemande().getAdresseLivraison());
            dto.setDemandeStatut(p.getDemande().getStatut() != null ? p.getDemande().getStatut().name() : null);
        }

        return dto;
    }

    public ProFormaDTOResponse toDTOTechnicien(ProForma p) {
        ProFormaDTOResponse dto = toDTO(p);
        dto.setFraisDiagnostic(null);
        dto.setFraisRecuperation(null);
        dto.setFraisLivraison(null);
        dto.setTotalPieces(null);
        dto.setTotalMainOeuvre(null);
        dto.setTotalFraisAnnexes(null);
        dto.setTotalGeneral(null);
        if (dto.getLignesTravaux() != null) {
            dto.getLignesTravaux().forEach(l -> {
                l.setPrixUnitaire(null);
                l.setPrixTotal(null);
                l.setPrixMinConseille(null);
                l.setPrixMaxConseille(null);
            });
        }
        if (dto.getLignesMainOeuvre() != null) {
            dto.getLignesMainOeuvre().forEach(l -> {
                l.setTauxHoraire(null);
                l.setTotal(null);
            });
        }
        return dto;
    }

    public ProFormaDTOResponse toDTOClient(ProForma p) {
        ProFormaDTOResponse dto = toDTO(p);
        if (dto.getLignesMainOeuvre() != null) {
            dto.getLignesMainOeuvre().forEach(l -> l.setHeures(null));
        }
        dto.setNoteInterne(null);
        return dto;
    }

    private ProFormaDTOResponse.LigneTravailDTO toLigneTravailDTO(LigneProFormaTravail l) {
        ProFormaDTOResponse.LigneTravailDTO dto = new ProFormaDTOResponse.LigneTravailDTO();
        dto.setId(l.getId());
        dto.setPosition(l.getPosition());
        dto.setDesignation(l.getDesignation());
        dto.setPieceId(l.getPieceDetachee() != null ? l.getPieceDetachee().getId() : null);
        dto.setMarquePiece(l.getMarquePiece());
        dto.setModelePiece(l.getModelePiece());
        dto.setReferencePieceLibre(l.getReferencePieceLibre());
        dto.setQuantite(l.getQuantite());
        dto.setPrixUnitaire(l.getPrixUnitaire());
        dto.setPrixTotal(l.getPrixTotal());
        dto.setPrixMinConseille(l.getPrixMinConseille());
        dto.setPrixMaxConseille(l.getPrixMaxConseille());
        dto.setCocheeParClient(l.getCocheeParClient());
        dto.setStatut(l.getStatut() != null ? l.getStatut().name() : null);
        return dto;
    }

    private ProFormaDTOResponse.LigneMainOeuvreDTO toLigneMainOeuvreDTO(LigneProFormaMainOeuvre l) {
        ProFormaDTOResponse.LigneMainOeuvreDTO dto = new ProFormaDTOResponse.LigneMainOeuvreDTO();
        dto.setId(l.getId());
        dto.setPosition(l.getPosition());
        dto.setTypeIntervention(l.getTypeIntervention());
        dto.setHeures(l.getHeures());
        dto.setDureeMinutes(l.getDureeMinutes());
        dto.setCommentaireTechnicien(l.getCommentaireTechnicien());
        dto.setTauxHoraire(l.getTauxHoraire());
        dto.setTotal(l.getTotal());
        dto.setCocheeParClient(l.getCocheeParClient());
        dto.setLigneTravailId(l.getLigneTravailId());
        return dto;
    }
}
