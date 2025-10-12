package people;

import enums.EmployeeType;
import enums.PersonKind;
import enums.SkiSchool;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "person_kind")
public abstract class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Column(name = "email", length = 128)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "person_kind", nullable = false, insertable = false, updatable = false)
    private PersonKind personKind;

    @Column(name = "worksite_id")
    private Long worksiteId;

    // DB defaults handle insert; app sets updatedAt on UPDATE
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private Instant updatedAt;

    protected Person() { /* JPA */ }

    protected Person(String email, String firstName, String lastName, LocalDate dob) {
        this.publicId = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
    }

    // Keep DB as source of truth for created_at; update updated_at on changes
    @PreUpdate
    void touchUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    // ---------- getters ----------
    public Long getId() { return this.id; }
    public UUID getPublicId() { return this.publicId; }
    public String getEmail() { return email; }
    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public LocalDate getDob() { return this.dob; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
    public Long getWorskiteId() { return this.worksiteId; }

    public abstract PersonKind getPersonKind();
    public EmployeeType getEmployeeType() { return null; }
    public Long getWorksiteId() { return null; }
    public SkiSchool getSkiSchool() { return null; }

    protected void setWorksiteId(Long worksiteId) { this.worksiteId = worksiteId; }
    public void setPersonKind(PersonKind personKind) { this.personKind = personKind; }

    // equals/hashCode by publicId (stable before DB id exists)
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person that)) return false;
        return Objects.equals(publicId, that.publicId);
    }
    @Override public int hashCode() { return Objects.hash(publicId); }

    @Override
    public String toString() {
        return "Person{id=%s, name='%s %s', dob=%s}".formatted(id, firstName, lastName, dob);
    }
}
