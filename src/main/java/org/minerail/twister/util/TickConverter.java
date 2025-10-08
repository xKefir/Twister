package org.minerail.twister.util;

public class TickConverter {

    private static final int TICKS_PER_SECOND = 20;
    private static final int MS_PER_TICK = 50;

    // ms → tick
    public static long msToTicks(long ms) {
        return ms / MS_PER_TICK;
    }

    // seconds → tick
    public static long secondsToTicks(long seconds) {
        return seconds * TICKS_PER_SECOND;
    }

    // ticks → seconds (for displaying)
    public static long ticksToSeconds(long ticks) {
        return ticks / TICKS_PER_SECOND;
    }

    // ticks → ms (optional)
    public static long ticksToMs(long ticks) {
        return ticks * MS_PER_TICK;
    }
}
