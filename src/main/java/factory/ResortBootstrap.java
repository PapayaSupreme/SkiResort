package factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.UUID;

import enums.*;
import io.github.cdimascio.dotenv.Dotenv;
import terrain.*;
import utils.ResortUtils;


public final class ResortBootstrap {

    public static void main(String[] args) {
    }

    public static ResortLoader.Mappers makeMappers() {
        return new ResortLoader.Mappers() {
            @Override
            public SkiArea makeSkiArea(ResortLoader.SkiAreaRow ws, ResortLoader.SkiAreaDetailRow d) {
                return new SkiArea(
                        ws.id, UUID.fromString(d.publicId()), ws.name,
                        d.elevationMin(), d.elevationMax(),
                        ResortUtils.parseOH(ws.openingHoursJson), d.functioning());
            }
            @Override
            public Slope makeSlope(ResortLoader.SlopeRow r, Object skiArea) {
                return new Slope(r.id(), UUID.fromString(r.publicId()), r.name(),
                        new Point(r.upX(), r.upY(), r.upZ()),
                        new Point(r.downX(), r.downY(), r.downZ()), r.lengthM(),
                        ResortUtils.parseOH(r.openingHoursJson()), SlopeDifficulty.valueOf(r.difficulty()),
                        SlopeType.valueOf(r.slopeType()), (SkiArea) skiArea);
            }
            @Override
            public Lift makeLift(ResortLoader.LiftRow r, Object skiArea, Object upSlope, Object downSlope) {
                return new Lift(r.id(), UUID.fromString(r.publicId()), r.name(),
                        new Point(r.upX(), r.upY(), r.upZ()),
                        new Point(r.downX(), r.downY(), r.downZ()),
                        r.lengthM(), ResortUtils.parseOH(r.openingHoursJson()),
                        LiftType.valueOf(r.liftType()), LiftStatus.valueOf(r.liftStatus()),
                        (Slope) upSlope, (Slope) downSlope,
                        (SkiArea) skiArea);
            }
            @Override
            public Restaurant makeRestaurant(ResortLoader.RestaurantRow r, Object skiArea) {
                return new Restaurant(
                        r.worksiteId(), r.name(), new Point(r.x(), r.y(), r.z()),
                        (SkiArea) skiArea, POIStatus.valueOf(r.status()));
            }
            @Override
            public RescuePoint makeRescuePoint(ResortLoader.RescuePointRow r, Object skiArea) {
                return new RescuePoint(
                        r.worksiteId(), r.name(), new Point(r.x(), r.y(), r.z()),
                        (SkiArea) skiArea, POIStatus.valueOf(r.status()), r.warning());
            }
            @Override
            public Summit makeSummit(ResortLoader.SummitRow r, Object skiArea) {
                return new Summit(
                        r.poiId(), r.name(), new Point(r.x(), r.y(), r.z()),
                        (SkiArea) skiArea, r.snowHeightCm(), SnowConsistency.valueOf(r.snowConsistency()));
            }
        };
    }
}

