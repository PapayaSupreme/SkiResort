package com.terrain;

import com.enums.POIStatus;
import com.enums.Point;

public class Restaurant extends POI {

    public Restaurant(String name, int id, Point location, POIStatus status) {
        super(name, id, location, status);
    }
}
