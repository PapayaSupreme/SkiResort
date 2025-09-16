package com.skiresort.terrain;

import java.util.ArrayList;
import java.util.List;

public class SkiResort {
    private final String name;
    private final int id;
    private final List<SkiArea> areas = new ArrayList<>();

    public SkiResort(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() { return name; }
    public int getId() { return id; }
    public List<SkiArea> getAreas() { return List.copyOf(areas); }

    public void addArea(SkiArea area) { areas.add(area); }
}
