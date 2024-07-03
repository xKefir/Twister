package org.minerail.twister.file;

import org.bukkit.configuration.file.YamlConfiguration;
import org.minerail.twister.Twister;
import java.io.File;
import java.util.*;

public class Blocks {
    private static YamlConfiguration blocks;
    private static Set<String> keys = new HashSet<>();
    private static Set<String> types = new HashSet<>();
    private static Map<String, String> BLOCKS_KEYS_MAP = new LinkedHashMap<>();

    private Blocks() {
        File blocksFile = new File(Twister.get().getDataFolder().toPath() + "/blocks.yml");
        if (blocksFile.exists()) {
            blocks = YamlConfiguration.loadConfiguration(blocksFile);
            getAllTypes();
            for (String type : types) {
                getTypeElements(type);
            }
        } else {
            Twister.get().saveResource("blocks.yml", false);
        }
    }

    public static void create() {
        new Blocks();
    }

    public static Set<String> getAllTypes() {
        for (String key : blocks.getKeys(false)) {
            types.add(key);
        }
        return types;
    }

    public static Set<String> getTypeElements(String type) {
        for (String element : blocks.getConfigurationSection(type).getKeys(false)) {
            keys.add(element);
            BLOCKS_KEYS_MAP.put(type, element);
        }
        return keys;
    }
    public static Map<String, String> getBlockKeysMap() {
        return BLOCKS_KEYS_MAP;
    }


}
