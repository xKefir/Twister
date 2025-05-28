package org.minerail.twister.file.leaderboard;

import org.minerail.twister.util.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardHandler {

    private final Leaderboard leaderboard;

    public LeaderboardHandler(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
        LogUtil.debug("LeaderboardHandler initialized");
    }

    public Map<String, Object> getRawPlayerStats(String playerName) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("player", playerName);
        stats.put("wins", leaderboard.getPlayerValue("wins", playerName));
        stats.put("loses", leaderboard.getPlayerValue("loses", playerName));
        stats.put("totalPlayed", leaderboard.getPlayerValue("totalPlayed", playerName));

        int winsRank = leaderboard.getPlayerRank("wins", playerName);
        int losesRank = leaderboard.getPlayerRank("loses", playerName);
        int totalRank = leaderboard.getPlayerRank("totalPlayed", playerName);

        stats.put("winsRank", winsRank > 0 ? winsRank : null);
        stats.put("losesRank", losesRank > 0 ? losesRank : null);
        stats.put("totalRank", totalRank > 0 ? totalRank : null);

        return stats;
    }

    public List<Leaderboard.TopEntry> getTopEntries(String category) {
        return leaderboard.getTop(category);
    }
}
