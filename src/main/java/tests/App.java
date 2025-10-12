package tests;
import com.zaxxer.hikari.HikariDataSource;
import enums.EmployeeType;
import factory.ResortBootstrap;
import factory.ResortLoader;
import people.Employee;
import people.Guest;
import people.PersonRepo;
import terrain.*;

import java.time.LocalDate;
import java.util.*;

public final class App {
    public static void main(String[] args) {
        long t0 = System.nanoTime();
        try (HikariDataSource ds = ResortBootstrap.makeDataSource()) {
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
            long t1 = System.nanoTime() - t0;
            System.out.printf(
                    "%s Resort ready in %.2f ms. | Areas=%d, Lifts=%d, Slopes=%d," +
                            "Restaurants=%d, Rescue Points=%d, Summits=%d%n",
                    resort.getResortName(),
                    t1 / 1000000.0,
                    resort.getSkiAreas().size(),
                    resort.getLifts().size(),
                    resort.getSlopes().size(),
                    resort.getRestaurants().size(),
                    resort.getRescuePoints().size(),
                    resort.getSummits().size()
            );
            var repo = new PersonRepo();

            // INSERT
            var g = new Guest("gmail", "Pablo", "ferreira", LocalDate.of(2004,11,11));
            repo.save(g);

            Employee e = new Employee("gmail", "Pablo", "ferreira", LocalDate.of(2004,11,11), EmployeeType.PISTER, 10L);
            repo.save(e);
            Dashboard(resort);

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

    public static void Dashboard(Resort resort) {
        Scanner sc = new Scanner(System.in);
        boolean exit = false;
        boolean goBack = false;
        boolean valid = false;
        String email, firstName, lastName;
        int choice1, choice2, choice3; //3var for 3 levels of user input
        LocalDate dob;
        EmployeeType type = null;
        String search;
        while (!exit) {
            System.out.println("\n=== MAIN MENU ===\n");
            System.out.println("1. View resort data");
            System.out.println("2. Create person");
            System.out.println("3. Create pass");
            System.out.println("4. Exit");
            choice1 = sc.nextInt();
            sc.nextLine();
            goBack = false;
            HashSet<Long> ids;
            switch(choice1) {
                case 1 -> {
                    while (!goBack) {
                        System.out.println("\n=== RESORT MENU ===\n");
                        System.out.println("1. VIEW ALL terrain");
                        System.out.println("2. SEARCH IN terrain");
                        System.out.println("3. [WIP] VIEW ALL persons");
                        System.out.println("4. [WIP] SEARCH IN persons");
                        System.out.println("5. go back");
                        choice2 = sc.nextInt();
                        sc.nextLine();
                        switch(choice2) {
                            case 1 -> System.out.println(resort.toString());
                            case 2 -> {
                                System.out.println("\n=== TERRAIN SEARCH ===\n");
                                System.out.println("Search by name of the structure:\n");
                                search = sc.nextLine();
                                ids = resort.getIdsFromName(search);
                                for (Long id: ids){
                                    System.out.println(resort.getTerrainIndex()
                                            .get(id).toString());
                                }
                                if (Objects.equals(search, "")){
                                    System.out.println("\nNote: your query was empty, so it displayed the whole resort.");
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
                        do {
                            System.out.println("\n=== PERSONS MENU ===\n");
                            System.out.println("1. register an employee");
                            System.out.println("2. register a guest");
                            System.out.println("3. register an instructor");
                            System.out.println("4. go back");
                            choice2 = sc.nextInt();
                            sc.nextLine();
                        } while (choice2<0 || choice2>4);
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
                        switch (choice2) {
                            case 1 -> {
                                valid = false;
                                while (!valid) {
                                    System.out.println("Select employee type");
                                    System.out.println("1. Pister");
                                    System.out.println("2. Lift Operator");
                                    System.out.println("3. Restauration Crew");
                                    System.out.println("4. Maintenance");
                                    choice3 = sc.nextInt();
                                    sc.nextLine();
                                    type = switch (choice3) {
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
                            }
                            case 4 -> {
                                System.out.println("going back...");
                                goBack = true;
                            }
                        }
                    }
                }
                case 3 -> {
                    System.out.println("exiting...");
                    exit = true;
                }
            }
        }


    }
}
