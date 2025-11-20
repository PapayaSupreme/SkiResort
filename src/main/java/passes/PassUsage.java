package passes;

import jakarta.persistence.*;
import terrain.Resort;

import java.time.LocalDateTime;


@Entity
@Table(name="pass_usage")
public class PassUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lift_id")  // Lift isn't a JPA entity, so used a long here not to do worthy tho :P
    private Long liftId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pass_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name="fk_pass_usage_pass"))
    private Pass pass;

    @Column(name = "use_time", updatable = false)
    private LocalDateTime useTime;

    protected PassUsage() { /* JPA */ }

    public PassUsage(Pass pass, Long liftId) {
        this.pass = pass;
        this.liftId = liftId;
    }

    public PassUsage(Pass pass, Long liftId, LocalDateTime useTime) {
        this.pass = pass;
        this.liftId = liftId;
        this.useTime = useTime;
    }

    public Long getId() { return this.id; }
    public Pass getPass() { return this.pass; }
    public Long getLiftId() { return this.liftId; }
    public LocalDateTime getUseTime() { return this.useTime; }

    @Override
    public String toString(){
        return "type: " + this.pass.getPassKind() + ", id: " + this.pass.getId() + " used on "
                + this.useTime + " on lift " + Resort.getTerrainIndex().get(this.liftId).getName();
    }
}
