package Tg.OSEOR.DIWA.Backend.entity;

import java.time.LocalDate;

import Tg.OSEOR.DIWA.Backend.entity.enums.StatutRDV;
import Tg.OSEOR.DIWA.Backend.entity.enums.UrgenceRDV;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
public class RendezVous extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    
    @Enumerated(EnumType.STRING)
    private StatutRDV statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technicien_id")
    private Technicien technicien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceSAV service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creneau_id")
    private CreneauHoraire creneau;

    private String immatriculation;
    private String modeleVehicule;
    private String vin;
    private Integer kilometrage;
    
    @Enumerated(EnumType.STRING)
    private UrgenceRDV urgence;

    private String heureDebut;
    
    @Column(columnDefinition = "TEXT")
    private String detailsSpecifiques;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    public RendezVous() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public StatutRDV getStatut() { return statut; }
    public void setStatut(StatutRDV statut) { this.statut = statut; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Technicien getTechnicien() { return technicien; }
    public void setTechnicien(Technicien technicien) { this.technicien = technicien; }

    public ServiceSAV getService() { return service; }
    public void setService(ServiceSAV service) { this.service = service; }

    public Vehicule getVehicule() { return vehicule; }
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }

    public CreneauHoraire getCreneau() { return creneau; }
    public void setCreneau(CreneauHoraire creneau) { this.creneau = creneau; }

    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }

    public String getModeleVehicule() { return modeleVehicule; }
    public void setModeleVehicule(String modeleVehicule) { this.modeleVehicule = modeleVehicule; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public Integer getKilometrage() { return kilometrage; }
    public void setKilometrage(Integer kilometrage) { this.kilometrage = kilometrage; }

    public UrgenceRDV getUrgence() { return urgence; }
    public void setUrgence(UrgenceRDV urgence) { this.urgence = urgence; }

    public String getDetailsSpecifiques() { return detailsSpecifiques; }
    public void setDetailsSpecifiques(String detailsSpecifiques) { this.detailsSpecifiques = detailsSpecifiques; }

    public String getHeureDebut() { return heureDebut; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
