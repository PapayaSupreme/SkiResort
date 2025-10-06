package com.architect;

import com.enums.*;
import com.terrain.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;

public class SerreChe {
    //terrain id rules (cumulative):
        //1 : Resort ID
        //2 : 1 because terrain
        //3 : Ski Area ID
        //4 : Type ID (1 for slopes, 2 for lifts, 3 for POIs)
        //5 : terrain ID number if slope/lift
           // Subtype ID if POI ( 1 for restaurants, 2 for rescue points, 3 for summits)
        //6 : terrain ID number if POI

    //pass id rules (cumulative)
        //1 : Resort ID
        //2 : 2 because pass
        //3 : pass type (1 for employee, 2 for vip, 3 for instructor, 4 for guest or free)
        //4 : pass ID number

    //people id rules (cumulative)
        //1 : Resort ID
        //2 : 3 because people
        //3 : type ID (1 for employee, 2 for instructor, 3 for guest)
        //4 : person ID number

    private SerreChe() {}

    // Helper: standard winter opening hours (every day 09:00–16:30)
    public static OpeningHours winterHours() {
        return new OpeningHours(
                LocalTime.of(9, 0),
                LocalTime.of(16, 30),
                EnumSet.noneOf(DayOfWeek.class) // no closed days → open 7/7
        );
    }

