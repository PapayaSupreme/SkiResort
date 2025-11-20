package enums;

public record Point(double x, double y, double z) {

    @Override
    public String toString() {
        return "(x:" + this.x + ", y:" + this.y
                +  ", z:" + this.z + ")";
    }
}