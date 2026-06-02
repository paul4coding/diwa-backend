package Tg.OSEOR.DIWA.Backend.dto;

import java.time.LocalTime;

public class CreneauDTO {

    private Long plageHoraireId;
    private Long technicienId;
    private String nomTechnicien;
    private String specialite;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private boolean disponible;

    public CreneauDTO() {}

    public CreneauDTO(Long plageHoraireId, Long technicienId, String nomTechnicien,
                      String specialite, LocalTime heureDebut, LocalTime heureFin, boolean disponible) {
        this.plageHoraireId = plageHoraireId;
        this.technicienId = technicienId;
        this.nomTechnicien = nomTechnicien;
        this.specialite = specialite;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.disponible = disponible;
    }

    public Long getPlageHoraireId() { return plageHoraireId; }
    public void setPlageHoraireId(Long plageHoraireId) { this.plageHoraireId = plageHoraireId; }

    public Long getTechnicienId() { return technicienId; }
    public void setTechnicienId(Long technicienId) { this.technicienId = technicienId; }

    public String getNomTechnicien() { return nomTechnicien; }
    public void setNomTechnicien(String nomTechnicien) { this.nomTechnicien = nomTechnicien; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
