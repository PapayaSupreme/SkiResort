package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import enums.OpeningHours;
import io.github.cdimascio.dotenv.Dotenv;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Scanner;

public final class ResortUtils {
    /*public static <T extends Worksite> List<T> getAllWorksitesOfType(SkiResort resort, Class<T> clazz) {
        List<T> all = new ArrayList<>();

        for (SkiArea area : resort.getSkiAreas()) {
            List<? extends Worksite> list = switch (clazz.getSimpleName()) {
                case "RescuePoint" -> area.getRescuePoints();
                case "Restaurant"  -> area.getRestaurants();
                case "Lift"        -> area.getLifts();
                case "SkiArea"     -> List.of(area);
                default            -> List.of();
            };

            for (Worksite w : list) {
                if (clazz.isInstance(w)) {
                    all.add(clazz.cast(w));
                }
            }
        }
        return all;
    }*/

    public static HikariDataSource makeDataSource() {
        HikariConfig cfg = new HikariConfig();
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String username = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");
        int maxPoolSize = Integer.parseInt(dotenv.get("DB_MAX_POOL_SIZE"));
        int minPoolSize = Integer.parseInt(dotenv.get("DB_MIN_POOL_SIZE"));
        int connectionTimeout = Integer.parseInt(dotenv.get("DB_CONNECTION_TIMEOUT"));
        int idleTimeout = Integer.parseInt(dotenv.get("DB_IDLE_TIMEOUT"));
        int timeToLive = Integer.parseInt(dotenv.get("DB_TTL"));
        cfg.setJdbcUrl(url);
        cfg.setUsername(username);
        cfg.setPassword(password);
        cfg.setMinimumIdle(minPoolSize);
        cfg.setMaximumPoolSize(maxPoolSize);
        cfg.setConnectionTimeout(connectionTimeout);
        cfg.setIdleTimeout(idleTimeout);
        cfg.setMaxLifetime(timeToLive);
        return new HikariDataSource(cfg);
    }

    public static OpeningHours parseOH(String json) {
        if (json == null) return null;
        String j = json.replaceAll("\\s+", ""); //whitespace
        // expects: {"open":"HH:MM","close":"HH:MM"}
        int o1 = j.indexOf("\"open\":\"");  int c1 = j.indexOf("\"", o1 + 8);
        int o2 = j.indexOf("\"close\":\""); int c2 = j.indexOf("\"", o2 + 9);
        return new OpeningHours(
                LocalTime.parse(j.substring(o1 + 8, c1)),
                LocalTime.parse(j.substring(o2 + 9, c2))
        );
    }

    public static String norm(String s) {
        String n = Normalizer.normalize(s.trim(), Normalizer.Form.NFKD);
        n = n.replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.ROOT);
    }

    /**
    * MIN AND MAX ARE INCLUDED
     * IT RETURNS CHOICE
     * !!!NOT CHOICE - 1!!!
     */
    public static int pickInt(Scanner sc, int min, int max){
        int choice = sc.nextInt();
        sc.nextLine();
        while (choice < min || max < choice) {
            System.out.println("Out of bounds, try again.");
            choice = sc.nextInt();
            sc.nextLine();
        }
        return choice;
    }

    public static LocalDate pickDate(Scanner sc, boolean isAfterNow) {
        boolean valid = false;
        LocalDate date = null;
        while (!valid) {
            String dayInput = sc.nextLine();
            try {
                date = LocalDate.parse(dayInput);
                if (date.isBefore(LocalDate.now()) && isAfterNow) {
                    System.out.println("Date must be today or later. Try again.");
                } else {
                    valid = true;
                }
            } catch (Exception e) {
                System.out.println("Invalid date format. Try again.");
            }
        }
        return date;
    }

    public static class ConsoleColors {
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_BLACK = "\u001B[30m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_WHITE = "\u001B[37m";
    }

    public static void runTimer(String action, long t0, long t1){
        long t = t1 - t0;
        System.out.println("\n" + ConsoleColors.ANSI_PURPLE + "TIMER: " + ConsoleColors.ANSI_RESET
                + action+ " done in " + (t / 1000000.0) + "ms.");
    }
}