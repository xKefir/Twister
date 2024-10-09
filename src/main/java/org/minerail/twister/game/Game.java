package org.minerail.twister.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.minerail.twister.Twister;
import org.minerail.twister.command.subcommand.Start;
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
    private static final long CLEAR_AREA_DELAY = 2000;
    private static final long GAME_END_DELAY = 1500;
    private static final double MATERIAL_SELECTION_DELAY = 0.35;

    public static Set<Player> players = new HashSet<>();
    public static boolean canJoin = false;
    public static boolean lobbyIsOpen = false;
    public static boolean gameStarted = false;
    private static boolean gameIsEnded = false;
    private static int time;
    private static Location pos1, pos2;
    private static int fieldArea;
    private static Set<String> type1 = new HashSet<>();
    private static double multiplier;
    private static BukkitTask task;

    public static void runLobby(int field, String type) {
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
        new Board(pos1, pos2, field, type1).fillAreasWithMaterials();
        canJoin = true;
        lobbyIsOpen = true;
    }

    public static void runGame() {
        gameStarted = true;
        canJoin = false;
        time = Config.getInt(ConfigKey.SETTINGS_GAME_SECONDS_DURATION_ROUND);
        multiplier = Config.getDouble(ConfigKey.SETTINGS_GAME_SUBTRACTED_TIME_MULTIPLIER);

        task = new GameLoop().runTaskTimer(Twister.get(), 0, 1);
    }

    private static class GameLoop extends BukkitRunnable {
        private double roundTime;
        private long roundStartTime;
        private double incrementPerRound = 0.06;
        private double decrement;
        private boolean areaFilled = false;
        private boolean materialIsSelected = false;
        private Material randomMaterial;
        private int remainingPlayers;
        private long lastClearAreaTime = 0;
        private long xpBarStartTime;
        private double xpBarDuration;
        private boolean isFirstRound = true;

        GameLoop() {
            this.roundTime = time * 1000;
            this.roundStartTime = System.currentTimeMillis();
            this.decrement = roundTime * (multiplier / 100);
            this.remainingPlayers = players.size();
        }

        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - roundStartTime;

            checkGameEnd(currentTime);
            handleMaterialSelection(currentTime, elapsedTime);
            updateXPBars(currentTime);
            handleRoundEnd(currentTime, elapsedTime);
            handleAreaFilling(currentTime);

            if (gameIsEnded) {
                endGame();
            }
        }

        private void checkGameEnd(long currentTime) {
            if (players.size() <= 1 && (currentTime - lastClearAreaTime) >= GAME_END_DELAY) {
                gameIsEnded = true;
                fillArea();
            }
        }

        private void handleMaterialSelection(long currentTime, long elapsedTime) {
            if (!materialIsSelected && elapsedTime >= (roundTime + 2000) * MATERIAL_SELECTION_DELAY && areaFilled) {
                randomMaterial = Board.getRandomMaterial();
                PlayerUtil.preparePlayersInventory(randomMaterial);
                materialIsSelected = true;
                xpBarStartTime = currentTime;
                xpBarDuration = roundTime - elapsedTime;
            }
        }

        private void updateXPBars(long currentTime) {
            if (materialIsSelected) {
                long xpElapsedTime = currentTime - xpBarStartTime;
                double xpProgress = Math.max(0, (xpBarDuration - xpElapsedTime) / xpBarDuration);
                int remainingSeconds = (int) Math.ceil((xpBarDuration - xpElapsedTime) / 1000);
                PlayerUtil.changePlayersXPBar(xpProgress, remainingSeconds);
            }
        }

        private void handleRoundEnd(long currentTime, long elapsedTime) {
            if (elapsedTime >= roundTime) {
                if (materialIsSelected) {
                    clearArea(randomMaterial);
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
                remainingPlayers = players.size();
            }
        }

        private void handleAreaFilling(long currentTime) {
            if (!areaFilled && (currentTime - lastClearAreaTime) >= CLEAR_AREA_DELAY) {
                fillArea();
                areaFilled = true;
                if (!isFirstRound && remainingPlayers == players.size()) {
                    broadcastNoPlayerLost();
                }
                isFirstRound = false;
            }
        }

        private void endGame() {
            PlayerUtil.getWinner();
            new Board(pos1, pos2, fieldArea, type1).clearVariables();
            stop("game");
        }
    }

    public static void stop(String taskType) {
        PlayerUtil.kickAllPlayers("stop");

        if (taskType.equals("game")) {
            if (task != null) {
                task.cancel();
            }
            gameStarted = false;
            gameIsEnded = false;
        } else if (taskType.equals("lobby")) {
            lobbyIsOpen = false;
            canJoin = false;
            Start.num = 0;
            Start.executor = "";
        }
    }

    private static void fillArea() {
        new Board(pos1, pos2, fieldArea, type1).fillAreasWithMaterials();
    }

    private static void clearArea(Material random) {
        new Board(pos1, pos2, fieldArea, type1).removeAllBlocksExcept(random);
    }

    private static void broadcastNoPlayerLost() {
        Bukkit.broadcast(MessageProvider.get(MessageKey.MESSAGES_GAME_NO_ONE_LOST,
                Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)),
                Placeholder.component("remainplayers", Component.text(players.size()))));
    }
}