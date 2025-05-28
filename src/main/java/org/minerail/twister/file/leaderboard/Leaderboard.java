package org.minerail.twister.file.leaderboard;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.file.message.MessageProviderLoader;
import org.minerail.twister.util.LogUtil;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class Leaderboard {
    private final File file;
    private final FileConfiguration config;
    private static final int MAX_ENTRIES = 10;
    private static final Gson gson = new Gson();

    private final Map<String, List<TopEntry>> topMap = new HashMap<>();

    // Definicja obsługiwanych kategorii - KLUCZOWA POPRAWKA
    public static final List<String> SUPPORTED_KEYS = Arrays.asList(
            "wins",
            "loses",
            "totalPlayed"
    );

    public Leaderboard(File dataFolder) {
        this.file = new File(dataFolder, "topstats.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                LogUtil.info("Created leaderboard file at: " + file.getAbsolutePath());
            } catch (Exception e) {
                LogUtil.error("Error occurred while creating leaderboard file: " + e.getMessage());
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);

        // Inicjalizacja map dla wszystkich obsługiwanych kategorii
        for (String key : SUPPORTED_KEYS) {
            topMap.put(key, new ArrayList<>());
        }
        load();
    }

    public void load() {
        String encoded = config.getString("topStats");
        if (encoded != null && !encoded.isEmpty()) {
            try {
                deserialize(encoded);
                LogUtil.debug("Successfully loaded leaderboard data.");
            } catch (Exception e) {
                LogUtil.error("Failed to deserialize leaderboard data: " + e.getMessage());
            }
        } else {
            LogUtil.debug("No leaderboard data found in config file. Starting with empty leaderboards.");
        }
    }

    public void save() {
        try {
            String serialized = serialize();
            config.set("topStats", serialized);
            config.save(file);
            LogUtil.debug("Successfully saved leaderboard data.");
        } catch (Exception e) {
            LogUtil.error("Failed to save topstats.yml: " + e.getMessage());
        }
    }

    // Metoda do pobierania wartości przez PlaceholderAPI
    private int getValueFromPlaceholderAPI(Player player, String category) {
        String placeholder = "%twister_" + category + "%";
        String result = PlaceholderAPI.setPlaceholders(player, placeholder);

        try {
            return Integer.parseInt(result);
        } catch (NumberFormatException e) {
            LogUtil.warn("Failed to parse placeholder " + placeholder + " result: " + result + " for player " + player.getName());
            return 0;
        }
    }

    // NOWA METODA: Bezpośrednia aktualizacja ze statystyk gracza
    public void updateFromPlayerStats(String playerName, int wins, int losses, int totalPlayed) {
        updateCategory("wins", playerName, wins);
        updateCategory("loses", playerName, losses);
        updateCategory("totalPlayed", playerName, totalPlayed);

        save();
        LogUtil.debug("Updated leaderboard for player " + playerName + " from direct stats: W:" + wins + " L:" + losses + " T:" + totalPlayed);
    }

    // Nowa metoda do aktualizacji na podstawie PlaceholderAPI
    public void updateFromPlaceholderAPI(Player player) {
        if (player == null || !player.isOnline()) {
            LogUtil.warn("Cannot update leaderboard - player is null or offline");
            return;
        }

        String playerName = player.getName();
        LogUtil.debug("Attempting to update leaderboard for player: " + playerName);

        boolean hasUpdates = false;
        for (String category : SUPPORTED_KEYS) {
            int value = getValueFromPlaceholderAPI(player, category);
            LogUtil.debug("PlaceholderAPI returned " + value + " for category " + category + " for player " + playerName);

            if (value > 0) { // Tylko aktualizuj jeśli wartość jest większa od 0
                updateCategory(category, playerName, value);
                hasUpdates = true;
            }
        }

        if (hasUpdates) {
            save();
            LogUtil.debug("Updated leaderboard for player " + playerName + " from PlaceholderAPI");
        } else {
            LogUtil.warn("No valid data from PlaceholderAPI for player " + playerName);
        }
    }

    // Metoda do aktualizacji wszystkich graczy na podstawie PlaceholderAPI
    public void updateAllFromPlaceholderAPI(Collection<Player> players) {
        if (players == null || players.isEmpty()) {
            LogUtil.warn("Cannot update leaderboard - no players provided");
            return;
        }

        boolean hasAnyUpdates = false;
        for (Player player : players) {
            if (player == null || !player.isOnline()) {
                continue;
            }

            String playerName = player.getName();
            boolean hasPlayerUpdates = false;

            for (String category : SUPPORTED_KEYS) {
                int value = getValueFromPlaceholderAPI(player, category);
                if (value > 0) {
                    updateCategory(category, playerName, value);
                    hasPlayerUpdates = true;
                    hasAnyUpdates = true;
                }
            }

            if (hasPlayerUpdates) {
                LogUtil.debug("Updated leaderboard data for player: " + playerName);
            }
        }

        if (hasAnyUpdates) {
            save();
            LogUtil.debug("Updated leaderboard for all players from PlaceholderAPI");
        } else {
            LogUtil.warn("No valid data from PlaceholderAPI for any players");
        }
    }

    // Pomocnicza metoda do aktualizacji kategorii
    private void updateCategory(String category, String playerName, int newValue) {
        if (!SUPPORTED_KEYS.contains(category)) {
            LogUtil.error("Attempted to update unsupported category: " + category);
            return;
        }

        List<TopEntry> list = topMap.get(category);
        if (list == null) {
            list = new ArrayList<>();
            topMap.put(category, list);
        }

        Optional<TopEntry> existing = list.stream()
                .filter(e -> e.getPlayerName().equalsIgnoreCase(playerName))
                .findFirst();

        if (existing.isPresent()) {
            TopEntry entry = existing.get();
            if (entry.getValue() != newValue) {
                entry.setValue(newValue);
                LogUtil.debug("Updated " + category + " value for player " + playerName + " from " + entry.getValue() + " to " + newValue);
            }
        } else {
            list.add(new TopEntry(playerName, newValue));
            LogUtil.debug("Added new " + category + " entry for player " + playerName + " with value " + newValue);
        }

        // Sortowanie i ograniczenie do MAX_ENTRIES
        list.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        if (list.size() > MAX_ENTRIES) {
            List<TopEntry> removed = new ArrayList<>(list.subList(MAX_ENTRIES, list.size()));
            list.subList(MAX_ENTRIES, list.size()).clear();
            LogUtil.debug("Removed " + removed.size() + " entries from " + category + " leaderboard to maintain limit");
        }
    }

    // Zachowana stara metoda dla kompatybilności wstecznej
    public void update(String category, String playerName, int newValue) {
        updateCategory(category, playerName, newValue);
        save();
    }

    public List<TopEntry> getTop(String category) {
        if (!SUPPORTED_KEYS.contains(category)) {
            LogUtil.warn("Requested top list for unsupported category: " + category);
            return Collections.emptyList();
        }
        List<TopEntry> list = topMap.get(category);
        return list != null ? new ArrayList<>(list) : Collections.emptyList();
    }

    public int getPlayerRank(String category, String playerName) {
        if (!SUPPORTED_KEYS.contains(category)) {
            LogUtil.warn("Requested player rank for unsupported category: " + category);
            return -1;
        }

        List<TopEntry> list = topMap.get(category);
        if (list == null) return -1;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPlayerName().equalsIgnoreCase(playerName)) {
                return i + 1;
            }
        }

        return -1;
    }

    public int getPlayerValue(String category, String playerName) {
        if (!SUPPORTED_KEYS.contains(category)) {
            LogUtil.warn("Requested player value for unsupported category: " + category);
            return 0;
        }

        List<TopEntry> list = topMap.get(category);
        if (list == null) return 0;

        Optional<TopEntry> entry = list.stream()
                .filter(e -> e.getPlayerName().equalsIgnoreCase(playerName))
                .findFirst();

        return entry.map(TopEntry::getValue).orElse(0);
    }

    public String serialize() {
        String json = gson.toJson(topMap);
        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    public void deserialize(String base64) {
        try {
            String json = new String(Base64.getDecoder().decode(base64));
            Map<String, List<TopEntry>> loaded = gson.fromJson(json,
                    new TypeToken<Map<String, List<TopEntry>>>() {}.getType());

            if (loaded != null) {
                for (String key : SUPPORTED_KEYS) {
                    List<TopEntry> loadedList = loaded.getOrDefault(key, new ArrayList<>());

                    // Sortowanie i ograniczenie
                    loadedList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
                    if (loadedList.size() > MAX_ENTRIES) {
                        loadedList.subList(MAX_ENTRIES, loadedList.size()).clear();
                    }

                    topMap.put(key, loadedList);
                }
                LogUtil.debug("Successfully deserialized leaderboard data for " + topMap.size() + " categories");
            }
        } catch (Exception e) {
            LogUtil.error("Error deserializing leaderboards: " + e.getMessage());
            // Resetowanie do pustych list w przypadku błędu
            for (String key : SUPPORTED_KEYS) {
                topMap.put(key, new ArrayList<>());
            }
        }
    }

    // Metoda do debugowania - pokazuje aktualny stan leaderboardu
    public void debugPrint() {
        LogUtil.info("=== LEADERBOARD DEBUG ===");
        for (String category : SUPPORTED_KEYS) {
            List<TopEntry> entries = topMap.get(category);
            LogUtil.info(category + " (" + (entries != null ? entries.size() : 0) + " entries):");
            if (entries != null) {
                for (int i = 0; i < Math.min(entries.size(), 5); i++) {
                    TopEntry entry = entries.get(i);
                    LogUtil.info("  " + (i + 1) + ". " + entry.getPlayerName() + ": " + entry.getValue());
                }
            }
        }
        LogUtil.info("========================");
    }

    public static class TopEntry {
        private final String playerName;
        private int value;

        public TopEntry(String playerName, int value) {
            this.playerName = playerName;
            this.value = value;
        }

        public String getPlayerName() { return playerName; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }

        @Override
        public String toString() {
            return playerName + ": " + value;
        }
    }
}