    // ---- Build the whole resort (SkiResort -> 4 areas -> lifts/slopes/POIs) ----
    public static SkiResort createResort() {
        // Resort
        SkiResort resort = new SkiResort("Serre Chevalier",1);

        //Ski Areas
        SkiArea briancon   = new SkiArea("Briançon", 101,
                new Point(0, 0, 2404), new Point(0, 0, 1200), 1000, SerreChe.winterHours());

        SkiArea chantemerle= new SkiArea("Chantemerle", 102,
                new Point(0, 0, 2491),new Point(0, 0, 1350), 1000, SerreChe.winterHours());

        SkiArea villeneuve = new SkiArea("Villeneuve", 103,
                new Point(0, 0, 1659),new Point(0, 0, 1400), 1000, SerreChe.winterHours());

        SkiArea monetier   = new SkiArea("Le Monêtier", 104,
                new Point(0, 0, 2830),new Point(0, 0, 1500), 1000, SerreChe.winterHours());

        // Slopes
        Slope grandeGargouille = new Slope(
                "Grande Gargouille", 201, new Point(0, 0, 2360), new Point(0, 0, 1606),
                2614.0, SerreChe.winterHours(), SlopeDifficulty.RED, SlopeType.PISTE, briancon);

        Slope vauban = new Slope(
                "Vauban", 205, new Point(0, 0, 1625), new Point(0, 0, 1215),
                1920.0, SerreChe.winterHours(), SlopeDifficulty.RED, SlopeType.PISTE, briancon);

        Slope lucAlphand = new Slope(
                "Luc Alphand", 202, new Point(0, 0, 1892), new Point(0, 0, 1350),
                1978.0, SerreChe.winterHours(), SlopeDifficulty.BLACK, SlopeType.PISTE, chantemerle);

        Slope cucumelle = new Slope(
                "Cucumelle", 203, new Point(0, 0, 2510), new Point(0, 0, 1775),
                4036.0, SerreChe.winterHours(), SlopeDifficulty.RED, SlopeType.PISTE, villeneuve);

        Slope routeFrejus = new Slope(
                "Route Fréjus", 206, new Point(0, 0, 1940), new Point(0, 0, 1490),
                4312.0, SerreChe.winterHours(), SlopeDifficulty.GREEN, SlopeType.PISTE, villeneuve);

        Slope rochamout = new Slope(
                "Rochamout", 204, new Point(0, 0, 2175), new Point(0, 0, 1500),
                4361.0, SerreChe.winterHours(), SlopeDifficulty.BLUE, SlopeType.PISTE, monetier);

        // Lifts
        Lift prorel2 = new Lift(
                "Prorel 2", 301, new Point(0, 0, 2355), new Point(0, 0, 1627),
                2336.0, SerreChe.winterHours(), LiftType.GONDOLA, grandeGargouille, vauban, briancon);

        Lift ratier = new Lift(
                "Ratier", 302, new Point(0, 0, 1888), new Point(0, 0, 1350),
                1610.0, SerreChe.winterHours(), LiftType.GONDOLA, lucAlphand, null, chantemerle);

        Lift vallons = new Lift(
                "Vallons", 302, new Point(0, 0, 2505), new Point(0, 0, 1915),
                2207.0, SerreChe.winterHours(), LiftType.CHAIRLIFT, cucumelle, routeFrejus, chantemerle);

        Lift bachas = new Lift(
                "Bachas", 304, new Point(0, 0, 2176), new Point(0, 0, 1465),
                2492.0, SerreChe.winterHours(), LiftType.CHAIRLIFT, rochamout, null, monetier);

        // Attach lifts and slopes to areas
        briancon.addSlope(grandeGargouille);
        briancon.addSlope(vauban);
        briancon.addLift(prorel2);

        chantemerle.addSlope(lucAlphand);
        chantemerle.addLift(ratier);

        villeneuve.addSlope(cucumelle);
        villeneuve.addSlope(routeFrejus);
        villeneuve.addLift(vallons);

        monetier.addSlope(rochamout);
        monetier.addLift(bachas);

        // Restaurants
        Restaurant serreBlanc = new Restaurant("Chalet de Serre Blanc", 401, new Point(0, 0, 2200), briancon);
        Restaurant cabaneASucre1 = new Restaurant("Cabane à Sucre", 402, new Point(0, 0, 2172), chantemerle);
        Restaurant bivouac = new Restaurant("Bivouac 3200", 403, new Point(0, 0, 2300), villeneuve);
        Restaurant flocon = new Restaurant("Flocon", 405, new Point(0, 0, 2176), monetier);

        // Rescue Points
        RescuePoint prorelRP = new RescuePoint("Prorel", 501, new Point(0, 0, 2360), briancon);
        RescuePoint serreRatierRP = new RescuePoint("Serre Ratier", 502, new Point(0, 0, 1905), chantemerle);
        RescuePoint meaRP = new RescuePoint("Méa", 503, new Point(0, 0, 2251), villeneuve);
        RescuePoint bachasRP = new RescuePoint("Bachas", 504, new Point(0, 0, 2176), monetier);

        // Summits
        Summit colDuProrel = new Summit("Col du Prorel", 601, new Point(0, 0, 2404), briancon);
        Summit serreChevalier = new Summit("Serre Chevalier", 602, new Point(0, 0, 2491), chantemerle);
        Summit eychauda = new Summit("L'Eychauda", 603, new Point(0, 0, 2659), villeneuve);
        Summit colDeLaCucumelle = new Summit("Col de la Cucumelle", 604, new Point(0, 0, 2505), monetier);

        // attach pois to areas
        briancon.addPoi(serreBlanc);
        briancon.addPoi(prorelRP);
        briancon.addPoi(colDuProrel);

        chantemerle.addPoi(cabaneASucre1);
        chantemerle.addPoi(serreRatierRP);
        chantemerle.addPoi(serreChevalier);

        villeneuve.addPoi(bivouac);
        villeneuve.addPoi(meaRP);
        villeneuve.addPoi(eychauda);

        monetier.addPoi(flocon);
        monetier.addPoi(bachasRP);
        monetier.addPoi(colDeLaCucumelle);

        resort.addSkiArea(briancon);
        resort.addSkiArea(chantemerle);
        resort.addSkiArea(villeneuve);
        resort.addSkiArea(monetier);

        return resort;
    }

    // Convenience date range for season (2025-12-01 to 2026-04-30)
    public static LocalDate seasonStart() { return LocalDate.of(2025, 12, 1); }
    public static LocalDate seasonEnd()   { return LocalDate.of(2026, 4, 30); }
}
