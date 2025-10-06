package com.utils;

import com.terrain.SkiResort;

import java.util.ArrayList;
import java.util.List;

public class ResortUtils {
    public static <T> List<T> getAllPOIsOfType(SkiResort resort, Class<T> clazz) {
        List<T> l = new ArrayList<>();
        for (var area : resort.getSkiAreas()) {
            for (var poi : area.getPois()) {
                if (clazz.isInstance(poi)) l.add(clazz.cast(poi));
            }
        }
        return l;
    }
}