package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "affectations_travail")
public class AffectationTravail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technicien_id", nullable = false)
    private Technicien technicien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proforma_id", nullable = false)
    private ProForma proForma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id", nullable = false)
    private DemandeIntervention demande;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFinPrevue;

    @Column
    private LocalDateTime dateFinReelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutAffectation statut = StatutAffectation.A_FAIRE;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum StatutAffectation {
        A_FAIRE, EN_COURS, TERMINE, ANNULE
    }

    public AffectationTravail() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Technicien getTechnicien() { return technicien; }
    public void setTechnicien(Technicien t) { this.technicien = t; }

    public ProForma getProForma() { return proForma; }
    public void setProForma(ProForma p) { this.proForma = p; }

    public DemandeIntervention getDemande() { return demande; }
    public void setDemande(DemandeIntervention d) { this.demande = d; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime d) { this.dateDebut = d; }

    public LocalDateTime getDateFinPrevue() { return dateFinPrevue; }
    public void setDateFinPrevue(LocalDateTime d) { this.dateFinPrevue = d; }

    public LocalDateTime getDateFinReelle() { return dateFinReelle; }
    public void setDateFinReelle(LocalDateTime d) { this.dateFinReelle = d; }

    public StatutAffectation getStatut() { return statut; }
    public void setStatut(StatutAffectation s) { this.statut = s; }

    public String getNotes() { return notes; }
    public void setNotes(String n) { this.notes = n; }
}
