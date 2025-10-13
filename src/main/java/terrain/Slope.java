package terrain;

import enums.*;

import java.util.UUID;

public class Slope implements Terrain {
    private final long id;
    private UUID publicId;
    private final String name;
    private SlopeDifficulty slopeDifficulty;
    private SnowConsistency snowConsistency = SnowConsistency.NONE;
    private SlopeType slopeType;
    private final SkiArea skiArea;
    private final Point up;
    private final Point down;
    private final double length;
    private OpeningHours openingHours;

    public Slope(long id, UUID publicId, String name, Point up, Point down,
                 double length, OpeningHours openingHours,
                 SlopeDifficulty slopeDifficulty, SlopeType slopeType, SkiArea skiArea) {
        this.name = name;
        this.publicId = publicId;
        this.id = id;
        this.up = up;
        this.down = down;
        this.length = length;
        this.openingHours = openingHours;
        this.slopeDifficulty = slopeDifficulty;
        this.slopeType = slopeType;
        this.skiArea = skiArea;
    }

    @Override public String getName() { return this.name; }
    @Override public long getId() { return this.id; }
    public Point getUp() { return this.up; }
    public Point getDown() { return this.down; }
    public double getLength() { return this.length; }
    public OpeningHours getOpeningHours() { return this.openingHours; }
    public SlopeDifficulty getSlopeDifficulty() { return this.slopeDifficulty; }
    public SnowConsistency getSnowConsistency() { return this.snowConsistency; }
    public SlopeType getSlopeType() { return this.slopeType;}
    public SkiArea getSkiArea() { return this.skiArea; }

    public void setOpeningHours(OpeningHours openingHours) { this.openingHours = openingHours; }
    public void setSlopeDifficulty(SlopeDifficulty slopeDifficulty) { this.slopeDifficulty = slopeDifficulty; }
    public void setSnowConsistency(SnowConsistency snowConsistency) { this.snowConsistency = snowConsistency; }
    public void setSlopeType(SlopeType slopeType) { this.slopeType = slopeType; }

    @Override
    public TerrainType getTerrainType(){
        return TerrainType.Slope;
    }

    @Override
    public String toString() {
        return "Slope: name=" + this.name + ", id=" + this.id+ ", up="
                + this.up.toString() + ", down=" + this.down
                + ", length=" + this.length + ", opening hours=" + this.openingHours
                + ", difficulty=" + this.slopeDifficulty + ", snow=" + this.snowConsistency
                + ", type=" + this.slopeType + ", ski area=" + this.skiArea.getName();
    }


}
