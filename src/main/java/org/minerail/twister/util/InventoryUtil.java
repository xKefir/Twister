package org.minerail.twister.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.minerail.twister.file.playerdata.PlayerData;
import org.minerail.twister.game.core.Game;

public class InventoryUtil {

    public static void setupPlayerInventory(Player p) {
        PlayerData.get(p).savePlayerInventory(getPlayerFullInventory(p), p.getExp(), p.getLevel());
        p.getInventory().clear();
        p.setExp(0);
        p.setLevel(0);
    }

    public static void giveBackPlayerInventory(Player p) {
        ItemStack[] inventory = PlayerData.get(p).loadPlayerInventory();
        p.getInventory().setContents(inventory);
        p.getInventory().setArmorContents(new ItemStack[]{inventory[36], inventory[37], inventory[38], inventory[39]});
        p.getInventory().setItemInOffHand(inventory[40]);
        p.setExp(PlayerData.get(p).getXP());
        p.setLevel(PlayerData.get(p).getLevel());
        PlayerData.get(p).clearInventoryData();
    }

    public static ItemStack[] getPlayerFullInventory(Player player) {
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

    public static void preparePlayersInventory(Material random) {
        for (Player p : Game.players) {
            for (int i = 0; i <= 8; i++) {
                p.getInventory().setItem(i, new ItemStack(random));
            }
        }
    }

    public static void clearInventories() {
        for (Player p : Game.players) {
            p.getInventory().clear();
            p.setExp(0);
        }
    }

    public static void changePlayersXPBar(double time, int level) {
        for (Player p : Game.players) {
            p.setExp((float) time);
            p.setLevel(level);
        }
    }
}