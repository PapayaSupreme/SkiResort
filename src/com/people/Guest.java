package com.people;

import com.terrain.SkiResort;

import java.time.LocalDate;
import java.util.Scanner;

public class Guest extends Person {

    public Guest(String firstName, String lastName, LocalDate dob) {
        super(firstName, lastName, dob);
    }

    public static void register(Scanner sc, SkiResort skiResort) {
        boolean valid = false;
        System.out.println("=== Guest Registration ===");

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
                System.out.println("Error : " + e);
            }
        }
        try {
            Guest g = new Guest(firstName, lastName, dob);
            valid = true;
            System.out.println("Registration successful for: " + firstName + " " + lastName + " (" + dob + ")");
        } catch(Exception e) {
            System.out.println("Registration failed :" + e);
        }
        //TODO: skiResort.gu
    }

    @Override
    public String toString() {
        return "Guest - " + super.toString();
    }
}
