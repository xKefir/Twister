package org.minerail.twister.file.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.minerail.twister.Twister;
import org.yaml.snakeyaml.Yaml;

import java.io.File;

public class ConfigFile {
    private YamlConfiguration config;
    private File cf = new File(Twister.get().getDataFolder() + "/config.yml");

    public ConfigFile() {
        if (!cf.exists()) {
            Twister.get().saveResource("config.yml", false);
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(cf);
    }

    public String getString(ConfigKey key) {
        return config.getString(key.getPath());
    }

    public boolean getBoolean(ConfigKey key) {
        return config.getBoolean(key.getPath());
    }

    public int getInt(ConfigKey key) {
        return config.getInt(key.getPath());
    }


    public double getDouble(ConfigKey configKey) {
        return config.getDouble(configKey.getPath());
    }
}

