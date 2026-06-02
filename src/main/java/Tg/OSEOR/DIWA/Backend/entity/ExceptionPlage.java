package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "exception_plage")
public class ExceptionPlage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "plage_id") // Si null, journée entière
    private PlageHoraireDIWA plage;

    private String motif;

    @ManyToOne
    @JoinColumn(name = "createur_id")
    private User createur;

    public ExceptionPlage() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public PlageHoraireDIWA getPlage() { return plage; }
    public void setPlage(PlageHoraireDIWA plage) { this.plage = plage; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public User getCreateur() { return createur; }
    public void setCreateur(User createur) { this.createur = createur; }
}
