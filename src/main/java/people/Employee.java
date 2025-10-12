package people;

import enums.EmployeeType;
import enums.PersonKind;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("EMPLOYEE")
public class Employee extends Person {

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type")
    private EmployeeType employeeType;

    protected Employee() { /* JPA */ }

    public Employee(String firstName, String lastName, LocalDate dob,
                    EmployeeType employeeType, Long worksiteId) {
        super(firstName, lastName, dob);
        this.employeeType =employeeType;
        setWorksiteId(worksiteId);
    }

    public static Employee of(String firstName, String lastName, LocalDate dob,
                              EmployeeType type, Long worksiteId) {
        return new Employee(firstName, lastName, dob, type, worksiteId);
    }

    @Override public PersonKind getPersonKind(){ return PersonKind.EMPLOYEE; }
    @Override public EmployeeType getEmployeeType() { return this.employeeType; }

    protected void setEmployeeType(EmployeeType employeeType) { this.employeeType = employeeType; }
}
