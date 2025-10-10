package tests;

import terrain.SkiResort;

import java.util.Scanner;

import static factory.SerreChe.createResort;

public class MainTest {
    public static void mainTest(String[] args) {
        Scanner sc = new Scanner(System.in);
        SkiResort skiResort = null;
        boolean exit = false;
        boolean goBack = false;
        int choice1, choice2, choice3; //3var for 3 levels of user input
        while (!exit) {
            System.out.println("\n=== MAIN MENU ===\n");
            System.out.println("1. instantiate with default resort");
            System.out.println("2. View current resort");
            System.out.println("3. register a person");
            System.out.println("4. create pass");
            System.out.println("5. exit");

            choice1 = sc.nextInt();
            sc.nextLine();
            goBack = false;
            if (skiResort == null && choice1 !=1){
                choice1 = 1;
                System.out.println("You tried to access resort-related options without creating one.\n" +
                        "Instantiating with default resort now...");
            }
            switch(choice1) {
                case 1 -> {
                    try {
                        skiResort = createResort();
                        System.out.println("instantiation successful. Going back to main menu...");
                    } catch (Exception e) {
                        System.out.println("Error while instantiating : " + e);
                    }
                }
                case 2 -> {
                    while (!goBack) {
                        System.out.println("\n=== RESORT MENU ===\n");
                        System.out.println("1. view ALL terrain");
                        System.out.println("2. view ALL persons");
                        System.out.println("4. view SPECIFIC");
                        System.out.println("6. go back");
                        choice2 = sc.nextInt();
                        sc.nextLine();
                        switch(choice2) {
                            case 1 -> System.out.println(skiResort.toString());
                            case 6 -> {
                                System.out.println("going back...");
                                goBack = true;}
                        }
                        }
                }
                case 3 -> {
                    while (!goBack) {
                        System.out.println("\n=== PERSONS MENU ===\n");
                        System.out.println("1. register an employee");
                        System.out.println("2. register a guest");
                        System.out.println("3. register an instructor");
                        System.out.println("4. go back");
                        choice2 = sc.nextInt();
                        sc.nextLine();
                        switch (choice2) {
                            case 4 -> {
                                System.out.println("going back...");
                                goBack = true;
                            }
                        }
                    }
                }
                case 4 -> {
                    System.out.println("WIP, going back...");
                }
                case 5 -> {
                    System.out.println("exiting...");
                    exit = true;
                }
            }
        }


    }
}
