package Tg.OSEOR.DIWA.Backend.dto.AtelierDTO;

import Tg.OSEOR.DIWA.Backend.entity.AffectationTravail.StatutAffectation;
import java.time.LocalDateTime;

public class AffectationDTO {
    private Long id;
    private Long proFormaId;
    private String referenceDossier;
    private String vehicule;
    private Long technicienId;
    private String technicienNom;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFinPrevue;
    private StatutAffectation statut;
    private String notes;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProFormaId() { return proFormaId; }
    public void setProFormaId(Long id) { this.proFormaId = id; }
    public String getReferenceDossier() { return referenceDossier; }
    public void setReferenceDossier(String ref) { this.referenceDossier = ref; }
    public String getVehicule() { return vehicule; }
    public void setVehicule(String v) { this.vehicule = v; }
    public Long getTechnicienId() { return technicienId; }
    public void setTechnicienId(Long id) { this.technicienId = id; }
    public String getTechnicienNom() { return technicienNom; }
    public void setTechnicienNom(String n) { this.technicienNom = n; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime d) { this.dateDebut = d; }
    public LocalDateTime getDateFinPrevue() { return dateFinPrevue; }
    public void setDateFinPrevue(LocalDateTime d) { this.dateFinPrevue = d; }
    public StatutAffectation getStatut() { return statut; }
    public void setStatut(StatutAffectation s) { this.statut = s; }
    public String getNotes() { return notes; }
    public void setNotes(String n) { this.notes = n; }
}
