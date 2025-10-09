package com.terrain;

import com.enums.Point;
import com.utils.Worksite;

public class Restaurant extends POI implements Worksite {

    public Restaurant(String name, Point location, SkiArea skiArea) {
        super(name, location, skiArea);
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
