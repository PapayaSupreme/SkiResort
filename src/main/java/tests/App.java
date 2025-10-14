package tests;

import com.zaxxer.hikari.HikariDataSource;
import enums.PersonKind;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.*;

import enums.EmployeeType;
import enums.SkiSchool;

import factory.ResortBootstrap;
import factory.ResortLoader;
import people.*;
import terrain.*;
import utils.JPA;

import static utils.ResortUtils.*;

public final class App {
    public static void main(String[] args) {
        long t0 = System.nanoTime();
        try (HikariDataSource ds = makeDataSource()) {
            var loader   = new ResortLoader(ds);
            var mappers  = ResortBootstrap.makeMappers();

            var snapshot = loader.load(mappers);

            Resort resort = new Resort(
                    "Serre Chevalier",
                    cast(snapshot.skiAreas,     SkiArea.class),
                    cast(snapshot.slopes,       Slope.class),
                    cast(snapshot.lifts,        Lift.class),
                    cast(snapshot.restaurants,  Restaurant.class),
                    cast(snapshot.rescuePoints, RescuePoint.class),
                    cast(snapshot.summits,      Summit.class)
            );

            JPA.init(ds);

            EntityManagerFactory entityManagerFactory = JPA.emf();
            PersonRepo personRepo = new PersonRepo(entityManagerFactory);
            long t1 = System.nanoTime();
            System.out.printf(
                    "%s Resort ready | Areas=%d, Lifts=%d, Slopes=%d," +
                            "Restaurants=%d, Rescue Points=%d, Summits=%d%n",
                    resort.getResortName(),
                    resort.getSkiAreas().size(),
                    resort.getLifts().size(),
                    resort.getSlopes().size(),
                    resort.getRestaurants().size(),
                    resort.getRescuePoints().size(),
                    resort.getSummits().size()
            );
            runTimer("Resort start setup", t0, t1);

            Dashboard(resort, personRepo);

        } catch (Exception e) {
            System.err.println("Boot failed: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }

    }

    /** Unsafe but convenient cast from Map<Long,Object> -> Map<Long,T> (controlled both ends still). */
    @SuppressWarnings("unchecked")
    private static <T> Map<Long, T> cast(Map<Long, Object> m, Class<T> cls) {
        var out = new LinkedHashMap<Long, T>(m.size());
        for (var e : m.entrySet()) out.put(e.getKey(), (T) e.getValue());
        return Collections.unmodifiableMap(out);
    }




    public static void Dashboard(Resort resort, PersonRepo personRepo) {
        Scanner sc = new Scanner(System.in);
        long t0, t1;
        int count1 = 0;
        int count2 = 0;
        Optional<Person> person;
        boolean exit = false;
        boolean goBack = false;
        boolean valid = false;
        String email = "";
        String firstName = "";
        String lastName = "";;
        int choice1, choice2, choice3; //3var for 3 levels of user input
        LocalDate dob = LocalDate.of(2010,10,10);
        EmployeeType employeeType = null;
        String search;
        List<? extends Worksite> worksites;
        Worksite worksite = null;
        HashSet<Long> ids;
        Employee employee = null;
        Guest guest = null;
        Instructor instructor = null;
        SkiSchool skiSchool = null;
        List<Person> persons= new ArrayList<>();
        while (!exit) {
            System.out.println("\n=== MAIN MENU ===\n");
            System.out.println("1. View resort data");
            System.out.println("2. Create person");
            System.out.println("3. [WIP] Create pass");
            System.out.println("4. EXIT");
            choice1 = sc.nextInt();
            sc.nextLine();
            goBack = false;
            switch(choice1) {
                case 1 -> {
                    while (!goBack) {
                        System.out.println("\n=== RESORT MENU ===\n");
                        System.out.println("1. VIEW ALL terrain");
                        System.out.println("2. SEARCH IN terrain");
                        System.out.println("3. VIEW ALL persons");
                        System.out.println("4. SEARCH IN persons");
                        System.out.println("5. go back");
                        choice2 = pickInt(sc, 1, 5);
                        switch(choice2) {
                            case 1 -> {
                                t0 = System.nanoTime();
                                System.out.println(resort.toString());
                                System.out.printf("""
                                                Total terrain: %d
                                                (%d ski areas, %d slopes, %d lifts,
                                                %d restaurants, %d rescue points, %d summits)
                                                """,
                                        resort.getTerrainIndex().size(),
                                        resort.getSkiAreas().size(),
                                        resort.getSlopes().size(),
                                        resort.getLifts().size(),
                                        resort.getRestaurants().size(),
                                        resort.getRescuePoints().size(),
                                        resort.getSummits().size());
                                t1 = System.nanoTime();
                                runTimer("Total terrain display from instances", t0, t1);
                            }


                            case 2 -> {
                                System.out.println("\n=== TERRAIN SEARCH ===\n");
                                System.out.println("Search by name of the structure:\n");
                                search = sc.nextLine();
                                t0 = System.nanoTime();
                                ids = resort.getIdsFromName(search);
                                for (Long id: ids){
                                    System.out.println(resort.getTerrainIndex()
                                            .get(id).toString());
                                }
                                t1 = System.nanoTime();
                                runTimer("Terrain name search query", t0, t1);
                                if (Objects.equals(search, "")){
                                    System.out.println("\nNote: your query was empty, so it displayed the whole resort.");
                                }
                            }


                            case 3 -> {
                                t0 = System.nanoTime();
                                persons = personRepo.findAll();
                                for (Person p: persons){
                                    System.out.println(p.toString());
                                    if (p.getPersonKind() == PersonKind.EMPLOYEE){
                                        count1++;
                                    } else if(p.getPersonKind() == PersonKind.INSTRUCTOR){
                                        count2++;
                                    }
                                }
                                System.out.printf("""
                                                
                                                Total persons: %d
                                                Details: %d employees, %d instructors, %d guests.
                                                """,
                                persons.size(), count1, count2, (persons.size() - count1 - count2));
                                t1 = System.nanoTime();
                                runTimer("Total person display from DB", t0, t1);
                                count1 = 0;
                                count2 = 0;
                            }


                            case 4 -> {
                                System.out.println("Select the person's info to search from: ");
                                System.out.println("1. Email (exact match)");
                                System.out.println("2. Last name (exact/partial match)");
                                choice3 = pickInt(sc, 1, 2);
                                if (choice3 == 1){
                                    System.out.println("Enter email: ");
                                    email = sc.next();
                                    sc.nextLine();
                                    t0 = System.nanoTime();
                                    person = personRepo.findByEmail(email);
                                    t1 = System.nanoTime();
                                    if (person.isPresent()) {
                                        System.out.println("Person found. \n" + person.get().toString());
                                    } else {
                                        System.out.println("Person not found");
                                    }
                                    runTimer("Person match from email", t0, t1);
                                } else{
                                    System.out.println("Enter last name, or a part of it: ");
                                    lastName = sc.nextLine();
                                    t0 = System.nanoTime();
                                    persons = personRepo.findByLastName(lastName);
                                    t1 = System.nanoTime();
                                    if (!persons.isEmpty()) {
                                        if (persons.size() == 1) {
                                            System.out.println(persons.size() + " person was found: \n");
                                        } else {
                                            System.out.println(persons.size() + " persons were found: \n");
                                        }
                                        for (Person p: persons){
                                            System.out.println(p.toString());
                                        }
                                    } else {
                                        System.out.println("No match was found");
                                    }
                                    runTimer("Person match from email", t0, t1);
                                }
                            }


                            case 5 -> {
                                System.out.println("going back...");
                                goBack = true;}
                        }
                    }
                }



                case 2 -> {
                    while (!goBack) {
                        System.out.println("\n=== PERSONS MENU ===\n");
                        System.out.println("1. Register an employee");
                        System.out.println("2. Register a guest");
                        System.out.println("3. Register an instructor");
                        System.out.println("4. GO BACK");
                        choice2 = pickInt(sc, 1, 4);
                        if (choice2 != 4){
                            System.out.print("Enter email: ");
                            email = sc.nextLine();

                            System.out.print("Enter first name: ");
                            firstName = sc.nextLine();

                            System.out.print("Enter last name: ");
                            lastName = sc.nextLine();

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
                        }
                        switch (choice2) {
                            case 1 -> {
                                System.out.println("Select employee type");
                                System.out.println("1. Pister");
                                System.out.println("2. Lift Operator");
                                System.out.println("3. Restauration Crew");
                                System.out.println("4. Maintenance");
                                choice3 = pickInt(sc, 1, 4);
                                employeeType = switch (choice3) {
                                    case 1 -> EmployeeType.PISTER;
                                    case 2 -> EmployeeType.LIFT_OP;
                                    case 3 -> EmployeeType.RESTAURATION;
                                    case 4 -> EmployeeType.MAINTENANCE;
                                    default -> {
                                        System.out.println("Invalid choice, try again.");
                                        yield null;
                                    }
                                };

                                assert employeeType != null;
                                worksites = switch (employeeType) {
                                    case PISTER -> new ArrayList<>(resort.getRescuePoints().values());
                                    case LIFT_OP -> new ArrayList<>(resort.getLifts().values());
                                    case RESTAURATION -> new ArrayList<>(resort.getRestaurants().values());
                                    case MAINTENANCE -> new ArrayList<>(resort.getSkiAreas().values());
                                };
                                System.out.println("Select employee worksite (adapted to employee type): ");
                                for (int i = 0;i<worksites.size();i++){
                                    System.out.println((i+1) + ". " + worksites.get(i));
                                }
                                choice3 = pickInt(sc, 1, worksites.size())-1;
                                worksite = worksites.get(choice3);

                                try {
                                    employee = new Employee(email, firstName, lastName,
                                            dob, employeeType, worksite.getId());
                                    try {
                                        personRepo.save(employee);
                                        System.out.println("Successfully saved employee : " + employee.toString());
                                    } catch (Exception e){
                                        System.out.println("Error while saving employee to the DB : " + e);
                                    }

                                } catch (Exception e){
                                    System.out.println("Error while instantiating employee : " + e);
                                }
                            }


                            case 2 ->{
                                try {
                                    guest = new Guest(email, firstName, lastName, dob);
                                    try {
                                        personRepo.save(guest);
                                        System.out.println("Successfully saved guest : " + guest.toString());
                                    } catch (Exception e){
                                        System.out.println("Error while saving guest to the DB : " + e);
                                    }
                                } catch (Exception e){
                                    System.out.println("Error while instantiating guest : " + e);
                                }
                            }


                            case 3 ->{
                                System.out.println("Select instructor ski school");
                                for (SkiSchool s: SkiSchool.values()){
                                    System.out.println((s.ordinal()+1) + ". " + s.name());
                                }
                                choice3 = pickInt(sc, 1, SkiSchool.values().length)-1;
                                skiSchool = SkiSchool.values()[choice3];

                                worksites = new ArrayList<>(resort.getSkiAreas().values());
                                System.out.println("Select instructor worksite: ");
                                for (int i = 0;i<worksites.size();i++){
                                    System.out.println((i+1) + ". " + worksites.get(i));
                                }
                                choice3 = pickInt(sc, 1, worksites.size())-1;
                                worksite = worksites.get(choice3);

                                try {
                                    instructor = new Instructor(email, firstName, lastName,
                                            dob, skiSchool, worksite.getId());
                                    try {
                                        personRepo.save(instructor);
                                        System.out.println("Successfully saved instructor : " + instructor.toString());
                                    } catch (Exception e){
                                        System.out.println("Error while saving instructor to the DB : " + e);
                                    }

                                } catch (Exception e){
                                    System.out.println("Error while instantiating instructor : " + e);
                                }
                            }


                            case 4 -> {
                                System.out.println("going back...");
                                goBack = true;
                            }
                        }
                    }
                }



                case 4 -> {
                    System.out.println("exiting...");
                    exit = true;
                }
            }
        }


    }
}
