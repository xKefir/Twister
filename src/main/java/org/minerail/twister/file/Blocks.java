package org.minerail.twister.file;

import org.bukkit.configuration.file.YamlConfiguration;
import org.minerail.twister.Twister;
import java.io.File;
import java.util.*;

public class Blocks {
    private static YamlConfiguration blocks;
    private static Set<String> keys = new HashSet<>();
    private static Set<String> types = new HashSet<>();
    private static Set<String> materials = new HashSet<>();
    private static Map<String, String> BLOCKS_KEYS_MAP = new LinkedHashMap<>();

    private Blocks() {
        File blocksFile = new File(Twister.get().getDataFolder().toPath() + "/blocks.yml");
        if (blocksFile.exists()) {
            blocks = YamlConfiguration.loadConfiguration(blocksFile);
            clear();
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
    private void clear() {
        keys.clear();
        types.clear();
        materials.clear();
        BLOCKS_KEYS_MAP.clear();
    }

    public static Set<String> getAllTypes() {
        try {
            for (String key : blocks.getKeys(false)) {
                types.add(key);
            }
            return types;
        } catch (Exception e) {
            Twister.get().getLogger().warning(e.getMessage());
        }
        return null;
    }

    public static Set<String> getTypeElements(String type) {
        try {
            for (String element : blocks.getConfigurationSection(type).getKeys(false)) {
                keys.add(element);
                BLOCKS_KEYS_MAP.put(type, element);
            }
            return keys;
        } catch (Exception e) {
            Twister.get().getLogger().warning(e.getMessage());
        }
        return null;
    }
    public static Set<String> getMaterialList(String type) {
        for (String entry : keys) {
            materials.add(blocks.getString(type + "." + entry));

        }
        return materials;
    }
    public static Map<String, String> getBlockKeysMap() {
        return BLOCKS_KEYS_MAP;
    }


}
