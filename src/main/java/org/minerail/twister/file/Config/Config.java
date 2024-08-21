package org.minerail.twister.file.Config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.minerail.twister.Twister;

import java.io.File;

public class Config {
    public static YamlConfiguration config;

    public static void reload() {
        File msg1 = new File(Twister.get().getDataFolder().toPath() + "/config.yml");
        if (!msg1.exists()) {
            Twister.get().saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(msg1);

    }

    public static String getString(ConfigKey key) {
        return config.getString(key.getPath());
    }
    public static int getInt(ConfigKey key) {
        return config.getInt(key.getPath());
    }

    public static double getDouble(ConfigKey key) {
        return config.getDouble(key.getPath());
    }

    public static boolean getBoolean(ConfigKey key) { return config.getBoolean(key.getPath()); }


}

