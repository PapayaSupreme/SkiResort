package com.people;

import com.enums.SkiSchool;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@DiscriminatorValue("INSTRUCTOR")
public class Instructor extends Person {

    // same FK logic as employee
    private Long worksiteId;

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

    public SkiSchool getSkiSchool() { return this.skiSchool; }
    public Long getWorksiteId() { return this.worksiteId; }
    public void setWorksiteId(Long worksiteId) { this.worksiteId = worksiteId; }
}
