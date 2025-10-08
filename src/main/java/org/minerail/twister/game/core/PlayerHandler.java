package org.minerail.twister.game.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.minerail.twister.event.PlayerJoinedToGame;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.file.playerdata.PlayerData;
import org.minerail.twister.util.MessageDeliverUtil;
import org.minerail.twister.util.StatsUtil;

import java.util.*;

public class PlayerHandler {
    private GameController controller;

    private final Set<Player> players = new HashSet<>();
    private int playerListSize;

    public PlayerHandler(GameController controller) {
        this.controller = controller;
    }

    public void eliminatePlayer(Player p) {
        if (players.contains(p)) {
            MessageDeliverUtil.sendBroadcastWithPrefix(MessageKey.MESSAGES_GAME_PLAYER_LOSE_BROADCAST,
                    Placeholder.component("player", Component.text(p.getName())),
                    Placeholder.component("remainplayers", Component.text(players.size())));
            MessageDeliverUtil.sendWithPrefix(p, MessageKey.MESSAGES_GAME_PLAYER_LOSE_TO_PLAYER);
            removePlayer(p);
            StatsUtil.changeStats(p, "lose");
        }
    }
    public boolean checkIfSomeoneLost() {
        if (playerListSize > players.size()) {
            playerListSize = players.size();
            return true;
        }
        return false;
    }

    public void teleportTo(Player p, Location loc) {
        p.teleport(loc);
    }

    public void teleportAllPlayersTo(Location loc) {
        players.forEach(player -> teleportTo(player, loc));
    }

    public void removeAllPlayers() {
        teleportAllPlayersTo(controller.getLoseLocation());
        players.forEach(player -> {
            restorePlayerInventory(player);
        });
        players.clear();
    }

    public boolean addPlayer(Player p) {
        if (!players.contains(p)) {
            players.add(p);
            playerListSize = players.size();
            PlayerData.get(p).deserializeAndSavePlayerLastLocation(p.getLocation());
            Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinedToGame(p));
            teleportTo(p, controller.getTpLocation());
            return true;
        }
        return false;
    }

    public boolean removePlayer(Player p) {
        teleportTo(p, Optional.ofNullable(PlayerData.get(p).getPlayerLastLocationFromData()).orElse(controller.getLoseLocation()));
        restorePlayerInventory(p);
        return players.remove(p);
    }

    private void restorePlayerInventory(Player p) {
        p.getInventory().setContents(PlayerData.get(p).loadPlayerInventory());
        p.setExp(PlayerData.get(p).getXP());
        p.setLevel(PlayerData.get(p).getLevel());
    }

    //Supporting code methods
    public Set<Player> getPlayersList() {
        return this.players;
    }

    public void clear() {
        players.clear();
    }

    public void winningPlayer(Player winner) {
        removePlayer(winner);
        StatsUtil.changeStats(winner, "win");
    }
}
