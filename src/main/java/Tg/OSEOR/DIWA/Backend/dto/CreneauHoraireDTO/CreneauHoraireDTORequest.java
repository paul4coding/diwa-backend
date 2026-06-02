package Tg.OSEOR.DIWA.Backend.dto.CreneauHoraireDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.NotNull;

public class CreneauHoraireDTORequest {
    @NotNull(message = "La date est obligatoire")
    private LocalDate date;
    
    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;
    
    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;
    
    private Boolean estLibre = true;
    
    @NotNull(message = "Le technicien est obligatoire")
    private Long technicienId;

    public CreneauHoraireDTORequest() {}

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public Boolean getEstLibre() { return estLibre; }
    public void setEstLibre(Boolean estLibre) { this.estLibre = estLibre; }

    public Long getTechnicienId() { return technicienId; }
    public void setTechnicienId(Long technicienId) { this.technicienId = technicienId; }
}
