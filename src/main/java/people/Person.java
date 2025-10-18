package people;

import enums.EmployeeType;
import enums.PersonKind;
import enums.SkiSchool;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static utils.ResortUtils.pickInt;
import static utils.ResortUtils.runTimer;

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


    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private Instant updatedAt;

    protected Person() { /* JPA */ }

    protected Person(String email, String firstName, String lastName, LocalDate dob) {
        this.publicId = UUID.randomUUID(); // we have to still init it bc its not nullable in db and jpa so we have to send it lol
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
    }

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
    public Long getWorksiteId() { return this.worksiteId; }
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

    public static <T extends Person> T findByNameGUI(Scanner sc, PersonRepo personRepo, Class<T> clazz){
        System.out.println("Enter last name of " + clazz.getSimpleName() + ", or a part of it: ");
        String lastName = sc.nextLine();
        long t0 = System.nanoTime();
        List<Person> persons = personRepo.findByLastName(lastName);
        List<T> out = persons.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .toList();
        long t1 = System.nanoTime();
        if (!out.isEmpty()) {
            if (out.size() == 1) {
                System.out.println(out.size() + clazz.getSimpleName() + " was found: \n");
            } else {
                System.out.println(out.size() + " " +  clazz.getSimpleName() + "s were found: \n");
            }
            for (int i = 0; i<out.size(); i++){
                System.out.println((i+1) + ". " + out.get(i));
            }
            System.out.println("0. CANCEL");
        } else {
            System.out.println("No match was found");
            runTimer(clazz.getSimpleName() + " from partial name match", t0, t1);
            return null;
        }
        runTimer(clazz.getSimpleName() + " from partial name match", t0, t1);
        int choice = pickInt(sc, 0, out.size()) -1;
        if (choice != -1){
            return out.get(choice);
        }
        System.out.println("Cancelling...");
        return null;
    }

    @Override
    public String toString() {
        return " id=%s, name='%s %s', dob=%s, email=%s".formatted(id, firstName, lastName, dob, email);
    }
}
