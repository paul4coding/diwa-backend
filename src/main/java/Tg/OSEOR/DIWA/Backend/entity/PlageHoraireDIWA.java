package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "plage_horaire_diwa")
public class PlageHoraireDIWA extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    @Column(nullable = false)
    private boolean active = true;

    private Integer ordre;

    public PlageHoraireDIWA() {}

    public PlageHoraireDIWA(LocalTime heureDebut, LocalTime heureFin, Integer ordre) {
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.ordre = ordre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }

    public String getLibelle() {
        return heureDebut.toString() + " – " + heureFin.toString();
    }
}
