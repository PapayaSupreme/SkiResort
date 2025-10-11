package tests;
import com.zaxxer.hikari.HikariDataSource;
import factory.ResortBootstrap;
import factory.ResortLoader;
import terrain.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class App {

    public static void main(String[] args) {
        try (HikariDataSource ds = ResortBootstrap.makeDataSource()) {
            var loader   = new ResortLoader(ds);
            var mappers  = ResortBootstrap.makeMappers();

            var snapshot = loader.load(mappers);

            Resort resort = new Resort(
                    cast(snapshot.skiAreas,     SkiArea.class),
                    cast(snapshot.slopes,       Slope.class),
                    cast(snapshot.lifts,        Lift.class),
                    cast(snapshot.restaurants,  Restaurant.class),
                    cast(snapshot.rescuePoints, RescuePoint.class),
                    cast(snapshot.summits,      Summit.class)
            );

            // globally accessible instance
            ResortHolder.set(resort);

            System.out.printf(
                    "Resort ready | Areas=%d, Lifts=%d, Slopes=%d, Restaurants=%d, Rescue=%d, Summits=%d%n",
                    resort.getSkiAreas().size(),
                    resort.getLifts().size(),
                    resort.getSlopes().size(),
                    resort.getRestaurants().size(),
                    resort.getRescuePoints().size(),
                    resort.getSummits().size()
            );

            // plain for-each
            for (var e : resort.getLifts().entrySet()) {
                long id = e.getKey();
                Lift l  = e.getValue();
                System.out.printf("[%d] %s %n", id, l.getName());
            }


        } catch (Exception e) {
            System.err.println("Boot failed: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /** Unsafe but convenient cast from Map<Long,Object> -> Map<Long,T> (we control both ends). */
    @SuppressWarnings("unchecked")
    private static <T> Map<Long, T> cast(Map<Long, Object> m, Class<T> cls) {
        var out = new LinkedHashMap<Long, T>(m.size());
        for (var e : m.entrySet()) out.put(e.getKey(), (T) e.getValue());
        return Collections.unmodifiableMap(out);
    }

    /** Optional holder if other parts of the app need the current Resort quickly. */
    public static final class ResortHolder {
        private static volatile Resort current;
        public static void set(Resort r) { current = r; }
        public static Resort get() { return current; }
    }
}
