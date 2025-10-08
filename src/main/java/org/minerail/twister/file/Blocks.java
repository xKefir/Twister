package org.minerail.twister.file;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.minerail.twister.Twister;
import org.minerail.twister.util.TextFormatUtil;

import java.io.File;
import java.util.*;

public class Blocks {
    private static YamlConfiguration blocks;
    private static List<String> keys = new ArrayList<>();
    private static List<String> types = new ArrayList<>();
    private static List<String> materials = new ArrayList<>();
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

    public static List<String> getAllTypes() {
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

    public static List<String> getTypeElements(String type) {
        try {
            for (String element : blocks.getConfigurationSection(type + ".elements").getKeys(false)) {
                keys.add(element);
                BLOCKS_KEYS_MAP.put(type, element);
            }
            return keys;
        } catch (Exception e) {
            Twister.get().getLogger().warning(e.getMessage());
        }
        return null;
    }
    public static List<String> getMaterialList(String type) {
        for (String entry : keys) {
            materials.add(blocks.getString(type + ".elements." + entry));
        }
        return materials;
    }

    public static Material getMaterialFromSettings(String type) {
        return Material.valueOf(blocks.getString(type + ".type-display.material"));
    }

    public static Component getItemNameFromSettings(String type) {
        return TextFormatUtil.format(blocks.getString(type + ".type-display.name"));
    }
    public static List<Component> getItemLoreFromSettings(String type) {
        return TextFormatUtil.format(blocks.getStringList(type + ".type-display.lore"));
    }
}
