package passes;

import enums.PassKind;
import enums.PassStatus;
import jakarta.persistence.*;
import people.Person;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pass")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "pass_kind")
public abstract class Pass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pass_owner"))
    private Person owner;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "pass_status", nullable = false, length = 32)
    private PassStatus passStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "pass_kind", nullable = false,insertable = false, updatable = false, length = 32)
    private PassKind passKind;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private Instant updatedAt;

    protected Pass() { /* JPA */ }

    public Pass(Person owner) {
        this.publicId = UUID.randomUUID();
        this.owner = owner;
        this.passStatus = PassStatus.ACTIVE;
    }

    public long getId() { return this.id; }
    public Person getOwner() { return this.owner; }
    public UUID getPublicId() { return this.publicId; }
    public PassStatus getPassStatus() { return this.passStatus; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
    public abstract PassKind getPassKind();

    public void setPassStatus(PassStatus passStatus) { this.passStatus = passStatus; }

    public void setPassKind(PassKind passCategory) { this.passKind = passCategory; }

    public void activate() { this.passStatus = PassStatus.ACTIVE; }
    public void deactivate() { this.passStatus = PassStatus.SUSPENDED; }

    @Override
    public String toString() {
        return "owner("+ this.owner.getPersonKind() + ")=" + this.owner.getId() + " - " + this.owner.getFirstName() + this.owner.getLastName()
                + " - " + this.owner.getEmail();
    }
}
