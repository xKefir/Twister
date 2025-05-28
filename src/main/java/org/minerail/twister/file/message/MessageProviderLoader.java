package org.minerail.twister.file.message;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.minerail.twister.Twister;
import org.minerail.twister.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageProviderLoader {

    private YamlConfiguration messages;
    private File msg = new File(Twister.get().getDataFolder() + "/messages.yml");

    public MessageProviderLoader() {
        if (!msg.exists()) {
            Twister.get().saveResource("messages.yml", false);
            LogUtil.info("Created messages.yml file");
        }
    }

    public void reload() {
        try {
            messages = YamlConfiguration.loadConfiguration(msg);
            LogUtil.debug("Successfully reloaded messages.yml");
        } catch (Exception e) {
            LogUtil.error("Failed to reload messages.yml: " + e.getMessage());
        }
    }

    public YamlConfiguration get() {
        return messages;
    }

    public String getString(String path) {
        String result = messages.getString(path);
        if (result == null) {
            LogUtil.warn("Missing message at path: " + path);
            return "Missing message: " + path;
        }
        return result;
    }

    public List<String> getStringList(String path) {
        List<String> result = messages.getStringList(path);
        if (result.isEmpty() && !messages.isList(path)) {
            LogUtil.warn("Missing message list at path: " + path);
            return List.of("Missing message list: " + path);
        }
        return new ArrayList<>(result);
    }

    // HELPER METHODS dla systemu leaderboard
    public ConfigurationSection getConfigurationSection(String path) {
        ConfigurationSection section = messages.getConfigurationSection(path);
        if (section == null) {
            LogUtil.warn("Missing configuration section at path: " + path);
        }
        return section;
    }

    public Set<String> getKeys(String path) {
        ConfigurationSection section = getConfigurationSection(path);
        return section != null ? section.getKeys(false) : Set.of();
    }

    // STATIC HELPER METHODS - dla wygody
    public static String getStaticString(String path) {
        return Twister.getMessages().getString(path);
    }

    public static List<String> getStaticStringList(String path) {
        return Twister.getMessages().getStringList(path);
    }

    public static ConfigurationSection getStaticConfigurationSection(String path) {
        return Twister.getMessages().getConfigurationSection(path);
    }
}