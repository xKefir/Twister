package org.minerail.twister.util;

import org.bukkit.entity.Player;
import org.minerail.twister.file.playerdata.PlayerData;

public class StatsUtil {

    public static void changeStats(Player p, String type) {
        switch (type) {
            case "lose" -> {
                PlayerData.get(p).getPlayerStats().addLoss();
            }
            case "win" -> {
                PlayerData.get(p).getPlayerStats().addWin();
            }
            case "quit" -> PlayerData.get(p).getPlayerStats().removeLoss();
            case "stop" -> PlayerData.get(p).getPlayerStats().addGamesPlayed();
        }
    }
}
