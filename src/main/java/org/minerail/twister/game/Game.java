package org.minerail.twister.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.minerail.twister.Twister;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.file.Config.Config;
import org.minerail.twister.file.Config.ConfigKey;
import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.file.Message.MessageProvider;
import org.minerail.twister.util.LocationUtil;
import org.minerail.twister.util.PlayerUtil;

import java.util.HashSet;
import java.util.Set;

public class Game {

    public static Set<String> players = new HashSet<>();
    public static boolean canJoin = false;
    public static boolean gameStarted = false;
    private static int time = Config.getInt(ConfigKey.SETTINGS_GAME_SECONDS_DURATION_ROUND);
    private static Location pos2;
    private static Location pos1;
    private static int fieldArea;
    private static Set<String> type1 = new HashSet<>();
    private static double multiplier = Config.getDouble(ConfigKey.SETTINGS_GAME_SUBTRACTED_TIME_MULTIPLIER);
    private static BukkitRunnable task;
    private static boolean gameIsEnded = false;

    public static void runlLobby(int field, String type) {
        pos1 = LocationUtil.serializeLocation(
                Config.getDouble(ConfigKey.ARENA_POS1_X),
                Config.getDouble(ConfigKey.ARENA_POS1_Y),
                Config.getDouble(ConfigKey.ARENA_POS1_Z),
                Config.getString(ConfigKey.ARENA_WORLD)
        );
        pos2 = LocationUtil.serializeLocation(
                Config.getDouble(ConfigKey.ARENA_POS2_X),
                Config.getDouble(ConfigKey.ARENA_POS2_Y),
                Config.getDouble(ConfigKey.ARENA_POS2_Z),
                Config.getString(ConfigKey.ARENA_WORLD)
        );
        fieldArea = field;
        type1 = Blocks.getMaterialList(type);
        new Board(pos1, pos2, field, Blocks.getMaterialList(type)).fillAreasWithMaterials();
        canJoin = true;
    }

    public static void runGame() {
        gameStarted = true;
        canJoin = false;

        task = new BukkitRunnable() {
            private double roundTime = time * 1000;
            private long roundStartTime = System.currentTimeMillis();
            private double incrementPerRound = 0.06;
            private double decrement = roundTime * (multiplier / 100);
            private boolean areaFilled = false;
            private boolean materialIsSelected = false;
            private Material randomMaterial;
            private int remainingPlayers = players.size();
            private long lastClearAreaTime = 0;
            private long xpBarStartTime;
            private double xpBarDuration;

            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - roundStartTime;

                if (players.size() <= 1) {
                    if ((currentTime - lastClearAreaTime) >= 1500) {
                        gameIsEnded = true;
                        fillArea();
                    }
                }

                if (!materialIsSelected) {
                    randomMaterial = Board.getRandomMaterial();
                    if (elapsedTime >= (roundTime + 2000) * 0.35 && areaFilled) {
                        PlayerUtil.preparePlayersInventory(randomMaterial);
                        materialIsSelected = true;

                        xpBarStartTime = currentTime;
                        xpBarDuration = roundTime - elapsedTime;
                    }
                }

                if (materialIsSelected) {
                    long xpElapsedTime = currentTime - xpBarStartTime;
                    double xpProgress = Math.max(0, (xpBarDuration - xpElapsedTime) / xpBarDuration);
                    int remainingSeconds = (int) Math.ceil((xpBarDuration - xpElapsedTime) / 1000);

                    PlayerUtil.changePlayersXPBar(xpProgress, remainingSeconds);
                }

                if (elapsedTime >= roundTime) {
                    if (materialIsSelected) {
                        Game.clearArea(randomMaterial);
                        PlayerUtil.clearInventories();
                        lastClearAreaTime = currentTime;
                        areaFilled = false;
                        materialIsSelected = false;
                        PlayerUtil.changePlayersXPBar(1.0, 0);
                    }
                    roundTime -= decrement;
                    decrement += incrementPerRound;
                    if (roundTime < 1) {
                        roundTime = 1;
                    }

                    roundStartTime = currentTime;
                }
                if (!areaFilled && (currentTime - lastClearAreaTime) >= 2000) {
                    Game.fillArea();
                    areaFilled = true;
                    if (remainingPlayers == players.size()) {
                        Bukkit.broadcast(MessageProvider.get(MessageKey.MESSAGES_GAME_NO_ONE_LOST,
                                Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)),
                                Placeholder.component("remainplayers", Component.text(players.size()))));
                    }
                }

                if (gameIsEnded) {
                    PlayerUtil.getWinner();
                    new Board(pos1, pos2, fieldArea, type1).clearVariables();
                    stop();
                }
            }
        };

        task.runTaskTimer(Twister.get(), 0, 1);
    }


    public static void stop() {
        if (task != null) {
            task.cancel();
        }
        gameStarted = false;
        canJoin = false;
        gameIsEnded = false;
        PlayerUtil.kickAllPlayers();
    }

    private static void fillArea() {
        new Board(pos1, pos2, fieldArea, type1).fillAreasWithMaterials();
    }

    private static void clearArea(Material random) {
        new Board(pos1, pos2, fieldArea, type1).removeAllBlocksExcept(random);
    }
}
