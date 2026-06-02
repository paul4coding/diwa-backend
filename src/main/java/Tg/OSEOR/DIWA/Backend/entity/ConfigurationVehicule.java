package Tg.OSEOR.DIWA.Backend.entity;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
public class ConfigurationVehicule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomConfig;
    private Double prixTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finition_id")
    private Finition finition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorisation_id")
    private Motorisation motorisation;

    @ManyToMany
    @JoinTable(name = "configuration_options",
               joinColumns = @JoinColumn(name = "configuration_id"),
               inverseJoinColumns = @JoinColumn(name = "option_id"))
    private List<OptionVehicule> optionsChoisies;

    public ConfigurationVehicule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomConfig() { return nomConfig; }
    public void setNomConfig(String nomConfig) { this.nomConfig = nomConfig; }

    public Double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(Double prixTotal) { this.prixTotal = prixTotal; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Vehicule getVehicule() { return vehicule; }
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }

    public Finition getFinition() { return finition; }
    public void setFinition(Finition finition) { this.finition = finition; }

    public Motorisation getMotorisation() { return motorisation; }
    public void setMotorisation(Motorisation motorisation) { this.motorisation = motorisation; }

    public List<OptionVehicule> getOptionsChoisies() { return optionsChoisies; }
    public void setOptionsChoisies(List<OptionVehicule> optionsChoisies) { this.optionsChoisies = optionsChoisies; }
}
