package org.minerail.twister.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.minerail.twister.file.Config.Config;
import org.minerail.twister.file.Config.ConfigKey;
import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.file.Message.MessageProvider;
import org.minerail.twister.file.PlayerData;
import org.minerail.twister.game.Game;

public class PlayerUtil {
    private static String winner;
    public static void playerJoinToGame(Player p) {
        if (!Game.players.contains(p.getUniqueId().toString())) {
            Game.players.add(p.getUniqueId().toString());
            p.teleport(LocationUtil.serializeLocation(
                  Config.getDouble(ConfigKey.ARENA_TP_POS_X),
                  Config.getDouble(ConfigKey.ARENA_TP_POS_Y),
                  Config.getDouble(ConfigKey.ARENA_TP_POS_Z),
                  Config.getString(ConfigKey.ARENA_WORLD)
            ));
            setupPlayerInventory(p);
            PlayerData.get(p).addTotalPlayed(1);
        }
    }

    public static void playerRemoveFromGame(Player p) {
        if (Game.players.size() <= 1) {
            winner = Game.players.iterator().next();
        }
        Game.players.remove(p.getUniqueId().toString());
        PlayerData.get(p).addLoses(1);
        giveBackPlayersInventory(p);
    }
    public static boolean playerIsInGame(Player p) {
        if (Game.players.contains(p.getUniqueId().toString())) {
            return true;
        }
        return false;
    }
    public static void getWinner() {
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
    public static void preparePlayersInventory(Material random) {

    }
    private static void setupPlayerInventory(Player p) {
        PlayerData.get(p).savePlayerInventory(getPlayerFullInventory(p), p.getExp());
        p.getInventory().clear();
        p.setExp(0f);

    }
    private static void giveBackPlayersInventory(Player p) {
        ItemStack[] inventory = PlayerData.get(p).loadPlayerInventory();
        p.getInventory().setContents(inventory);
        p.getInventory().setArmorContents(new ItemStack[]{inventory[36], inventory[37], inventory[38], inventory[39]});
        p.getInventory().setItemInOffHand(inventory[40]);
        p.setExp(PlayerData.get(p).getPlayerXP());
        PlayerData.get(p).clearInventoryData();
    }
    public static void changePlayersXPBar(double time) {}


    private static ItemStack[] getPlayerFullInventory(Player player) {
        if (player != null) {
            ItemStack[] inventoryContents = player.getInventory().getContents(); // Main inventory
            ItemStack[] armorContents = player.getInventory().getArmorContents(); // Armor
            ItemStack[] extraContents = player.getInventory().getExtraContents(); // External items like offhand

            ItemStack[] fullInventory = new ItemStack[inventoryContents.length + armorContents.length + extraContents.length];

            System.arraycopy(inventoryContents, 0, fullInventory, 0, inventoryContents.length);
            System.arraycopy(armorContents, 0, fullInventory, inventoryContents.length, armorContents.length);
            System.arraycopy(extraContents, 0, fullInventory, inventoryContents.length + armorContents.length, extraContents.length);

            return fullInventory;
        }
        return new ItemStack[0]; // Return empty list if player is null
    }
}
