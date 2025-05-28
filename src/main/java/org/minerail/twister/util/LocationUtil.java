package org.minerail.twister.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.minerail.twister.Twister;
import org.minerail.twister.file.config.ConfigFile;
import org.minerail.twister.file.config.ConfigKey;

public class LocationUtil {
    private static ConfigFile config = Twister.getConfigFile();

    public static Location serializeLocation(double x, double y, double z, String world) {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public static Location createLocation(ConfigKey xKey, ConfigKey yKey, ConfigKey zKey) {
        try {
            return serializeLocation(
                    config.getDouble(xKey),
                    config.getDouble(yKey),
                    config.getDouble(zKey),
                    config.getString(ConfigKey.ARENA_WORLD)
            );
        } catch (Exception e) {
            LogUtil.error("Failed to create location: " + e.getMessage());
            return null;
        }
    }
}
