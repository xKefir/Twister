package org.minerail.twister.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.minerail.twister.Twister;
import org.minerail.twister.file.config.ConfigFile;
import org.minerail.twister.file.config.ConfigKey;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.file.playerdata.PlayerData;
import org.minerail.twister.game.core.Game;

import java.util.ArrayList;
import java.util.List;

public class GameUtil {
    private static final ConfigFile config = Twister.getConfigFile();
    private static Player winningPlayer;

    // === GŁÓWNE OPERACJE NA GRACZACH ===

    public static boolean addPlayer(Player player) {
        if (player == null || Game.players.contains(player)) {
            return false;
        }

        try {
            Game.players.add(player);
            savePlayerState(player);
            teleportToArena(player);
            InventoryUtil.setupPlayerInventory(player);
            return true;
        } catch (Exception e) {
            LogUtil.error("Failed to add player " + player.getName() + " to game: " + e.getMessage());
            Game.players.remove(player);
            return false;
        }
    }

    public static boolean removePlayer(Player player) {
        if (player == null || !Game.players.contains(player)) {
            return false;
        }
        try {
            Game.players.remove(player);
            restorePlayerState(player);
            return true;
        } catch (Exception e) {
            LogUtil.error("Failed to remove player " + player.getName() + " from game: " + e.getMessage());
            return false;
        }
    }

    public static void kickAllPlayers(String reason) {
        List<Player> playersToKick = new ArrayList<>(Game.players);

        for (Player player : playersToKick) {
            if (player != null && player.isOnline()) {
                removePlayer(player);
                StatsUtil.changeStats(player, reason);
            }
        }

        Game.players.clear();
        LogUtil.debug("Kicked " + playersToKick.size() + " players from game");
    }

    // === TELEPORTACJA ===

    public static void teleportToArena(Player player) {
        Location arenaLocation = LocationUtil.createLocation(
                ConfigKey.ARENA_TP_POS_X,
                ConfigKey.ARENA_TP_POS_Y,
                ConfigKey.ARENA_TP_POS_Z
        );

        if (arenaLocation != null) {
            player.teleport(arenaLocation);
        }
    }

    public static void teleportToLoseArea(Player player) {
        Location loseLocation = LocationUtil.createLocation(
                ConfigKey.ARENA_LOSE_POS_X,
                ConfigKey.ARENA_LOSE_POS_Y,
                ConfigKey.ARENA_LOSE_POS_Z
        );

        if (loseLocation != null) {
            player.teleport(loseLocation);
        }
    }

    // === ZARZĄDZANIE ZWYCIĘZCĄ ===

    public static void determineWinner() {
        try {
            if (Game.players.size() == 1) {
                handleSingleWinner();
            } else {
                handleNoWinner();
            }
        } catch (Exception e) {
            LogUtil.error("Error determining winner: " + e.getMessage());
            handleNoWinner();
        }
    }

    private static void handleSingleWinner() {
        winningPlayer = Game.players.iterator().next();
        removePlayer(winningPlayer);
        StatsUtil.changeStats(winningPlayer, "win");

        MessageDeliverUtil.sendBroadcastWithPrefix(
                MessageKey.MESSAGES_GAME_PLAYER_WIN_BROADCAST,
                Placeholder.component("player", Component.text(winningPlayer.getName()))
        );

        MessageDeliverUtil.sendWithPrefix(winningPlayer, MessageKey.MESSAGES_GAME_PLAYER_WIN_TO_PLAYER);

        LogUtil.info("Game won by: " + winningPlayer.getName());
    }

    private static void handleNoWinner() {
        MessageDeliverUtil.sendBroadcastWithPrefix(MessageKey.MESSAGES_GAME_NO_ONE_IS_WINNER);
        LogUtil.info("Game ended with no winner");
    }

    // === POMOCNICZE METODY ===

    public static boolean isPlayerInGame(Player player) {
        return player != null && Game.players.contains(player);
    }

    public static Player getWinningPlayer() {
        return winningPlayer;
    }


    private static void savePlayerState(Player player) {
        PlayerData.get(player).deserializeAndSavePlayerLocation(player.getLocation());
    }

    private static void restorePlayerState(Player player) {
        InventoryUtil.giveBackPlayerInventory(player);
        player.teleport(PlayerData.get(player).getPlayerJoinLocationFromData());
    }

}