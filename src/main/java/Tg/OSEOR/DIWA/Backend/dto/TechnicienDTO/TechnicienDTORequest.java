package Tg.OSEOR.DIWA.Backend.dto.TechnicienDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TechnicienDTORequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    
    @NotBlank(message = "La spécialité est obligatoire")
    private String specialite;
    
    @NotBlank(message = "Le grade est obligatoire")
    private String grade;
    
    @NotNull(message = "La charge max est obligatoire")
    private Integer chargeTravailMax;

    public TechnicienDTORequest() {}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Integer getChargeTravailMax() { return chargeTravailMax; }
    public void setChargeTravailMax(Integer chargeTravailMax) { this.chargeTravailMax = chargeTravailMax; }
}
