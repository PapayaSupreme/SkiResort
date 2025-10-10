package com.people;

import com.enums.EmployeeType;
import com.enums.PersonKind;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("EMPLOYEE")
public class Employee extends Person {

    // worksite_id is a Long (FK to whatever terrain entity implements Worksite)
    @Column(name = "worksite_id")
    private Long worksiteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type")
    private EmployeeType employeeType;

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

    @Override public Long getWorksiteId() { return this.worksiteId; }
    @Override public PersonKind getPersonKind(){ return PersonKind.EMPLOYEE; }
    @Override public EmployeeType getEmployeeType() { return this.employeeType; }

    public void setWorksiteId(Long worksiteId) { this.worksiteId = worksiteId; }
    protected void setEmployeeType(EmployeeType employeeType) { this.employeeType = employeeType; }
}
