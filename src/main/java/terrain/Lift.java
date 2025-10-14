package terrain;

import enums.*;

import java.util.UUID;

public class Lift implements Terrain, Worksite {
    private final long id;
    private UUID publicId;
    private final String name;
    private LiftType type;
    private LiftStatus status;
    private final SkiArea skiArea;
    private final Point up;
    private final Point down;
    private final double length;
    private OpeningHours openingHours;
    private Slope upSlope;
    private Slope downSlope;

    public Lift(long id, UUID publicId, String name, Point up, Point down,
                double length, OpeningHours openingHours,
                LiftType type, LiftStatus status, Slope upSlope, Slope downSlope,
                SkiArea skiArea) {
        this.name = name;
        this.id = id;
        this.publicId = publicId;
        this.up = up;
        this.down = down;
        this.length = length;
        this.openingHours = openingHours;
        this.type = type;
        this.status = status;
        this.upSlope = upSlope;
        this.downSlope = downSlope;
        this.skiArea = skiArea;

    }
    @Override public String getName() { return this.name; }
    @Override public long getId() { return this.id; }
    public Point getUp() { return this.up; }
    public Point getDown() { return this.down; }
    public double getLength() { return this.length; }
    public OpeningHours getOpeningHours() { return this.openingHours; }
    public LiftType getType() { return this.type; }
    public LiftStatus getStatus() { return this.status; }
    public Slope getUpSlope() { return this.upSlope; }
    public Slope getDownSlope() { return this.downSlope; }
    public SkiArea getSkiArea() { return skiArea; }

    public void setType(LiftType type) { this.type = type; }
    public void setStatus(LiftStatus status) { this.status = status; }
    public void setUpSlope(Slope upSlope) { this.upSlope = upSlope; }
    public void setDownSlope(Slope downSlope) { this.downSlope = downSlope; }
    public void setOpeningHours(OpeningHours openingHours) { this.openingHours = openingHours; }
    @Override
    public TerrainType getTerrainType(){
        return TerrainType.Lift;
    }

    @Override
    public TerrainType getWorksiteType(){
        return TerrainType.Lift;
    }

    @Override
    public String toString() {
        String upSlopeToString = ", upSlope=none";
        String downSlopeToString = ", downSlope=none";
        if (upSlope != null){
            upSlopeToString = ", up slope=[" + this.upSlope.getId() + " - " + this.upSlope.getName() + "]";
        }
        if (downSlope != null){
            downSlopeToString = ", down slope=[" + this.downSlope.getId() + " - " + this.downSlope.getName() + "]";
        }
        return "Lift: name=" + this.name + ", id=" + this.id+ ", up="
                + this.up.toString() + ", down=" + this.down
                + ", length=" + this.length + ", opening hours=" + this.openingHours.toString()
                + ", type=" + this.type + ", status=" + this.status
                + upSlopeToString + downSlopeToString
                + ", ski area=" + this.skiArea.getName();
    }
}
