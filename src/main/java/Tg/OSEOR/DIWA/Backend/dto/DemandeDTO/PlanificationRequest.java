package Tg.OSEOR.DIWA.Backend.dto.DemandeDTO;

public class PlanificationRequest {
    private String dateRecuperation;
    private Long creneauId;

    public PlanificationRequest() {}

    public String getDateRecuperation() {
        return dateRecuperation;
    }

    public void setDateRecuperation(String dateRecuperation) {
        this.dateRecuperation = dateRecuperation;
    }

    public Long getCreneauId() {
        return creneauId;
    }

    public void setCreneauId(Long creneauId) {
        this.creneauId = creneauId;
    }
}
