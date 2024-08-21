package org.minerail.twister.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {

    public static Location serializeLocation(double x, double y, double z, String world) {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

}
