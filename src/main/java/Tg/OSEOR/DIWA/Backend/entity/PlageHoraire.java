package Tg.OSEOR.DIWA.Backend.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "plage_horaire")
public class PlageHoraire extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek jourSemaine;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    private boolean estDisponible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technicien_id", nullable = false)
    @JsonIgnore
    private Technicien technicien;

    public PlageHoraire() {}

    public PlageHoraire(DayOfWeek jour, LocalTime debut, LocalTime fin, Technicien technicien) {
        this.jourSemaine = jour;
        this.heureDebut = debut;
        this.heureFin = fin;
        this.technicien = technicien;
        this.estDisponible = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DayOfWeek getJourSemaine() { return jourSemaine; }
    public void setJourSemaine(DayOfWeek jourSemaine) { this.jourSemaine = jourSemaine; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public boolean isEstDisponible() { return estDisponible; }
    public void setEstDisponible(boolean estDisponible) { this.estDisponible = estDisponible; }

    public Technicien getTechnicien() { return technicien; }
    public void setTechnicien(Technicien technicien) { this.technicien = technicien; }
}
