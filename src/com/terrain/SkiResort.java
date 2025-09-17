package com.terrain;

import java.util.ArrayList;
import java.util.List;

public class SkiResort {
    private final String name;
    private final int id;
    private final List<SkiArea> skiAreas = new ArrayList<>();

    public SkiResort(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() { return this.name; }
    public int getId() { return this.id; }
    public List<SkiArea> getSkiAreas() { return List.copyOf(this.skiAreas); }

    public void addSkiArea(SkiArea skiArea) { this.skiAreas.add(skiArea); }

    public boolean removeSkiArea(SkiArea skiArea) { return this.skiAreas.remove(skiArea); }
}
