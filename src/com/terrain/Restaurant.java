package com.terrain;

import com.enums.Point;
import com.people.Worksite;

public class Restaurant extends POI implements Worksite {

    public Restaurant(String name, int id, Point location, SkiArea skiArea) {
        super(name, id, location, skiArea);
    }

    @Override
    public String getWorksiteType(){
        return "Restaurant";
    }

    @Override
    public String toString() {
        return "Restaurant: " + super.toString();
    }
}
