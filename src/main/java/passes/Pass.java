package passes;

import enums.PassKind;
import enums.PassStatus;
import enums.PersonKind;
import jakarta.persistence.*;
import people.Person;
import utils.ResortUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static utils.ResortUtils.runTimer;

@Entity
@Table(name = "pass")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "pass_kind")
public abstract class Pass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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


    public static ALaCartePass createALaCartePass(PassRepo passRepo, Person owner){
        return savePass(passRepo, new ALaCartePass(owner), "ALaCartePass");
    }

    public static DayPass createDayPass(PassRepo passRepo, Person owner, LocalDate validDay){
        return savePass(passRepo, new DayPass(owner, validDay), "DayPass");
    }

    public static MultiDayPass  createMultiDayPass(PassRepo passRepo, Person owner, LocalDate validFrom, LocalDate validTo){
        return savePass(passRepo, new MultiDayPass(owner, validFrom, validTo), "MultiDayPass");
    }

    public static SeasonPass createSeasonPass(PassRepo passRepo, Person owner){
        return savePass(passRepo, new SeasonPass(owner), "SeasonPass");
    }

    public static void displayPassesValidAt(PassRepo passRepo, LocalDate date){
        long t0 = System.nanoTime();
        List<DayPass> dayPasses = passRepo.findDayPassesValidOn(date);
        List<MultiDayPass> multiDayPasses = passRepo.findMultiDayPassesValidOn(date);
        List<SeasonPass> seasonPasses = passRepo.findSeasonPassesValidOn(date);
        List<ALaCartePass> aLaCartePasses = passRepo.findALaCartePassesValidOn(date);
        long t1 = System.nanoTime();
        runTimer("All passes valid on " + date.toString() + " query", t0, t1);
        System.out.println(ResortUtils.ConsoleColors.ANSI_BLUE + "\n=== DAY PASSES ===" + ResortUtils.ConsoleColors.ANSI_RESET);
        for (DayPass p: dayPasses){
            System.out.println(p);
        }
        System.out.println(ResortUtils.ConsoleColors.ANSI_BLUE + "\n=== MULTI-DAY PASSES ===" + ResortUtils.ConsoleColors.ANSI_RESET);
        for (MultiDayPass p: multiDayPasses){
            System.out.println(p);
        }
        System.out.println(ResortUtils.ConsoleColors.ANSI_BLUE + "\n=== SEASON PASSES ===" + ResortUtils.ConsoleColors.ANSI_RESET);
        for (SeasonPass p: seasonPasses){
            System.out.println(p);
        }
        System.out.println(ResortUtils.ConsoleColors.ANSI_BLUE + "\n=== A LA CARTE PASSES ===" + ResortUtils.ConsoleColors.ANSI_RESET);
        for (ALaCartePass p: aLaCartePasses){
            System.out.println(p);
        }
        System.out.println("\nTotal valid passes on " + date + ": "
                + (dayPasses.size() + multiDayPasses.size() + seasonPasses.size() + aLaCartePasses.size()));
        System.out.println("Day Passes: " + dayPasses.size() + ", Multi-Day Passes: " + multiDayPasses.size()
                + ", Season Passes: " + seasonPasses.size() + ", A La Carte Passes: " + aLaCartePasses.size());
    }


    public static void displayPassesOfKind(PassRepo passRepo, PassKind passKind){
        long t0 = System.nanoTime();
        List<Pass> passes = passRepo.findPassesOfKind(passKind);
        long t1 = System.nanoTime();
        runTimer("All active " + passKind.toString() + " Passes query", t0, t1);
        for (Pass p : passes) {
            System.out.println(p);
        }
        System.out.println("\nTotal " + passKind + " Passes: " + passes.size());
    }

    public static void displaySpecialPasses(PassRepo passRepo){
        long t0 = System.nanoTime();
        List<Pass> employeePasses = passRepo.findPassesOfPersonKind(PersonKind.EMPLOYEE);
        List<Pass> instructorPasses = passRepo.findPassesOfPersonKind(PersonKind.INSTRUCTOR);
        long t1 = System.nanoTime();
        runTimer("All special passes query", t0, t1);
        int count1 = 0;
        System.out.println(ResortUtils.ConsoleColors.ANSI_BLUE + "\n=== EMPLOYEE - SEASON PASSES ===" + ResortUtils.ConsoleColors.ANSI_RESET);
        for (Pass p: employeePasses){
            System.out.println(p);
        }
        System.out.println(ResortUtils.ConsoleColors.ANSI_BLUE + "\n=== INSTRUCTOR - SEASON PASSES ===" + ResortUtils.ConsoleColors.ANSI_RESET);
        for (Pass p: instructorPasses){
            if (p.getPassKind() == PassKind.SEASON) {
                System.out.println(p);
                count1++;
            }
        }
        System.out.println(ResortUtils.ConsoleColors.ANSI_BLUE + "\n=== INSTRUCTOR - A LA CARTE PASSES ===" + ResortUtils.ConsoleColors.ANSI_RESET);
        for (Pass p: instructorPasses){
            if (p.getPassKind() == PassKind.ALACARTE) {
                System.out.println(p);
            }
        }
        System.out.println("\nTotal special passes: " + employeePasses.size() + instructorPasses.size());
        System.out.println("Employee Season Passes: " + employeePasses.size() + ", Instructor Season Passes: "
                + count1 + ", A La Carte Passes: " + (instructorPasses.size() - count1));
    }


    private static <T extends Pass> T savePass(PassRepo passRepo, T pass, String typeName) {
        try {
            passRepo.save(pass);
            System.out.println(ResortUtils.ConsoleColors.ANSI_GREEN +
                    "Successfully saved to the DB: " + ResortUtils.ConsoleColors.ANSI_RESET + pass);
            return pass;
        } catch (Exception e) {
            System.err.println("Failed to save " + typeName + ": " + e);
            return null;
        }
    }


    @Override
    public String toString() {
        return "owner("+ this.owner.getPersonKind() + "): " + this.owner.getId() + " - " + this.owner.getFirstName() + " "
                + this.owner.getLastName() + " - " + this.owner.getEmail() + ", status: " + this.passStatus.toString();
    }
}
