package tests;
import com.zaxxer.hikari.HikariDataSource;
import factory.ResortBootstrap;
import factory.ResortLoader;
import terrain.*;

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
        int choice1, choice2, choice3; //3var for 3 levels of user input
        String search;
        while (!exit) {
            System.out.println("\n=== MAIN MENU ===\n");
            System.out.println("1. View resort data");
            System.out.println("2. Create pass");
            System.out.println("3. Exit");
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
                        System.out.println("2. [WIP] SELECT IN terrain");
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
                case 3 -> {
                    System.out.println("exiting...");
                    exit = true;
                }
            }
        }


    }
}
