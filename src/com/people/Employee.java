package com.people;

import com.enums.EmployeeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@DiscriminatorValue("EMPLOYEE")
public class Employee extends Person {

    // worksite_id is a Long (FK to whatever terrain entity implements Worksite)
    private Long worksiteId;

    protected Employee() { /* JPA */ }

    private Employee(String firstName, String lastName, LocalDate dob,
                     EmployeeType type, Long worksiteId) {
        super(firstName, lastName, dob);
        setEmployeeType(type);
        this.worksiteId = worksiteId;
    }

    public static Employee of(String firstName, String lastName, LocalDate dob,
                              EmployeeType type, Long worksiteId) {
        return new Employee(firstName, lastName, dob, type, worksiteId);
    }

    public Long getWorksiteId() { return this.worksiteId; }
    public void setWorksiteId(Long worksiteId) { this.worksiteId = worksiteId; }
}
