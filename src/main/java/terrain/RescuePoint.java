package terrain;

import enums.Point;
import enums.WorksiteType;
import utils.Worksite;

public class RescuePoint extends POI implements Worksite {
    private boolean warning = false;

    public RescuePoint(String name, Point location, SkiArea skiArea) {
        super(name, location, skiArea);
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
