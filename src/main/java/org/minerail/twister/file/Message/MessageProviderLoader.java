package org.minerail.twister.file.Message;

import org.bukkit.configuration.file.YamlConfiguration;
import org.minerail.twister.Twister;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageProviderLoader {

    private static YamlConfiguration messages;

    public static void reload() {
        File msg1 = new File(Twister.get().getDataFolder().toPath() + "/messages.yml");
        if (!msg1.exists()) {
            Twister.get().saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(msg1);

    }

    public static String getString(String path) {
        return messages.getString(path);
    }
    public static List<String> getStringList(String path) {
        return new ArrayList<>(messages.getStringList(path));
    }

    public static List<String> getTopList() {
        List<String> keys = new ArrayList<>();
        for (String key : messages.getConfigurationSection(MessageKey.MESSAGES_COMMAND_TOP_LIST.getPath()).getKeys(false)) {
            keys.add(getString(MessageKey.MESSAGES_COMMAND_TOP_LIST.getPath() + "." + key));
        }
        return keys;
    }

}
