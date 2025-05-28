package org.minerail.twister.file.playerdata;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.minerail.twister.Twister;
import org.minerail.twister.file.leaderboard.Leaderboard;
import org.minerail.twister.file.leaderboard.LeaderboardHandler;
import org.minerail.twister.util.LocationUtil;
import org.minerail.twister.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerData {
    private static final Map<UUID, PlayerData> PLAYER_DATA_MAP = new HashMap<>();
    private final UUID playerUUID;
    private final String playerName;
    private final Player player;
    private PlayerStats stats;
    protected YamlConfiguration data;
    private File playerFile;

    private static Leaderboard leaderboard;
    private static LeaderboardHandler leaderboardHandler;

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

        if (leaderboard != null) {
            syncToLeaderboard();
        }
    }

    public static void initLeaderboardIntegration(Leaderboard leaderboardInstance) {
        leaderboard = leaderboardInstance;
        leaderboardHandler = new LeaderboardHandler(leaderboard);
        LogUtil.debug("Inicjalizacja integracji PlayerData z systemem rankingowym zakończona");
    }

    public static LeaderboardHandler getLeaderboardHandler() {
        return leaderboardHandler;
    }

    public static PlayerData get(Player player) {
        if (!PLAYER_DATA_MAP.containsKey(player.getUniqueId())) {
            return new PlayerData(player);
        }
        return PLAYER_DATA_MAP.get(player.getUniqueId());
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

        // Synchronizacja wszystkich graczy z leaderboardem
        syncAllToLeaderboard();

        if (leaderboard != null) {
            leaderboard.save();
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

    // POPRAWIONA METODA synchronizacji z leaderboardem
    public void syncToLeaderboard() {
        if (leaderboard == null) {
            LogUtil.debug("Leaderboard not initialized, skipping sync for " + playerName);
            return;
        }

        if (stats == null) {
            LogUtil.warn("PlayerStats is null for " + playerName + ", cannot sync to leaderboard");
            return;
        }

        try {
            // Użyj bezpośredniej synchronizacji ze statystyk gracza
            leaderboard.updateFromPlayerStats(
                    playerName,
                    stats.getWins(),
                    stats.getLosses(),
                    stats.getGamesPlayed()
            );

            LogUtil.debug("Successfully synced " + playerName + " to leaderboard: W:" +
                    stats.getWins() + " L:" + stats.getLosses() + " T:" + stats.getGamesPlayed());

        } catch (Exception e) {
            LogUtil.error("Error syncing " + playerName + " to leaderboard: " + e.getMessage());

            // Fallback - spróbuj z PlaceholderAPI jeśli bezpośrednia synchronizacja nie działa
            if (player != null && player.isOnline()) {
                try {
                    leaderboard.updateFromPlaceholderAPI(player);
                    LogUtil.debug("Fallback PlaceholderAPI sync successful for " + playerName);
                } catch (Exception e2) {
                    LogUtil.error("Fallback PlaceholderAPI sync also failed for " + playerName + ": " + e2.getMessage());
                }
            }
        }
    }

    // Metoda do synchronizacji wszystkich graczy (statyczna)
    public static void syncAllToLeaderboard() {
        if (leaderboard == null) {
            LogUtil.debug("Leaderboard not initialized, skipping sync for all players");
            return;
        }

        LogUtil.debug("Starting leaderboard sync for all players (" + PLAYER_DATA_MAP.size() + " players)");

        int successCount = 0;
        int errorCount = 0;

        for (PlayerData pd : PLAYER_DATA_MAP.values()) {
            try {
                pd.syncToLeaderboard();
                successCount++;
            } catch (Exception e) {
                LogUtil.error("Error syncing player " + pd.playerName + " to leaderboard: " + e.getMessage());
                errorCount++;
            }
        }

        LogUtil.debug("Leaderboard sync completed: " + successCount + " successful, " + errorCount + " errors");

    }

    // NOWE METODY do aktualizacji statystyk z automatyczną synchronizacją
    public void addWin() {
        if (stats != null) {
            stats.addWin();
            syncToLeaderboard();
            LogUtil.debug("Added win for " + playerName + " (now: " + stats.getWins() + ")");
        }
    }

    public void addLoss() {
        if (stats != null) {
            stats.addLoss();
            syncToLeaderboard();
            LogUtil.debug("Added loss for " + playerName + " (now: " + stats.getLosses() + ")");
        }
    }

    public void addGamePlayed() {
        if (stats != null) {
            stats.addGamesPlayed();
            syncToLeaderboard();
            LogUtil.debug("Added game played for " + playerName + " (now: " + stats.getGamesPlayed() + ")");
        }
    }

    // Metoda do manualnego odświeżenia statystyk z PlaceholderAPI
    public void refreshFromPlaceholderAPI() {
        if (leaderboard != null && player != null && player.isOnline()) {
            leaderboard.updateFromPlaceholderAPI(player);
            LogUtil.debug("Refreshed " + playerName + " stats from PlaceholderAPI");
        }
    }

    // Pozostałe metody bez zmian...
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

    public Player getPlayer() {
        return player;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }
}