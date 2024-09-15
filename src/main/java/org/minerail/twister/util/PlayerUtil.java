package org.minerail.twister.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.minerail.twister.Twister;
import org.minerail.twister.file.Config.Config;
import org.minerail.twister.file.Config.ConfigKey;
import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.file.Message.MessageProvider;
import org.minerail.twister.file.PlayerData;
import org.minerail.twister.game.Game;

import java.util.Objects;


public class PlayerUtil {
    public static String winner;

    public static void playerJoinToGame(Player p) {
        if (!Game.players.contains(p.getName())) {
            Game.players.add(p.getName());
            storePlayerLocation(p.getLocation(), p);
            p.teleport(LocationUtil.serializeLocation(
                  Config.getDouble(ConfigKey.ARENA_TP_POS_X),
                  Config.getDouble(ConfigKey.ARENA_TP_POS_Y),
                  Config.getDouble(ConfigKey.ARENA_TP_POS_Z),
                  Config.getString(ConfigKey.ARENA_WORLD)
            ));
            setupPlayerInventory(p);
        }
    }

    public static void playerRemoveFromGame(Player p) {
        Game.players.remove(p.getName());
        PlayerData.get(p).addLoses(1);
        PlayerData.get(p).addTotalPlayed(1);
        giveBackPlayersInventory(p);;
    }

    public static boolean playerIsInGame(Player p) {
        return Game.players.contains(p.getName());
    }

    public static void preparePlayersInventory(Material random) {
        for (String p : Game.players) {
            for (int i = 0; i <= 8; i++) {
                Bukkit.getPlayer(p).getInventory().setItem(i, new ItemStack(random));
            }
        }
    }
    public static void clearInventories() {
        for (String p : Game.players) {
            Bukkit.getPlayer(p).getInventory().clear();
            Bukkit.getPlayer(p).setExp(0);
        }
    }
    public static void changePlayersXPBar(double time, int level) {
        for (String p : Game.players) {
            Player player = Bukkit.getPlayer(p);
            if (player != null) {
                player.setExp((float) time);
                player.setLevel(level);
            }
        }
    }

    private static void setupPlayerInventory(Player p) {
        PlayerData.get(p).savePlayerInventory(getPlayerFullInventory(p), p.getExp(), p.getLevel());
        p.getInventory().clear();
        p.setExp(0);
        p.setLevel(0);
    }

    private static void giveBackPlayersInventory(Player p) {
        ItemStack[] inventory = PlayerData.get(p).loadPlayerInventory();
        p.getInventory().setContents(inventory);
        p.getInventory().setArmorContents(new ItemStack[]{inventory[36], inventory[37], inventory[38], inventory[39]});
        p.getInventory().setItemInOffHand(inventory[40]);
        p.setExp(PlayerData.get(p).getXP());
        p.setLevel(PlayerData.get(p).getLevel());
        PlayerData.get(p).clearInventoryData();
    }

    private static ItemStack[] getPlayerFullInventory(Player player) {
        if (player != null) {
            ItemStack[] inventoryContents = player.getInventory().getContents();
            ItemStack[] armorContents = player.getInventory().getArmorContents();
            ItemStack[] extraContents = player.getInventory().getExtraContents();

            ItemStack[] fullInventory = new ItemStack[inventoryContents.length + armorContents.length + extraContents.length];

            System.arraycopy(inventoryContents, 0, fullInventory, 0, inventoryContents.length);
            System.arraycopy(armorContents, 0, fullInventory, inventoryContents.length, armorContents.length);
            System.arraycopy(extraContents, 0, fullInventory, inventoryContents.length + armorContents.length, extraContents.length);

            return fullInventory;
        }
        return new ItemStack[0];
    }

    private static void storePlayerLocation(Location loc, Player p) {
        PlayerData.get(p).deserializeAndSavePlayerLocation(loc);
    }

    public static void teleportPlayerToArena(Player p) {
        p.teleport(LocationUtil.serializeLocation(
                Config.getDouble(ConfigKey.ARENA_TP_POS_X),
                Config.getDouble(ConfigKey.ARENA_TP_POS_Y),
                Config.getDouble(ConfigKey.ARENA_TP_POS_Z),
                Config.getString(ConfigKey.ARENA_WORLD)
        ));
    }

    public static void getWinner() {
        if (Game.players.size() == 1) {
            winner = Game.players.iterator().next();
            kickAllPlayers();
        }
        if (winner != null) {
            Bukkit.broadcast(MessageProvider.get(MessageKey.MESSAGES_GAME_PLAYER_WIN_BROADCAST,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)),
                    Placeholder.component("player", Component.text(Bukkit.getPlayer(winner).getName()))));

            Bukkit.getPlayer(winner).sendMessage(MessageProvider.get(MessageKey.MESSAGES_GAME_PLAYER_WIN_TO_PLAYER,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING))));

            PlayerData.get(Bukkit.getPlayer(winner)).addWins(1);

        } else {
            Bukkit.broadcast(MessageProvider.get(MessageKey.MESSAGES_GAME_NO_ONE_IS_WINNER,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING))));
        }
    }


    public static void kickAllPlayers() {
        for (String p : Game.players) {
            if (p != null) {
                playerRemoveFromGame(Objects.requireNonNull(Bukkit.getPlayer(p)));
            }
        }
    }


}
