package terrain;

import enums.POIStatus;
import enums.Point;
import enums.WorksiteType;
import utils.Worksite;

public class RescuePoint extends POI implements Worksite {
    private boolean warning ;

    public RescuePoint(long id, String name, Point location, SkiArea skiArea, POIStatus status, boolean warning) {
        super(id, name, location, skiArea, status);
        this.warning = warning;
    }

    public boolean isWarning() { return this.warning; }

    public void setWarning(boolean warning) { this.warning = warning; }

    @Override
    public WorksiteType getWorksiteType(){
        return WorksiteType.RESCUE_POINT;
    }

    @Override
    public String toString() {
        return "Rescue Point: warning=" + this.warning + ", " + super.toString();
    }
}
