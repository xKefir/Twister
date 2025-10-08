package org.minerail.twister.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.minerail.twister.Twister;
import org.minerail.twister.file.playerdata.PlayerData;

public class PlaceholderApiHook extends PlaceholderExpansion {
    private final Twister plugin;

    public PlaceholderApiHook(Twister plugin) {
        this.plugin = plugin;
    }


    @Override
    public @NotNull String getIdentifier() {
        return "Twister";
    }

    @Override
    public @NotNull String getAuthor() {
        return "x_Kefir";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        return switch (params) {
            case "wins" -> String.valueOf(PlayerData.get(player.getPlayer()).getPlayerStats().getWins());
            case "loses" -> String.valueOf(PlayerData.get(player.getPlayer()).getPlayerStats().getLosses());
            case "totalPlayed" -> String.valueOf(PlayerData.get(player.getPlayer()).getPlayerStats().getGamesPlayed());
           // case "last_winner" -> (GameUtil.getWinningPlayer() != null) ? GameUtil.getWinningPlayer().getName() : "null";
            default -> null;
        };
    }
}
