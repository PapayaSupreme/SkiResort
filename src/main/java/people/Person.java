package people;

import enums.EmployeeType;
import enums.PassStatus;
import enums.PersonKind;
import enums.SkiSchool;
import jakarta.persistence.*;
import passes.Pass;
import passes.PassRepo;
import passes.PassUsage;
import utils.ResortUtils;

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
        this.publicId = UUID.randomUUID(); // we have to still init it bc it's not nullable in db and jpa so we have to send it lol
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

    public static Guest createGuest(PersonRepo repo, String email, String firstName, String lastName, LocalDate dob) {
        return savePerson(repo, new Guest(email, firstName, lastName, dob), "Guest");
    }

    public static Instructor createInstructor(PersonRepo repo, String email, String firstName, String lastName, LocalDate dob,
                                              SkiSchool skiSchool, long worksiteId) {
        return savePerson(repo, new Instructor(email, firstName, lastName, dob, skiSchool, worksiteId), "Instructor");
    }

    public static Employee createEmployee(PersonRepo repo, String email, String firstName, String lastName, LocalDate dob, EmployeeType employeeType, long worksiteId) {
        return savePerson(repo, new Employee(email, firstName, lastName, dob, employeeType, worksiteId), "Employee");
    }

    private static <T extends Person> T savePerson(PersonRepo personRepo, T person, String typeName) {
        try {
            personRepo.save(person);
            System.out.println(ResortUtils.ConsoleColors.ANSI_GREEN +
                    "Successfully saved to the DB: " + ResortUtils.ConsoleColors.ANSI_RESET + person);
            return person;
        } catch (Exception e) {
            System.err.println("Failed to save " + typeName + ": " + e);
            return null;
        }
    }

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

    public void displayFullInfo(PassRepo passRepo){
        System.out.println(ResortUtils.ConsoleColors.ANSI_BLUE + "\n=== INFO ===\n\n" + ResortUtils.ConsoleColors.ANSI_RESET + this);
        long t0 = System.nanoTime();
        List<Pass> passes = passRepo.findAllPasses(this);
        List<Pass> expiredPasses = passes.stream().filter(p -> p.getPassStatus() == PassStatus.EXPIRED).toList();
        List<Pass> suspendedPasses = passes.stream().filter(p -> p.getPassStatus() == PassStatus.SUSPENDED).toList();
        List<Pass> validPasses = passes.stream().filter(p -> p.getPassStatus() == PassStatus.ACTIVE).toList();
        List<PassUsage> passUsages = passRepo.findAllPassUsages(this);
        HashSet<LocalDate> dayCount = new HashSet<>();
        for (PassUsage pu: passUsages){
            dayCount.add(pu.getUseTime().toLocalDate());
        }
        long t1 = System.nanoTime();
        int count = 0;
        System.out.println(ResortUtils.ConsoleColors.ANSI_BLUE + "\n=== PASSES ===\n\n"+ ResortUtils.ConsoleColors.ANSI_RESET + "Expired Passes:");
        for (Pass p: expiredPasses){
            for (PassUsage pu: passUsages){
                if (pu.getPass().equals(p)){
                    count++;
                }
            }
            System.out.println("Uses: " + count + " - " + p);
            count = 0;
        }
        System.out.println("\nSuspended Passes: ");
        for (Pass p: suspendedPasses){
            for (PassUsage pu: passUsages){
                if (pu.getPass().equals(p)){
                    count++;
                }
            }
            System.out.println("Uses: " + count + " - " + p);
            count = 0;
        }
        System.out.println("\nValid Passes: ");
        for (Pass p: validPasses){
            for (PassUsage pu: passUsages){
                if (pu.getPass().getId() == p.getId()) {
                    count++;
                }
            }
            System.out.println("Used " + count + " times - " + p);
            count = 0;
        }
        System.out.println("\nTotal: " + expiredPasses.size() + " expired, " + suspendedPasses.size() + " suspended, " + validPasses.size() + " valid.");
        System.out.println("Total uses: " + passUsages.size() + " times on " + dayCount.size() + " days.");
        runTimer("Fetch of a person's passes", t0, t1);
    }
    @Override
    public String toString() {
        return "ID: %s, Name: %s %s, DOB: %s, Email: %s".formatted(id, firstName, lastName, dob, email);
    }
}
