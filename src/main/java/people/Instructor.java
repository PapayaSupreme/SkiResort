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

    public Instructor(String email, String firstName, String lastName, LocalDate dob,
                       SkiSchool skiSchool, Long worksiteId) {
        super(email, firstName, lastName, dob);
        this.skiSchool = skiSchool;
        setWorksiteId(worksiteId);
    }

    public static Instructor of(String email, String firstName, String lastName, LocalDate dob,
                                SkiSchool skiSchool, Long worksiteId) {
        return new Instructor(email, firstName, lastName, dob, skiSchool, worksiteId);
    }
    @Override public PersonKind getPersonKind() { return PersonKind.INSTRUCTOR; }
    @Override public SkiSchool getSkiSchool() { return this.skiSchool; }

    protected void setSkiSchool(SkiSchool skiSchool) { this.skiSchool = skiSchool; }

    @Override
    public String toString(){
        return "Instructor: ski school=" + this.skiSchool + ", worksiteId=" + this.getWorksiteId() + super.toString();
    }
}
