package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
public class AssetVisuel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private OptionVehicule option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    private String vue; // FACE, PROFIL, ARRIERE, HABITACLE, INTERIEUR, EXTERIEUR
    
    private String urlCloudinary;
    private Integer zIndex;

    public AssetVisuel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OptionVehicule getOption() { return option; }
    public void setOption(OptionVehicule option) { this.option = option; }

    public Vehicule getVehicule() { return vehicule; }
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }

    public String getVue() { return vue; }
    public void setVue(String vue) { this.vue = vue; }

    public String getUrlCloudinary() { return urlCloudinary; }
    public void setUrlCloudinary(String urlCloudinary) { this.urlCloudinary = urlCloudinary; }

    public Integer getzIndex() { return zIndex; }
    public void setzIndex(Integer zIndex) { this.zIndex = zIndex; }
}
