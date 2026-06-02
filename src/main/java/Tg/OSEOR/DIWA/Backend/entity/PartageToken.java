package Tg.OSEOR.DIWA.Backend.entity;

import java.time.LocalDateTime;

import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
public class PartageToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private LocalDateTime dateExpiration;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id")
    private ConfigurationVehicule configuration;

    public PartageToken() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDateTime dateExpiration) { this.dateExpiration = dateExpiration; }

    public ConfigurationVehicule getConfiguration() { return configuration; }
    public void setConfiguration(ConfigurationVehicule configuration) { this.configuration = configuration; }
}
