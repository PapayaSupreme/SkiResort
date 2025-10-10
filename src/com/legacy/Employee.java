package com.legacy;

import com.enums.EmployeeType;
import com.people.Person;
import com.terrain.*;
import com.utils.ResortUtils;
import com.utils.Worksite;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Employee extends Person {
    private EmployeeType employeeType;
    private Worksite worksite;

    public Employee(String firstName, String lastName, LocalDate dob, EmployeeType employeeType, Worksite worksite) {
        super(firstName, lastName, dob);
        this.employeeType = employeeType;
        this.worksite = worksite;
    }

    public EmployeeType getEmployeeType() { return this.employeeType; }
    public Worksite getWorksite() { return this.worksite; }

    public void setEmployeeType(EmployeeType employeeType) { this.employeeType = employeeType; }
    public void setWorksite(Worksite worksite) { this.worksite = worksite; }

    public static void register(Scanner sc, SkiResort skiResort) {
        boolean valid = false;
        int choice;
        System.out.println("=== Employee Registration ===");

        System.out.print("Enter first name: ");
        String firstName = sc.nextLine();

        System.out.print("Enter last name: ");
        String lastName = sc.nextLine();

        LocalDate dob = null;
        while (!valid) {
            System.out.print("Enter date of birth, format YYYY-MM-DD : ");
            String dobInput = sc.nextLine();
            try {
                dob = LocalDate.parse(dobInput);
                valid = true;
            } catch (Exception e) {
                System.out.println("Invalid date format. Try again.");
            }
        }

        valid = false;
        EmployeeType type = null;
        while (!valid) {
            System.out.println("Select employee type");
            System.out.println("1. Pister");
            System.out.println("2. Lift Operator");
            System.out.println("3. Restauration Crew");
            System.out.println("4. Maintenance");
            choice = sc.nextInt();
            sc.nextLine();
            type = switch (choice) {
                case 1 -> EmployeeType.PISTER;
                case 2 -> EmployeeType.LIFT_OP;
                case 3 -> EmployeeType.RESTAURATION;
                case 4 -> EmployeeType.MAINTENANCE;
                default -> {
                    System.out.println("Invalid choice, try again.");
                    yield null;
                }
            };
            valid = (type!=null);
        }

        List<? extends Worksite> workplaces = switch (type) {
            case PISTER -> ResortUtils.getAllWorksitesOfType(skiResort, RescuePoint.class);
            case LIFT_OP -> ResortUtils.getAllWorksitesOfType(skiResort, Lift.class);
            case RESTAURATION -> ResortUtils.getAllWorksitesOfType(skiResort, Restaurant.class);
            case MAINTENANCE -> ResortUtils.getAllWorksitesOfType(skiResort, SkiArea.class);
            default -> List.of();
        };
        do {
            System.out.println("Select workplace (adapted to employee type): ");
            for (int i = 0; i < workplaces.size(); i++) {
                System.out.println((i + 1) + ". " + workplaces.get(i).toString());
            }
            choice = sc.nextInt();
            sc.nextLine();
            if (choice <= 0 || choice > workplaces.size()) {
                System.out.println("Out of bounds. Try again");
            }
        } while (choice <=0 || choice > workplaces.size());
        Worksite worksiteChoice = workplaces.get(choice-1);
        try {
            com.people.Employee employee = new com.people.Employee(firstName, lastName, dob, type, worksiteChoice);
            skiResort.addEmployee(employee);
            System.out.println("Registration successful for " + employee);
        } catch(Exception e) {
            System.out.println("Registration failed :" + e);
        }
    }



    @Override
    public String toString() {
        return "Employee: type=" + this.employeeType + ", worksite="
                + this.worksite.getName() + " (" + this.worksite.getWorksiteType() + ")" + " - " + super.toString();
    }
}
