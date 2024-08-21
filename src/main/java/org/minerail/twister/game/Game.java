package org.minerail.twister.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.minerail.twister.Twister;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.file.Config.Config;
import org.minerail.twister.file.Config.ConfigKey;
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
            private double roundTime = time;
            private long roundStartTime = System.currentTimeMillis();
            private double incrementPerRound = 0.06;
            private double decrement = roundTime * (multiplier / 100);
            private long lastClearAreaTime = System.currentTimeMillis();
            private boolean areaFilled = false;
            private boolean materialIsSelected = false;
            private Material randomMaterial;
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - roundStartTime;

                if (!materialIsSelected) {
                    randomMaterial = Board.getRandomMaterial();
                    if (elapsedTime >= roundTime * 0.25) {
                        PlayerUtil.preparePlayersInventory(randomMaterial);
                        materialIsSelected = true;
                    }
                }

                if (players.size() <= 1) {
                    gameIsEnded = true;
                    fillArea();
                }

                if (elapsedTime >= roundTime * 1000) {
                    if (materialIsSelected) {
                        Game.clearArea(randomMaterial);
                        materialIsSelected = false;
                    }

                    roundTime -= decrement;
                    decrement += incrementPerRound;

                    if (roundTime < 1) {
                        roundTime = 1;
                    }

                    areaFilled = false;
                    lastClearAreaTime = currentTime;
                    roundStartTime = currentTime;

                }

                long fillAreaElapsedTime = currentTime - lastClearAreaTime;

                if (!areaFilled && fillAreaElapsedTime >= (roundTime * 1000) / 2) {
                    Game.fillArea();
                    areaFilled = true;
                }

                if (gameIsEnded && fillAreaElapsedTime >= 2000) {
                    PlayerUtil.getWinner();
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
        players.clear();
    }

    private static void fillArea() {
        new Board(pos1, pos2, fieldArea, type1).fillAreasWithMaterials();
    }

    private static void clearArea(Material random) {
        new Board(pos1, pos2, fieldArea, type1).removeAllBlocksExcept(random);
    }
}
