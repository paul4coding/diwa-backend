package Tg.OSEOR.DIWA.Backend.dto.TechnicienDTO;

import java.time.LocalDateTime;

public class TechnicienDTOResponse {
    private Long id;
    private String uuid;
    private String nom;
    private String specialite;
    private String grade;
    private Integer chargeTravailMax;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public TechnicienDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Integer getChargeTravailMax() { return chargeTravailMax; }
    public void setChargeTravailMax(Integer chargeTravailMax) { this.chargeTravailMax = chargeTravailMax; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
}
