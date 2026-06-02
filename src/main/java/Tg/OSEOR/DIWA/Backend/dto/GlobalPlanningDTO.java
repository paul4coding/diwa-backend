package Tg.OSEOR.DIWA.Backend.dto;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.RendezVousDTO.RendezVousDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.TicketSAVDTO.TicketSAVDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.DemandeDTO.DemandeDTOResponse;

public class GlobalPlanningDTO {
    private List<RendezVousDTOResponse> rendezVous;
    private List<TicketSAVDTOResponse> interventions;
    private List<DemandeDTOResponse> visitesExperts;

    public GlobalPlanningDTO() {}

    public GlobalPlanningDTO(List<RendezVousDTOResponse> rendezVous, 
                            List<TicketSAVDTOResponse> interventions,
                            List<DemandeDTOResponse> visitesExperts) {
        this.rendezVous = rendezVous;
        this.interventions = interventions;
        this.visitesExperts = visitesExperts;
    }

    public List<RendezVousDTOResponse> getRendezVous() { return rendezVous; }
    public void setRendezVous(List<RendezVousDTOResponse> rendezVous) { this.rendezVous = rendezVous; }

    public List<TicketSAVDTOResponse> getInterventions() { return interventions; }
    public void setInterventions(List<TicketSAVDTOResponse> interventions) { this.interventions = interventions; }

    public List<DemandeDTOResponse> getVisitesExperts() { return visitesExperts; }
    public void setVisitesExperts(List<DemandeDTOResponse> visitesExperts) { this.visitesExperts = visitesExperts; }
}
