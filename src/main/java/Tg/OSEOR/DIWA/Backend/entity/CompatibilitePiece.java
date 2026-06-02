package Tg.OSEOR.DIWA.Backend.entity;

import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"piece_id", "vehicule_id"})
})
public class CompatibilitePiece extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piece_id")
    private PieceDetachee piece;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    public CompatibilitePiece() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PieceDetachee getPiece() { return piece; }
    public void setPiece(PieceDetachee piece) { this.piece = piece; }

    public Vehicule getVehicule() { return vehicule; }
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }
}
