package people;

import enums.PersonKind;
import enums.SkiSchool;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("INSTRUCTOR")
public class Instructor extends Person {

    // INSTRUCTOR-only (others must be NULL per DB CHECK)
    @Enumerated(EnumType.STRING)
    @Column(name = "ski_school")
    private SkiSchool skiSchool;

    protected Instructor() { /* JPA */ }

    public Instructor(String firstName, String lastName, LocalDate dob,
                       SkiSchool skiSchool, Long worksiteId) {
        super(firstName, lastName, dob);
        this.skiSchool = skiSchool;
        setWorksiteId(worksiteId);
    }

    public static Instructor of(String firstName, String lastName, LocalDate dob,
                                SkiSchool skiSchool, Long worksiteId) {
        return new Instructor(firstName, lastName, dob, skiSchool, worksiteId);
    }
    @Override public PersonKind getPersonKind() { return PersonKind.INSTRUCTOR; }
    @Override public SkiSchool getSkiSchool() { return this.skiSchool; }

    protected void setSkiSchool(SkiSchool skiSchool) { this.skiSchool = skiSchool; }
}
