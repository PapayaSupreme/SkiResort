package com.people;

import com.enums.EmployeeType;
import com.enums.PersonKind;
import com.enums.SkiSchool;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "person_kind", nullable = false, insertable = false, updatable = false)
    private PersonKind personKind;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type")
    private EmployeeType employeeType;

    // EMPLOYEE + INSTRUCTOR specific (guests must have NULL per DB CHECK)
    @Column(name = "worksite_id") //TODO: make an util findWorksiteByID
    private Long worksiteId;

    // INSTRUCTOR-only (others must be NULL per DB CHECK)
    @Enumerated(EnumType.STRING)
    @Column(name = "ski_school")
    private SkiSchool skiSchool;

    // DB defaults handle insert; app sets updatedAt on UPDATE
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private Instant updatedAt;

    protected Person() { /* JPA */ }

    protected Person(String firstName, String lastName, LocalDate dob) {
        this.publicId = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName  = lastName;
        this.dob       = dob;
    }

    // Keep DB as source of truth for created_at; update updated_at on changes
    @PreUpdate
    void touchUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    // ---------- getters ----------
    public Long getId() { return this.id; }
    public UUID getPublicId() { return this.publicId; }
    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public LocalDate getDob() { return this.dob; }
    public PersonKind getPersonKind() { return this.personKind; }
    public EmployeeType getEmployeeType() { return this.employeeType; }
    public Long getWorksiteId() { return this.worksiteId; }
    public SkiSchool getSkiSchool() { return this.skiSchool; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }

    // ---------- protected setters for subclasses ----------
    protected void setEmployeeType(EmployeeType employeeType) { this.employeeType = employeeType; }
    protected void setSkiSchool(SkiSchool skiSchool) { this.skiSchool = skiSchool; }

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
