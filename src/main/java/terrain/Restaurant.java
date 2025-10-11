package terrain;

import enums.POIStatus;
import enums.Point;
import enums.WorksiteType;
import utils.Worksite;

public class Restaurant extends POI implements Worksite {

    public Restaurant(long id, String name, Point location, SkiArea skiArea, POIStatus status) {
        super(id, name, location, skiArea, status);
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
