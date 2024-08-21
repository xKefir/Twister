package org.minerail.twister.file;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.minerail.twister.Twister;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerData {
    private static final Map<UUID, PlayerData> PLAYER_DATA_MAP = new HashMap<>();
    private final UUID playerUUID;
    private YamlConfiguration data;
    private File playerFile;

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
    }

    public static PlayerData get(Player player) {
        if (!PLAYER_DATA_MAP.containsKey(player.getUniqueId())) {
            return new PlayerData(player);
        }
        return PLAYER_DATA_MAP.get(player.getUniqueId());
    }


    public static void remove(Player player) {
        PLAYER_DATA_MAP.remove(player.getUniqueId());
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

    public void setWins(int wins) {
        data.set(getPlayer().getName() + ".wins", Math.max(0, wins));
    }

    public int getWins() {
        return data.getInt(getPlayer().getName() + ".wins", 0);
    }

    public void addWins(int wins) {
        setWins(getWins() + wins);
    }

    public void setLoses(int loses) {
        data.set(getPlayer().getName() + ".loses", Math.max(0, loses));
    }

    public int getLoses() {
        return data.getInt(getPlayer().getName() + ".loses", 0);
    }

    public void addLoses(int loses) {
        setLoses(getLoses() + loses);
    }

    public void setTotalPlayed(int totalPlayed) {
        data.set(getPlayer().getName() + ".totalPlayed", Math.max(0, totalPlayed));
    }

    public int getTotalPlayed() {
        return data.getInt(getPlayer().getName() + ".totalPlayed", 0);
    }

    public void addTotalPlayed(int totalPlayed) {
        setTotalPlayed(getTotalPlayed() + totalPlayed);
    }

    public void resetStats() {
        setWins(0);
        setLoses(0);
        setTotalPlayed(0);
    }

    public boolean validatePlayer() {
        return Bukkit.getPlayer(playerUUID) != null;
    }

    public static List<PlayerData> getTopPlayerDataByWins(int topN) {
        return PLAYER_DATA_MAP.values().stream()
                .sorted(Comparator.comparingInt(PlayerData::getWins).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    public static List<String> getTopPlayersByWins(int topN) {
        return getTopPlayerDataByWins(topN).stream()
                .map(playerData -> playerData.getPlayer().getName() + ": " + playerData.getWins() + " wins")
                .collect(Collectors.toList());
    }

    public static String getTopPlayersByWinsAsString(int topN) {
        return String.join("\n", getTopPlayersByWins(topN));
    }

    public static String getTopPlayerByRank(int rank) {
        List<PlayerData> topPlayers = getTopPlayerDataByWins(rank);
        if (rank > 0 && rank <= topPlayers.size()) {
            PlayerData playerData = topPlayers.get(rank - 1);
            return playerData.getPlayer().getName() + ": " + playerData.getWins() + " wins";
        } else {
            return "No player at rank " + rank;
        }
    }

    public void savePlayerInventory(ItemStack[] itemStacks, float exp) {
        for (int i = 0; i < itemStacks.length; i++) {
            if (itemStacks[i] != null) {
                data.set("inventory.slot"+i, itemStacks[i]);
            }
        }
        data.set("level.xp", exp);
    }
    public ItemStack[] loadPlayerInventory() {
        ItemStack[] inv = new ItemStack[41];
        for (int i = 0; i < inv.length; i++) {
            if (data.contains("inventory.slot" + i)) {
                inv[i] = data.getItemStack("inventory.slot" + i);
            }
        }
        return inv;
    }
    public float getPlayerXP() {
        return (float) data.get("level.xp");
    }
    public void clearInventoryData() {
        data.set("inventory", null);
        data.set("level", null);
    }
}

