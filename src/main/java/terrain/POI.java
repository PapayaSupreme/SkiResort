package terrain;

import enums.POIStatus;
import enums.Point;

import java.util.UUID;

public class POI {
    private final String name;
    private final long id;
    private UUID publicId;
    private final Point location;
    private POIStatus status;
    private final SkiArea skiArea;

    public POI(long id, String name, Point location, SkiArea skiArea, POIStatus status) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.skiArea = skiArea;
        this.status = status;
    }

    public String getName() { return this.name; }
    public long getId() { return this.id; }
    public Point getLocation() { return this.location; }
    public POIStatus getStatus() { return this.status; }
    public SkiArea getSkiArea() { return skiArea; }


    public void setStatus(POIStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "name=" + this.name + ", id=" + this.id+ ", location="
                + this.location.toString() + ", status=" + this.status
                + ", ski area=" + this.skiArea.getName();
    }
}
