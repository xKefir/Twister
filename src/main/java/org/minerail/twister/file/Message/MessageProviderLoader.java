package org.minerail.twister.file.Message;

import org.bukkit.configuration.file.YamlConfiguration;
import org.minerail.twister.Twister;

import java.io.File;

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

}
