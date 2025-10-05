package com.people;

import com.enums.EmployeeType;

public class Employee extends Person {
    private EmployeeType employeeType;
    private Worksite worksite;

    public Employee(int id, EmployeeType employeeType, Worksite worksite) {
        super(id);
        this.employeeType = employeeType;
        this.worksite = worksite;
    }

    public EmployeeType getEmployeeType() { return this.employeeType; }
    public Worksite getWorksite() { return this.worksite; }

    public void setEmployeeType(EmployeeType employeeType) { this.employeeType = employeeType; }
    public void setWorksite(Worksite worksite) { this.worksite = worksite; }
}
