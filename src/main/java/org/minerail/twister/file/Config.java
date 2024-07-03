package org.minerail.twister.file;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.minerail.twister.Twister;
import java.io.File;

public class Config {
    private static YamlConfiguration config;

    public static void create() {
        Twister.get().saveResource("config.yml", false);
    }
    public static void reload() {
        config = YamlConfiguration.loadConfiguration(new File(Twister.get().getDataFolder().toPath() + "/config.yml"));
        Twister.get().getLogger().info("Successfully reloaded config.");
    }

    public static String getString(String path) {
        return config.getString(path);
    }

    public static int getInt(String path) {
        return config.getInt(path);
    }

    public static double getDouble(String path) {
        return config.getDouble(path);
    }

    public static boolean getBoolean(String path) { return config.getBoolean(path); }

    public static Component provideMessage(String message, TagResolver... resolvers) {
        return MessageProvider.message(message, resolvers);
    }
}

class MessageProvider {

    // Player <player>, int <remainPlayers>, int <1-10>, int <poolSize>, String <type>, Player to string <lastWinner>
    public static Component message(String message, TagResolver... resolvers) {
        String prfx = "";
        if (Config.getBoolean("Messages.prefix.show-prefix")) {
            prfx = Config.getString("Messages.prefix.string");
        }
        if (Config.getString("Messages.Input-type").equals("LEGACY")) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(
                    LegacyComponentSerializer.legacyAmpersand().serialize(
                            MiniMessage.builder().build().deserialize(prfx + message, resolvers))
            );
        } else {
            MiniMessage.miniMessage().deserialize(message, resolvers);
        }

        return null;
    }


}