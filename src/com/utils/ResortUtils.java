package com.utils;

import com.people.Worksite;
import com.terrain.SkiArea;
import com.terrain.SkiResort;

import java.util.ArrayList;
import java.util.List;

public class ResortUtils {
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
}