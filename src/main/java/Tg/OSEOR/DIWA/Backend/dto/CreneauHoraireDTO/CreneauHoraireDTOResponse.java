package Tg.OSEOR.DIWA.Backend.dto.CreneauHoraireDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class CreneauHoraireDTOResponse {
    private Long id;
    private String uuid;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Boolean estLibre;
    
    private Long technicienId;
    private String technicienNom;
    
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public CreneauHoraireDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

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

    public String getTechnicienNom() { return technicienNom; }
    public void setTechnicienNom(String technicienNom) { this.technicienNom = technicienNom; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
}
