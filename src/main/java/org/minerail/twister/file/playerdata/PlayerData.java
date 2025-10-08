package org.minerail.twister.file.playerdata;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.minerail.twister.util.LocationUtil;
import org.minerail.twister.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerData {
    private static Map<UUID, PlayerData> PLAYER_DATA_MAP = new HashMap<>();
    private final UUID playerUUID;
    private final String playerName;
    private final Player player;
    private PlayerStats stats;
    protected YamlConfiguration data;
    private File playerFile;


    private PlayerData(Player player) {
        this.playerUUID = player.getUniqueId();
        this.playerName = player.getName();
        this.player = player;
        this.stats = new PlayerStats(this);
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
                LogUtil.warn("Could not create player data file: " + e.getMessage());
            }
        }

        data = YamlConfiguration.loadConfiguration(playerFile);
        data.set("name", player.getName());
        loadPlayerStats();

    }


    public static PlayerData get(Player p) {
        if (!PLAYER_DATA_MAP.containsKey(p.getUniqueId())) {
            return new PlayerData(p);
        }
        return PLAYER_DATA_MAP.get(p.getUniqueId());
    }

    public void save() {
        try {
            savePlayerStats();
            data.save(playerFile);
        } catch (Exception e) {
            LogUtil.warn("Error saving player data: " + e.getMessage());
        }
    }

    public static void saveAll() {
        LogUtil.debug("Saving all player data...");

        for (PlayerData playerData : PLAYER_DATA_MAP.values()) {
            try {
                playerData.save();
            } catch (Exception e) {
                LogUtil.error("Error saving data for player " + playerData.playerName + ": " + e.getMessage());
            }
        }


        LogUtil.debug("Saved all player data (" + PLAYER_DATA_MAP.size() + " players).");
    }

    private void loadPlayerStats() {
        this.stats = new PlayerStats(this);
        this.stats.writeData(
                data.getInt("stats.wins", 0),
                data.getInt("stats.losses", 0),
                data.getInt("stats.gamesPlayed", 0)
        );

        LogUtil.debug("Loaded stats for " + playerName + ": W:" + stats.getWins() +
                " L:" + stats.getLosses() + " T:" + stats.getGamesPlayed());
    }

    public void savePlayerStats() {
        data.set("stats.wins", stats.getWins());
        data.set("stats.losses", stats.getLosses());
        data.set("stats.gamesPlayed", stats.getGamesPlayed());

        LogUtil.debug("Saved stats for " + playerName + ": W:" + stats.getWins() +
                " L:" + stats.getLosses() + " T:" + stats.getGamesPlayed());
    }

    public PlayerStats getPlayerStats() {
        return this.stats;
    }


    public void savePlayerInventory(ItemStack[] itemStacks, float exp, int level) {
        for (int i = 0; i < itemStacks.length; i++) {
            if (itemStacks[i] != null) {
                byte[] itemBytes = itemStacks[i].serializeAsBytes();
                String base64itemMeta = Base64.getEncoder().encodeToString(itemBytes);
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
                byte[] itemBytes = Base64.getDecoder().decode(data.getString("inventory.contents." + i + ".meta"));
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

    public void deserializeAndSavePlayerLastLocation(Location loc) {
        double x = loc.getBlockX();
        double y = loc.getBlockY();
        double z = loc.getBlockZ();
        World world = loc.getWorld();
        data.set("last-location.x", x);
        data.set("last-location.y", y);
        data.set("last-location.z", z);
        data.set("last-location.world", world.getName());
    }

    public Location getPlayerLastLocationFromData() {
        return LocationUtil.serializeLocation(
                data.getDouble("last-location.x"),
                data.getDouble("last-location.y"),
                data.getDouble("last-location.z"),
                data.getString("last-location.world"));
    }
    public Player getPlayer() {
        return player;
    }
    public UUID getPlayerUUID() { return playerUUID; }
    public String getPlayerName() { return playerName; }
}
