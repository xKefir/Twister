package org.minerail.twister.game.core;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.minerail.twister.Twister;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.file.config.ConfigFile;
import org.minerail.twister.game.animation.BoardAnimation;
import org.minerail.twister.game.animation.CountDownXPBarAnimation;
import org.minerail.twister.game.animation.HotbarAnimation;
import org.minerail.twister.game.board.Board;
import org.minerail.twister.game.board.BoardBuilder;
import org.minerail.twister.util.LogUtil;
import java.util.Set;

public class Game {

    /*
    Gra zostaje uruchomiona i zaczyna się mieszanie areny oraz odliczanie do rozpoczęcia rundy
    Runda się kończy rozpoczyna się delay 2s do uzupelnienia areny. Następnie przez 1 sekundę miesza arenę.
    Rozpoczyna się nowa runda i mieszanie hotbara.
    *no i tak w kółko*
     */

    //Init
    private ConfigFile configFile = Twister.getConfigFile();
    private GameController controller;
    private Round round;
    public Board board;

    //Variables
    protected ItemStack randomItem;
    protected Material randomMaterial;
    private BukkitTask gameTask;
    private volatile Set<String> materials;
    private volatile long RUNNING_GAME_START_TIME;
    private volatile long FINISH_GAME_STATUS_STARTED;

    //Flags
    private volatile boolean runningStateStarted = false;
    private volatile boolean finishStateStarted = false;

    //Animations
    BoardAnimation boardAnimation = new BoardAnimation();
    HotbarAnimation hotbarAnimation = new HotbarAnimation();
    CountDownXPBarAnimation countDownXPBarAnimation = new CountDownXPBarAnimation();

    public Game(GameController controller, Round round) {
        this.controller = controller;
        this.round = round;
    }

    //Run Countdown
    protected void runCountdown() {
        runCountdownAnimation(controller.COUNTDOWN);
    }

    //Run CountdownXPBarAnimation
    protected void runCountdownAnimation(long countdown) {
        countDownXPBarAnimation.start(countdown).then(() -> {
            runGame();
            LogUtil.debug("CountdownXPBarAnimation completed! Running game...");
        });
    }

    //Run Game
    protected void runGame() {
        controller.transitionGameStateTo(GameController.GameState.RUNNING);
        gameTask = Bukkit.getScheduler().runTaskTimer(Twister.get(), () -> {
            controller.currentTime = System.currentTimeMillis(); //Updating current time for other classes
            switch (controller.getCurrentGameState()) {
                case GameController.GameState.RUNNING -> {
                    if (!runningStateStarted) {
                        RUNNING_GAME_START_TIME = controller.currentTime;
                        runningStateStarted = true;
                    }
                    if ((controller.currentTime - RUNNING_GAME_START_TIME) >= controller.CLEAR_AREA_DELAY) {
                        if (controller.getPlayersList().size() <= controller.MIN_PLAYERS) controller.transitionGameStateTo(GameController.GameState.FINISHED);
                        if (!boardAnimation.isRunning()) runBoardAnimation();
                    }
                    //Etap między rundami, rozpoczyna się delay na uzupełnienie areny i jej pomieszanie a następnie rozpoczecie rundy
                }
//                case GameController.GameState.ROUND_RUNNING -> {}
                case GameController.GameState.ROUND_END -> {
                    board.removeAllBlocksExcept(randomMaterial);
                }
                case GameController.GameState.FINISHED -> {
                    if (!finishStateStarted) {
                        FINISH_GAME_STATUS_STARTED = controller.currentTime;
                        finishStateStarted = true;
                    }
                    if ((controller.currentTime - FINISH_GAME_STATUS_STARTED) >= controller.GAME_END_DELAY) {
                        round.clear();
                        gameTask.cancel();
                    }

                }
            }
        }, 0, 1L);
    }

    //Initialize and setup area
    protected void initBoard(int fieldSize, String type) {
        materials = Blocks.getMaterialList(type);
        board = new BoardBuilder().setCorners().setFieldSize(fieldSize).setMaterials(materials).build();
        board.fillAreasWithMaterials();
    }

    //Run BoardAnimation
    private void runBoardAnimation() {
        boardAnimation.start(board).then(() -> {
            round.startNewRound();
            LogUtil.debug("BoardAnimation completed! Starting new round...");
        });
    }

    protected void runHotbarAnimation() {
        hotbarAnimation.start(calculateHotbarAnimationDuration()).then(() -> {
            randomMaterial = board.getRandomMaterial();
            randomItem = new ItemStack(randomMaterial);
            for (Player p : controller.getPlayersList()) {
                for (int i = 0; i < 9; i++) {
                    p.getInventory().setItem(i, randomItem);
                }
            }
            LogUtil.debug("Set up players inventories");
            LogUtil.debug("HotbarAnimation completed!");
        });
    }

    private long calculateHotbarAnimationDuration() {
        return (long) (round.getRoundDurationTime() * controller.MATERIAL_SELECTION_DELAY * 1);
    }




    //PlayerState methods
//    public void transitionPlayerStateTo(PlayerState newState) {
//        this.currentPlayerState = newState;
//    }
//    public PlayerState getCurrentPlayerState() {
//        return this.currentPlayerState;
//    }

}