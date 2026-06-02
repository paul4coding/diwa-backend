package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
public class RegleExclusion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_a_id")
    private OptionVehicule optionA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_b_id")
    private OptionVehicule optionB;

    private String description;

    public RegleExclusion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OptionVehicule getOptionA() { return optionA; }
    public void setOptionA(OptionVehicule optionA) { this.optionA = optionA; }

    public OptionVehicule getOptionB() { return optionB; }
    public void setOptionB(OptionVehicule optionB) { this.optionB = optionB; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
