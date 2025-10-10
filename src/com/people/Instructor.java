package com.people;

import com.enums.PersonKind;
import com.enums.SkiSchool;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@DiscriminatorValue("INSTRUCTOR")
public class Instructor extends Person {

    // same FK logic as employee
    @Column(name = "worksite_id")
    private Long worksiteId;

    // INSTRUCTOR-only (others must be NULL per DB CHECK)
    @Enumerated(EnumType.STRING)
    @Column(name = "ski_school")
    private SkiSchool skiSchool;

    protected Instructor() { /* JPA */ }

    private Instructor(String firstName, String lastName, LocalDate dob,
                       SkiSchool skiSchool, Long worksiteId) {
        super(firstName, lastName, dob);
        this.skiSchool = skiSchool;
        this.worksiteId = worksiteId;
    }

    public static Instructor of(String firstName, String lastName, LocalDate dob,
                                SkiSchool skiSchool, Long worksiteId) {
        return new Instructor(firstName, lastName, dob, skiSchool, worksiteId);
    }

    @Override public Long getWorksiteId() { return this.worksiteId; }
    @Override public PersonKind getPersonKind() { return PersonKind.INSTRUCTOR; }
    @Override public SkiSchool getSkiSchool() { return this.skiSchool; }

    protected void setSkiSchool(SkiSchool skiSchool) { this.skiSchool = skiSchool; }
    public void setWorksiteId(Long worksiteId) { this.worksiteId = worksiteId; }
}
