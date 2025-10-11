package utils;

import enums.OpeningHours;
import terrain.SkiArea;
import terrain.SkiResort;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class ResortUtils {
    public static <T extends Worksite> List<T> getAllWorksitesOfType(SkiResort resort, Class<T> clazz) {
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
    }

    public static OpeningHours parseOH(String json) {
        if (json == null) return null;
        String j = json.replaceAll("\\s+", "");
        // expects: {"open":"HH:MM","close":"HH:MM"}
        int o1 = j.indexOf("\"open\":\"");  int c1 = j.indexOf("\"", o1 + 8);
        int o2 = j.indexOf("\"close\":\""); int c2 = j.indexOf("\"", o2 + 9);
        return new OpeningHours(
                LocalTime.parse(j.substring(o1 + 8, c1)),
                LocalTime.parse(j.substring(o2 + 9, c2))
        );
    }

}