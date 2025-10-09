package com.people;

import com.enums.SkiSchool;
import com.terrain.*;
import com.utils.ResortUtils;
import com.utils.Worksite;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Instructor extends Person {
    private SkiSchool skiSchool;
    private Worksite workplace;

    public Instructor(String firstName, String lastName, LocalDate dob, SkiSchool skiSchool, Worksite workplace) {
        super(firstName, lastName, dob);
        this.skiSchool = skiSchool;
        this.workplace = workplace;
    }

    public SkiSchool getSchool() { return this.skiSchool; }
    public Worksite getWorkplace() { return this.workplace; }

    public void setSchool(SkiSchool skiSchool) { this.skiSchool = skiSchool; }
    public void setWorkplace(Worksite workplace) { this.workplace = workplace; }

    public static void register(Scanner sc, SkiResort skiResort) {
        boolean valid = false;
        int choice;
        System.out.println("=== Instructor Registration ===");

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
        SkiSchool schoolChoice = null;
        while (!valid) {
            int i = 0;
            System.out.println("Select instructor school : ");
            for (SkiSchool school : SkiSchool.values()) {
                i++;
                System.out.println((i) + ". " + school);

            }
            choice = sc.nextInt();
            sc.nextLine();
            if (choice > 0 && choice <= i) {
                schoolChoice = SkiSchool.values()[i - 1];
                valid = true;
            }
        }

        List<SkiArea> workplaces = ResortUtils.getAllWorksitesOfType(skiResort, SkiArea.class);
        do {
            System.out.println("Select workplace: ");
            for (int i = 0; i < workplaces.size(); i++) {
                System.out.println((i + 1) + ". " + workplaces.get(i).toString());
            }
            choice = sc.nextInt();
            sc.nextLine();
            if (choice <= 0 || choice > workplaces.size()) {
                System.out.println("Out of bounds. Try again");
            }
        } while (choice <=0 || choice > workplaces.size());
        SkiArea workPlaceChoice = workplaces.get(choice-1);
        try {
            Instructor instructor = new Instructor(firstName, lastName, dob, schoolChoice, workPlaceChoice);
            skiResort.addInstructor(instructor);
            System.out.println("Registration successful for " + instructor);
        } catch (Exception e) {
            System.out.println("Registration failed :" + e);
        }
    }

    @Override
    public String toString() {
        return "Instructor: school=" + this.skiSchool + ", worksite="
                + this.workplace.getName() + "  (" + this.workplace.getWorksiteType() + " - " + super.toString();
    }
}
