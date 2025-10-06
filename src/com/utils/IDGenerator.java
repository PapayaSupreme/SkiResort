package com.utils;

public final class IDGenerator {
    private static long currentID = 0;

    private IDGenerator() {}


    public static synchronized long generateID(){
        return ++currentID;
    }
}
