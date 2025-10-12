package terrain;

import enums.POIStatus;
import enums.Point;
import enums.TerrainType;
import enums.WorksiteType;
import utils.Worksite;

public class Restaurant extends POI implements Worksite, Terrain {

    public Restaurant(long id, String name, Point location, SkiArea skiArea, POIStatus status) {
        super(id, name, location, skiArea, status);
    }

    @Override
    public WorksiteType getWorksiteType(){
        return WorksiteType.RESTAURANT;
    }

    @Override
    public TerrainType getTerrainType(){
        return TerrainType.Restaurant;
    }

    @Override
    public String toString() {
        return "Restaurant: " + super.toString();
    }
}
