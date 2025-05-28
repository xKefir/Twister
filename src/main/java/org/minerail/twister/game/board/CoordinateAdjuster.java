package org.minerail.twister.game.board;

import org.bukkit.Location;

public class CoordinateAdjuster {
    private int x1;
    private int x2;
    private int z1;
    private int z2;
    private static Location loc1Adjusted = null;
    private static Location loc2Adjusted = null;

    public CoordinateAdjuster(Location loc1, Location loc2) {
        x1 = loc1.getBlockX();
        x2 = loc2.getBlockX();
        z1 = loc1.getBlockZ();
        z2 = loc2.getBlockZ();
        compareAndAdjust(loc1, loc2);
    }

    private void compareAndAdjust(Location loc1, Location loc2) {
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (z1 > z2) {
            int temp = z1;
            z1 = z2;
            z2 = temp;
        }

        loc1Adjusted = new Location(loc1.getWorld(), x1 - 1, loc1.getBlockY(), z1 - 1);
        loc2Adjusted = new Location(loc2.getWorld(), x2, loc2.getBlockY(), z2);
    }

    public static Location getLoc1() {
        return loc1Adjusted;
    }

    public static Location getLoc2() {
        return loc2Adjusted;
    }
}
