package com.tests;

import com.passes.ALaCartePass;
import com.passes.DayPass;
import com.passes.MultiDayPass;
import com.passes.YearPass;
import com.people.Employee;
import com.people.Guest;
import com.people.Instructor;
import com.terrain.SkiResort;

import java.util.Scanner;

import static com.architect.SerreChe.createResort;

public class MainTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SkiResort skiResort = null;
        boolean exit = false;
        while (!exit) {
            System.out.println("=== MAIN MENU ===");
            System.out.println("1. instantiate with default resort");
            System.out.println("2. register a person");
            System.out.println("3. [WIP] add pass to a person");
            System.out.println("4. exit");

            int choice = sc.nextInt();
            sc.nextLine();

            switch(choice) {
                case 1:
                    try {
                        skiResort = createResort();
                        System.out.println("instantiation successful. Going back to main menu...");
                    } catch(Exception e) {
                        System.out.println("Error while instantiating : " + e);
                    }
                    break;
                case 2:
                    boolean goBack = false;
                    while(!goBack) {
                        System.out.println("=== PERSONS MENU ===");
                        System.out.println("1. register an employee");
                        System.out.println("2. register a guest");
                        System.out.println("3. [WIP] register an instructor");
                        System.out.println("4. go back");
                        choice = sc.nextInt();
                        sc.nextLine();
                        switch (choice) {
                            case 1 -> {
                                Employee.register(sc, skiResort);
                                System.out.println(skiResort.getEmployees());
                            }
                            case 2 ->
                                Guest.register(sc, skiResort);
                            case 3 -> System.out.println("WIP.");
                            case 4 -> {
                                System.out.println("going back...");
                                goBack = true;
                            }
                        }
                    }
                    break;
                case 3:
                    System.out.println("WIP, going back...");
                    break;
                case 4:
                    System.out.println("exiting...");
                    exit = true;
                    break;
            }
        }


    }
}
