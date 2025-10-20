package tests;

import com.zaxxer.hikari.HikariDataSource;
import enums.PassKind;
import enums.PersonKind;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.*;

import enums.EmployeeType;
import enums.SkiSchool;

import factory.ResortBootstrap;
import factory.ResortLoader;
import passes.*;
import people.*;
import terrain.*;
import utils.JPA;
import utils.ResortUtils;

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
            PassRepo passRepo = new PassRepo(entityManagerFactory);
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

            Dashboard(resort, personRepo, passRepo);

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




    public static void Dashboard(Resort resort, PersonRepo personRepo, PassRepo passRepo) {
        Scanner sc = new Scanner(System.in);
        long t0, t1;
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        Optional<Person> personOptional;
        Person person;
        boolean exit = false;
        boolean goBack = false;
        boolean valid = false;
        String email = "";
        String firstName = "";
        String lastName = "";;
        int choice1, choice2, choice3; //3var for 3 levels of user input
        LocalDate day = LocalDate.of(2010,10,10);
        EmployeeType employeeType = null;
        String search;
        List<? extends Worksite> worksites;
        Worksite worksite = null;
        HashSet<Long> ids;
        Employee employee = null;
        List<Pass> passes = new ArrayList<>();
        List<Lift> lifts = new ArrayList<>();
        Guest guest = null;
        Pass pass;
        Lift lift;
        Instructor instructor = null;
        SkiSchool skiSchool = null;
        List<Person> persons= new ArrayList<>();
        List<Guest> guests= new ArrayList<>();
        List<Instructor> instructors= new ArrayList<>();
        List<Employee> employees= new ArrayList<>();
        DayPass dayPass;
        MultiDayPass multiDayPass;
        SeasonPass seasonPass;
        ALaCartePass aLaCartePass;
        while (!exit) {
            System.out.println(ConsoleColors.ANSI_BLUE + "\n=== MAIN MENU ===\n" + ConsoleColors.ANSI_RESET);
            System.out.println("1. View resort data");
            System.out.println("2. Create person");
            System.out.println("3. Create pass");
            System.out.println("4. Emulate IRL actions");
            System.out.println("0. EXIT");
            choice1 = pickInt(sc, 0, 4);
            goBack = false;
            switch(choice1) {
                case 1 -> {
                    while (!goBack) {
                        System.out.println(ConsoleColors.ANSI_BLUE + "\n=== RESORT MENU ===\n" + ConsoleColors.ANSI_RESET);
                        System.out.println("1. VIEW ALL terrain");
                        System.out.println("2. SEARCH IN terrain");
                        System.out.println("3. VIEW ALL persons");
                        System.out.println("4. SEARCH IN persons");
                        System.out.println("5. VIEW ALL passes");
                        System.out.println("6. SEARCH IN passes");
                        System.out.println("0. GO BACK");
                        choice2 = pickInt(sc, 0, 6);
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
                                System.out.println(ConsoleColors.ANSI_BLUE + "\n=== TERRAIN SEARCH ===\n" + ConsoleColors.ANSI_RESET);
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
                                    System.out.println("\n===Note: your query was empty, so it displayed the whole resort.===");
                                }
                            }


                            case 3 -> {
                                System.out.println("This is a heavy request. Proceed ?");
                                System.out.println("1. Yes, proceed");
                                System.out.println("0. CANCEL");
                                choice3 = pickInt(sc, 0, 1);
                                if (choice3==1) {
                                    t0 = System.nanoTime();
                                    persons = personRepo.findAll();
                                    for (Person p : persons) {
                                        System.out.println(p.toString());
                                        if (p.getPersonKind() == PersonKind.EMPLOYEE) {
                                            count1++;
                                        } else if (p.getPersonKind() == PersonKind.INSTRUCTOR) {
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
                                } else {
                                    System.out.println("Cancelling...");
                                }
                            }


                            case 4 -> {
                                System.out.println("Select the person's info to search from: ");
                                System.out.println("1. Email (exact match)");
                                System.out.println("2. Last name (exact/partial match)");
                                System.out.println("0. CANCEL");
                                choice3 = pickInt(sc, 0, 2);
                                if (choice3 == 1){
                                    System.out.println("Enter email: ");
                                    email = sc.next();
                                    sc.nextLine();
                                    t0 = System.nanoTime();
                                    personOptional = personRepo.findByEmail(email);
                                    t1 = System.nanoTime();
                                    runTimer("Person match from email", t0, t1);
                                    if (personOptional.isPresent()) {
                                        personOptional.get().displayFullInfo(passRepo);
                                    } else {
                                        System.out.println("Person not found");
                                    }
                                } else if (choice3 == 2){
                                    person = Person.findByNameGUI(sc, personRepo, Person.class); //TODO: when clicked,  ask to edit ?
                                    if (person != null){
                                        person.displayFullInfo(passRepo);
                                    }
                                } else{
                                    System.out.println("Cancelling...");
                                }
                            }


                            case 5 -> {
                                System.out.println("This is a heavy request. Proceed ?");
                                System.out.println("1. Yes, proceed");
                                System.out.println("0. CANCEL");
                                choice3 = pickInt(sc, 0, 1);
                                if (choice3==1) {
                                    passes = passRepo.findAllPasses();
                                    count1 = 0;
                                    count2 = 0;
                                    count3 = 0;
                                    for(Pass p: passes){
                                        if (p.getPassKind() == PassKind.DAY){
                                            count1++;
                                        } else if (p.getPassKind() == PassKind.MULTIDAY) {
                                            count2++;
                                        } else if (p.getPassKind() == PassKind.SEASON) {
                                            count3++;
                                        }
                                        System.out.println(p.toString());
                                    }
                                    System.out.printf("""
                                                    
                                                    Total passes: %d
                                                    Details: %d day passes, %d multi-day passes, %d season passes, %d a la carte passes.
                                                    """,
                                            passes.size(), count1, count2, count3, (passes.size() - count1 - count2 - count3));
                                } else {
                                    System.out.println("Cancelling...");
                                }
                            }


                            case 6 -> {
                                System.out.println("Select the pass criteria to search from: ");
                                System.out.println("1. Pass kind (Day, Season...)");
                                System.out.println("2. Pass validity on a date");
                                System.out.println("3. Pass issued to non-guests (Employees, Instructors)");
                                System.out.println("0. CANCEL");
                                choice3 = pickInt(sc, 0, 3);
                                switch(choice3) {
                                    case 1 -> {
                                        System.out.println("Select what kind of pass to display: ");
                                        System.out.println("1. Day Pass");
                                        System.out.println("2. Multi-Day Pass");
                                        System.out.println("3. Season Pass");
                                        System.out.println("4. A la Carte Pass (Pay-per-use)");
                                        System.out.println("0. CANCEL");
                                        choice3 = pickInt(sc, 0, 4);
                                        switch (choice3) {
                                            case 1 -> Pass.displayPassesOfKind(passRepo, PassKind.DAY);

                                            case 2 -> Pass.displayPassesOfKind(passRepo, PassKind.MULTIDAY);

                                            case 3 -> Pass.displayPassesOfKind(passRepo, PassKind.SEASON);

                                            case 4 -> Pass.displayPassesOfKind(passRepo, PassKind.ALACARTE);

                                            case 0 -> System.out.println("Cancelling...");
                                        }
                                    }

                                    case 2 -> {
                                        System.out.println("Enter the desired date to display valid passes (YYYY-MM-DD): ");
                                        day = pickDate(sc, true);
                                        Pass.displayPassesValidAt(passRepo, day);
                                    }

                                    case 3 -> Pass.displaySpecialPasses(passRepo);

                                    case 0 -> System.out.println("Cancelling...");
                                }
                            }


                            case 0 -> {
                                System.out.println("Going back...");
                                goBack = true;
                        }
                        }
                    }
                }



                case 2 -> {
                    while (!goBack) {
                        System.out.println(ConsoleColors.ANSI_BLUE + "\n=== PERSONS MENU ===\n" + ConsoleColors.ANSI_RESET);
                        System.out.println("1. Register an employee");
                        System.out.println("2. Register a guest");
                        System.out.println("3. Register an instructor");
                        System.out.println("0. GO BACK");
                        choice2 = pickInt(sc, 0, 3);
                        if (choice2 != 0){
                            System.out.print("Enter email: ");
                            email = sc.nextLine();

                            System.out.print("Enter first name: ");
                            firstName = sc.nextLine();

                            System.out.print("Enter last name: ");
                            lastName = sc.nextLine();

                            System.out.print("Enter date of birth, format YYYY-MM-DD : ");
                            day = pickDate(sc, false);
                        }
                        switch (choice2) {
                            case 1 -> {
                                System.out.println("Select employee type");
                                System.out.println("1. Pister");
                                System.out.println("2. Lift Operator");
                                System.out.println("3. Restauration Crew");
                                System.out.println("4. Maintenance");
                                System.out.println("0. CANCEL");
                                choice3 = pickInt(sc, 0, 4);
                                employeeType = switch (choice3) {
                                    case 1 -> EmployeeType.PISTER;
                                    case 2 -> EmployeeType.LIFT_OP;
                                    case 3 -> EmployeeType.RESTAURATION;
                                    case 4 -> EmployeeType.MAINTENANCE;
                                    default -> {
                                        System.out.println("Cancelling...");
                                        yield null;
                                    }
                                };
                                if (employeeType != null) {
                                    worksites = switch (employeeType) {
                                        case PISTER -> new ArrayList<>(resort.getRescuePoints().values());
                                        case LIFT_OP -> new ArrayList<>(resort.getLifts().values());
                                        case RESTAURATION -> new ArrayList<>(resort.getRestaurants().values());
                                        case MAINTENANCE -> new ArrayList<>(resort.getSkiAreas().values());
                                    };
                                    System.out.println("Select employee worksite (adapted to employee type): ");
                                    for (int i = 0; i < worksites.size(); i++) {
                                        System.out.println((i + 1) + ". " + worksites.get(i));
                                    }
                                    System.out.println("0. CANCEL");
                                    choice3 = pickInt(sc, 0, worksites.size()) - 1;
                                    if (choice3 != -1) {
                                        worksite = worksites.get(choice3);
                                        employee = Person.createEmployee(personRepo, email, firstName, lastName, day, employeeType, worksite.getId());
                                    } else {
                                        System.out.println("Cancelling...");
                                    }
                                }
                            }


                            case 2 -> guest = Person.createGuest(personRepo, email, firstName, lastName, day);


                            case 3 ->{
                                System.out.println("Select instructor ski school");
                                for (SkiSchool s: SkiSchool.values()){
                                    System.out.println((s.ordinal()+1) + ". " + s.name());
                                }
                                System.out.println("0. CANCEL");
                                choice3 = pickInt(sc, 0, SkiSchool.values().length)-1;
                                if (choice3 != -1) {
                                    skiSchool = SkiSchool.values()[choice3];

                                    worksites = new ArrayList<>(resort.getSkiAreas().values());
                                    System.out.println("Select instructor worksite: ");
                                    for (int i = 0; i < worksites.size(); i++) {
                                        System.out.println((i + 1) + ". " + worksites.get(i));
                                    }
                                    System.out.println("0. CANCEL");
                                    choice3 = pickInt(sc, 0, worksites.size()) - 1;
                                    if (choice3 != -1) {
                                        worksite = worksites.get(choice3);

                                        instructor = Person.createInstructor(personRepo, email, firstName, lastName, day, skiSchool, worksite.getId());
                                    } else {
                                        System.out.println("Cancelling...");
                                    }
                                } else {
                                    System.out.println("Cancelling...");
                                }
                            }


                            case 0 -> {
                                System.out.println("Going back...");
                                goBack = true;
                            }
                        }
                    }
                }



                case 3 ->{
                    while (!goBack) {
                        System.out.println(ConsoleColors.ANSI_BLUE + "\n=== PASS MENU ===\n" + ConsoleColors.ANSI_RESET);
                        System.out.println("1. Create a guest pass");
                        System.out.println("2. Create an employee pass");
                        System.out.println("3. Create an instructor pass");
                        System.out.println("0. GO BACK");
                        choice2 = pickInt(sc, 0, 3);
                        switch (choice2) {
                            case 1 -> {//TODO: cleanup that
                                guest = Person.findByNameGUI(sc, personRepo, Guest.class);
                                if (guest != null) {
                                    System.out.println("\nSelect Pass Kind: \n");
                                    System.out.println("1. Day Pass");
                                    System.out.println("2. Multi-Day Pass");
                                    System.out.println("3. Season Pass");
                                    System.out.println("4. A la Carte Pass (Pay-per-use)");
                                    System.out.println("0. Cancel");
                                    choice3 = pickInt(sc, 0, 4);
                                    switch (choice3) {
                                        case 1 -> {
                                            System.out.print("Enter the valid date of the day pass, format YYYY-MM-DD : ");
                                            day = pickDate(sc, true);
                                            dayPass = Pass.createDayPass(passRepo, guest, day);
                                        }

                                        case 2 -> {
                                            System.out.print("Enter the start date of the multi-day pass, format YYYY-MM-DD : ");
                                            day = pickDate(sc, true);

                                            System.out.print("How much day will this multi-day pass be valid for ? (min 2, max 30) : ");
                                            choice3 = pickInt(sc, 2, 30);
                                            multiDayPass = Pass.createMultiDayPass(passRepo, guest, day, day.plusDays(choice3-1));
                                        }

                                        case 3 -> seasonPass = Pass.createSeasonPass(passRepo, guest);

                                        case 4 -> aLaCartePass = Pass.createALaCartePass(passRepo, guest);

                                        case 0 -> System.out.println("Cancelling...");
                                    }
                                }
                            }


                            case 2 ->{
                                employee = Person.findByNameGUI(sc, personRepo, Employee.class);
                                if (employee != null) {
                                    passes = passRepo.findValidPasses(employee);
                                    if (!passes.isEmpty()) {
                                        System.out.println("This employee already has a valid employee pass: ");
                                        System.out.println("Employee " + passes.getFirst());
                                    } else {
                                        Pass.createSeasonPass(passRepo, employee);
                                    }
                                }
                            }


                            case 3->{
                                instructor = Person.findByNameGUI(sc, personRepo, Instructor.class);
                                if (instructor != null) {
                                    passes = passRepo.findValidPasses(instructor);
                                    if (!passes.isEmpty()) {
                                        System.out.println("This instructor already has a valid instructor pass: ");
                                        System.out.println("Instructor " + passes.getFirst());
                                    } else {
                                        System.out.println("Choose instructor pass type:");
                                        System.out.println("1. Season Pass (One time payment)");
                                        System.out.println("2. A La Carte Pass (Pay-per-use)");
                                        System.out.println("0. Cancel");
                                        choice3 = pickInt(sc, 0, 2);
                                        switch (choice3) {
                                            case 1 -> Pass.createSeasonPass(passRepo, instructor);//TODO: create printPass method and ask it always
                                            case 2 -> Pass.createALaCartePass(passRepo, instructor);
                                            case 0 -> System.out.println("Cancelling...");
                                        }
                                    }
                                }
                            }


                            case 0 -> {
                                System.out.println("going back...");
                                goBack = true;
                            }
                        }
                    }
                }



                case 4 -> {
                    while (!goBack) {
                        System.out.println(ConsoleColors.ANSI_BLUE + "\n=== EMULATION MENU ===\n" + ConsoleColors.ANSI_RESET);
                        System.out.println("1. Log pass usage");
                        System.out.println("0. GO BACK");
                        choice2 = pickInt(sc, 0, 1);
                        switch (choice2) {
                            case 1 ->{
                                System.out.println("This is a heavy request. Proceed ?");
                                System.out.println("1. Yes, proceed");
                                System.out.println("0. CANCEL");
                                choice3 = pickInt(sc, 0, 1);
                                if (choice3==1) {
                                    passes = passRepo.findAllPasses();
                                    for (int i = 0; i<passes.size(); i++){
                                        System.out.println(i+1 + ". " + passes.get(i));
                                    }
                                    System.out.println("0. CANCEL");
                                    System.out.println("\nChoose the pass to log a use on: ");
                                    choice3 = pickInt(sc, 0, passes.size()) - 1;
                                    if (choice3 != -1){
                                        pass = passes.get(choice3);
                                        lifts = new ArrayList<>(resort.getLifts().values());
                                        for (int i = 0; i < lifts.size(); i++){
                                            System.out.println(i+1 + ". " + lifts.get(i).getName());
                                        }
                                        System.out.println("0. CANCEL");
                                        System.out.println("Choose the lift to log a use from: ");
                                        choice3 = pickInt(sc, 0, passes.size()) - 1;
                                        if (choice3 != -1){
                                            lift = lifts.get(choice3);
                                            passRepo.logUse(pass, lift);
                                        } else {
                                            System.out.println("Cancelling...");
                                        }
                                    } else {
                                        System.out.println("Cancelling...");
                                    }
                                }

                            }
                            case 0 -> {
                                System.out.println("going back...");
                                goBack = true;
                            }
                        }
                    }
                }



                case 0 -> {
                    System.out.println("Exiting...");
                    exit = true;
                }
            }
        }
    }
}
