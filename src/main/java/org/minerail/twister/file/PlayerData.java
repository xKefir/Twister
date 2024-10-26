package org.minerail.twister.file;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.minerail.twister.Twister;
import org.minerail.twister.util.LocationUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerData {
    private static final Map<UUID, PlayerData> PLAYER_DATA_MAP = new HashMap<>();
    private final UUID playerUUID;
    protected YamlConfiguration data;
    private File playerFile;

    private byte[] itemBytes;
    private String base64itemMeta;

    private PlayerData(Player player) {
        this.playerUUID = player.getUniqueId();
        PLAYER_DATA_MAP.put(playerUUID, this);

        File playerDataFolder = new File("plugins/Twister/PlayerData");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }

        playerFile = new File(playerDataFolder, playerUUID + ".yml");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                Twister.get().getLogger().warning("Could not create player data file: " + e.getMessage());
            }
        }

        data = YamlConfiguration.loadConfiguration(playerFile);
        data.set("name", player.getName());
    }

    public static PlayerData get(Player player) {
        if (!PLAYER_DATA_MAP.containsKey(player.getUniqueId())) {
            return new PlayerData(player);
        }
        return PLAYER_DATA_MAP.get(player.getUniqueId());
    }


    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public void save() {
        try {
            data.save(playerFile);
        } catch (Exception e) {
            Twister.get().getLogger().warning(e.getMessage());
        }
    }

    public static void saveAll() {
        PLAYER_DATA_MAP.values().forEach(PlayerData::save);
        Twister.get().getLogger().info("Saved player data.");
    }

    // INVENTORY MANAGEMENT

    public void savePlayerInventory(ItemStack[] itemStacks, float exp, int level) {
        for (int i = 0; i < itemStacks.length; i++) {
            if (itemStacks[i] != null) {
                itemBytes = itemStacks[i].serializeAsBytes();
                base64itemMeta = Base64.getEncoder().encodeToString(itemBytes);
                data.set("inventory.contents." + i + ".meta", base64itemMeta);
            }
        }
        data.set("inventory.experience.amount", exp);
        data.set("inventory.experience.level", level);
    }

    public ItemStack[] loadPlayerInventory() {
        ItemStack[] inv = new ItemStack[41];
        for (int i = 0; i < inv.length; i++) {
            if (data.contains("inventory.contents." + i)) {
                itemBytes = Base64.getDecoder().decode(data.getString("inventory.contents." + i + ".meta"));
                inv[i] = ItemStack.deserializeBytes(itemBytes);
            }
        }
        return inv;
    }

    public float getXP() {
        return (float) data.getDouble("inventory.experience.amount");
    }

    public int getLevel() {
        return data.getInt("inventory.experience.level");
    }

    public void clearInventoryData() {
        data.set("inventory.contents", null);
        data.set("inventory.experience", null);
    }

    // LOCATION MANAGEMENT
    public void deserializeAndSavePlayerLocation(Location loc) {
        double x = loc.getBlockX();
        double y = loc.getBlockY();
        double z = loc.getBlockZ();
        World world = loc.getWorld();
        data.set("last-location.x", x);
        data.set("last-location.y", y);
        data.set("last-location.z", z);
        data.set("last-location.world", world.getName());
    }

    public Location getPlayerJoinLocationFromData() {
        return LocationUtil.serializeLocation(
                data.getDouble("last-location.x"),
                data.getDouble("last-location.y"),
                data.getDouble("last-location.z"),
                data.getString("last-location.world"));
    }


    // STATISTICS

    public void setWins(int wins) {
        data.set("statistics.wins", wins);
    }

    public int getWins() {
        return data.getInt("statistics.wins", 0);
    }

    public void addWins(int wins) {
        setWins(getWins() + wins);
    }

    public void setLoses(int loses) {
        data.set("statistics.loses", loses);
    }

    public int getLoses() {
        return data.getInt("statistics.loses");
    }

    public void addLoses(int loses) {
        setLoses(getLoses() + loses);
    }

    public void setTotalPlayed(int totalPlayed) {
        data.set("statistics.totalPlayed", totalPlayed);
    }

    public int getTotalPlayed() {
        return data.getInt("statistics.totalPlayed");
    }

    public void addTotalPlayed(int totalPlayed) {
        setTotalPlayed(getTotalPlayed() + totalPlayed);
    }

    public void resetStats() {
        setWins(0);
        setLoses(0);
        setTotalPlayed(0);
    }
}
class PlayerStatistics {
    private static Map<String, Integer> cachedData = new HashMap<>();


    public static Map<String, Integer> getTop10Players(String type) {
        switch (type) {
            case "wins" -> {
                return findBy("statistics.wins");
            }
            case "total" -> {
                return findBy("statistics.totalPlayed");
            }
            case "loses" -> {
                return findBy("statistics.loses");
            }
        }
        return null;
    }

    private static Map<String, Integer> findBy(String path) {
        for (File file : getFiles()) {
            YamlConfiguration tempFile = YamlConfiguration.loadConfiguration(file);
            cachedData.put(tempFile.getString("name"), tempFile.getInt(path));
        }
    }

    private static List<File> getFiles() {
        try {
            File playerDataFolder = new File("plugins/Twister/PlayerData");
            return Arrays.stream(playerDataFolder.listFiles()).toList();
        } catch (NullPointerException e) {
            Twister.get().getLogger().warning("No files found in PlayerData directory.");
        }
        return null;
    }
}

