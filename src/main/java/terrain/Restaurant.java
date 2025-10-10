package terrain;

import enums.Point;
import enums.WorksiteType;
import utils.Worksite;

public class Restaurant extends POI implements Worksite {

    public Restaurant(String name, Point location, SkiArea skiArea) {
        super(name, location, skiArea);
    }

    @Override
    public WorksiteType getWorksiteType(){
        return WorksiteType.RESTAURANT;
    }

    @Override
    public String toString() {
        return "Restaurant: " + super.toString();
    }
}
