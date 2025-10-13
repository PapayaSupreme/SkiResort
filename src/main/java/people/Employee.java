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

    public Employee(String email, String firstName, String lastName, LocalDate dob,
                    EmployeeType employeeType, Long worksiteId) {
        super(email, firstName, lastName, dob);
        this.employeeType =employeeType;
        setWorksiteId(worksiteId);
    }

    public static Employee of(String email, String firstName, String lastName, LocalDate dob,
                              EmployeeType type, Long worksiteId) {
        return new Employee(email, firstName, lastName, dob, type, worksiteId);
    }

    @Override public PersonKind getPersonKind(){ return PersonKind.EMPLOYEE; }
    @Override public EmployeeType getEmployeeType() { return this.employeeType; }

    protected void setEmployeeType(EmployeeType employeeType) { this.employeeType = employeeType; }

    @Override
    public String toString(){
        return "Employee: kind=" + this.employeeType + ", worksite=" + this.getWorksiteId() + super.toString();
    }
}
