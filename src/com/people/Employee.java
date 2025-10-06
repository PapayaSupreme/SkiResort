package com.people;

import com.enums.EmployeeType;
import com.terrain.*;
import com.utils.ResortUtils;

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

        Worksite worksiteChoice = switch (type) { //Switch to cast speicific type of worsksite to employee, ugly but light
            case PISTER -> {
                List<RescuePoint> workplaces = ResortUtils.getAllPOIsOfType(skiResort, RescuePoint.class);
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
                yield workplaces.get(choice-1);
            }
            case LIFT_OP -> {
                List<Lift> workplaces = ResortUtils.getAllPOIsOfType(skiResort, Lift.class);
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
                yield workplaces.get(choice-1);
            }
            case RESTAURATION -> {
                List<Restaurant> workplaces = ResortUtils.getAllPOIsOfType(skiResort, Restaurant.class);
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
                yield workplaces.get(choice-1);
            }
            case MAINTENANCE -> {
                List<SkiArea> workplaces = ResortUtils.getAllPOIsOfType(skiResort, SkiArea.class);
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
                yield workplaces.get(choice-1);
            }
        };
        try {
            Employee employee = new Employee(firstName, lastName, dob, type, worksiteChoice);
            skiResort.addEmployee(employee);
            System.out.println("Registration successful for " + employee);
        } catch(Exception e) {
            System.out.println("Registration failed :" + e);
        }
    }

    @Override
    public String toString() {
        return "Employee: type=" + this.employeeType + ", worksite="
                + this.worksite + " - " + super.toString();
    }
}
