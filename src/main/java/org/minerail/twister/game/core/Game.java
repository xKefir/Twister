package org.minerail.twister.game.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.minerail.twister.Twister;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.game.animation.BoardAnimation;
import org.minerail.twister.game.animation.CountDownXPBarAnimation;
import org.minerail.twister.game.animation.HotbarAnimation;
import org.minerail.twister.game.board.Board;
import org.minerail.twister.game.board.BoardBuilder;
import org.minerail.twister.util.LogUtil;
import org.minerail.twister.util.MessageDeliverUtil;

import java.util.List;

public class Game {
    //Init
    private GameController controller;
    private Round round;
    private PlayerHandler playerHandler;
    public Board board;

    //Variables
    protected ItemStack randomItem;
    protected Material randomMaterial;
    private BukkitTask gameTask;
    private List<String> materials;
    private long beforeRoundStartTick;

    //Flags
    private boolean beforeRoundStarted = false;

    //Animations
    BoardAnimation boardAnimation;
    HotbarAnimation hotbarAnimation;
    CountDownXPBarAnimation countDownXPBarAnimation;

    public Game(GameController controller, Round round, PlayerHandler handler) {
        this.controller = controller;
        this.round = round;
        this.playerHandler = controller.getPlayerHandler();
        init();
    }

    private void init() {
        boardAnimation = new BoardAnimation();
        hotbarAnimation = new HotbarAnimation();
        countDownXPBarAnimation = new CountDownXPBarAnimation();
    }

    //Run Countdown
    protected void runCountdown() {
        LogUtil.debug("Starting countdown with duration: " + controller.COUNTDOWN + " ticks");
        countDownXPBarAnimation.start(controller.COUNTDOWN).then(() -> {
            runGame();
            LogUtil.debug("Countdown completed! Running game...");
        });
    }

    protected void runGame() {
        controller.transitionGameStateTo(GameController.GameState.BEFORE_ROUND);
        gameTask = Bukkit.getScheduler().runTaskTimer(Twister.get(), () -> {
            controller.currentTick++;

            if (playerHandler.getPlayersList().size() < controller.MIN_PLAYERS) {
                controller.transitionGameStateTo(GameController.GameState.FINISHED);
            }

            switch (controller.getCurrentGameState()) {
                case GameController.GameState.BEFORE_ROUND -> handleBeforeRoundState();
                case GameController.GameState.ROUND_END -> handleRoundEndState();
                case GameController.GameState.FINISHED -> handleFinishedState();

            }
        }, 0, 1L);
    }

    private void handleBeforeRoundState() {
        if (!beforeRoundStarted) {
            beforeRoundStartTick = controller.currentTick;
            beforeRoundStarted = true;
            LogUtil.debug("BEFORE_ROUND state started at tick: " + controller.currentTick);
        }

        if (boardAnimation.isRunning()) {
            return;
        }

        long elapsedTicks = controller.currentTick;

        if (controller.getRoundHandler().isFirstRound()) {
            runBoardAnimation();
        } else if ((elapsedTicks - beforeRoundStartTick) >= controller.CLEAR_AREA_DELAY) {
            if (!boardAnimation.isRunning()) {
                beforeRoundStarted = false;
                beforeRoundStartTick = 0;

                if (!playerHandler.checkIfSomeoneLost()) {
                    MessageDeliverUtil.sendBroadcastWithPrefix(
                            MessageKey.MESSAGES_GAME_NO_ONE_LOST,
                            Placeholder.component("remainplayers",
                                    Component.text(playerHandler.getPlayersList().size()))
                    );
                }

                LogUtil.debug("Starting board animation after " + elapsedTicks + " ticks delay");
                runBoardAnimation();
            }
        }
    }


    private void handleRoundEndState() {
        if (!board.areaCleared) {
            LogUtil.debug("Clearing board except material: " + randomMaterial);
            board.removeAllBlocksExcept(randomMaterial);
            board.areaCleared = true;
        }

        board.areaCleared = false;
        beforeRoundStarted = false;
        beforeRoundStartTick = 0;

        LogUtil.debug("ROUND_END -> BEFORE_ROUND transition");
        controller.transitionGameStateTo(GameController.GameState.BEFORE_ROUND);
    }

    private void handleFinishedState() {
        board.fillAreasWithMaterials();
        revealWinner();
        playerHandler.removeAllPlayers();
        cleanup();
    }

    public void stop() {
        LogUtil.debug("Stopping game forcefully");
        controller.transitionGameStateTo(GameController.GameState.FINISHED);
        cleanup();
    }

    protected void cleanup() {
        if (gameTask != null) {
            gameTask.cancel();
        }
        // Stop all animations
        if (boardAnimation.isRunning()) boardAnimation.stop();
        if (hotbarAnimation.isRunning()) hotbarAnimation.stop();
        if (countDownXPBarAnimation.isRunning()) countDownXPBarAnimation.stop();

        controller.getRoundHandler().clear();
        playerHandler.clear();
        board.cleanupVariables();

        // Reset flags
        beforeRoundStarted = false;
        beforeRoundStartTick = 0;

        LogUtil.debug("Game cleanup completed");
    }

    private void revealWinner() {
        if (playerHandler.getPlayersList().size() == 0) {
            MessageDeliverUtil.sendBroadcastWithPrefix(
                    MessageKey.MESSAGES_GAME_NO_ONE_IS_WINNER
            );
        } else {
            Player winner = playerHandler.getPlayersList().iterator().next();
            MessageDeliverUtil.sendBroadcastWithPrefix(
                    MessageKey.MESSAGES_GAME_PLAYER_WIN_BROADCAST,
                    Placeholder.component("player", Component.text(winner.getName()))
            );
            MessageDeliverUtil.sendWithPrefix(
                    winner,
                    MessageKey.MESSAGES_GAME_PLAYER_WIN_TO_PLAYER
            );
            playerHandler.winningPlayer(winner);
        }
    }

    //Initialize and setup area
    protected void initBoard(int fieldSize, String type) {
        materials = Blocks.getMaterialList(type);
        board = new BoardBuilder()
                .setCorners()
                .setFieldSize(fieldSize)
                .setMaterials(materials)
                .build();
        board.fillAreasWithMaterials();
        LogUtil.debug("Board initialized with size: " + fieldSize + ", type: " + type);
    }

    //Run BoardAnimation
    private void runBoardAnimation() {
        LogUtil.debug("Starting BoardAnimation");
        boardAnimation.start(board).then(() -> {
            LogUtil.debug("BoardAnimation completed! Starting new round...");
            round.startNewRound();
        });
    }

    protected void runHotbarAnimation() {
        long durationTicks = calculateHotbarAnimationDuration();
        LogUtil.debug("Starting HotbarAnimation with duration: " + durationTicks + " ticks");

        hotbarAnimation.start(durationTicks).then(() -> {
            randomMaterial = board.getRandomMaterial();
            randomItem = new ItemStack(randomMaterial);

            for (Player p : playerHandler.getPlayersList()) {
                for (int i = 0; i < 9; i++) {
                    p.getInventory().setItem(i, randomItem);
                }
            }

            LogUtil.debug("HotbarAnimation completed! Selected material: " + randomMaterial);
        });
    }

    public void runXPBarAnimation() {
        long durationTicks = round.getRoundDurationTime();
        LogUtil.debug("Starting XPBarAnimation with duration: " + durationTicks + " ticks");

        countDownXPBarAnimation.start(durationTicks).then(() -> {
            LogUtil.debug("XPBarAnimation completed! Ending round...");
            round.endCurrentRound();
        });
    }

    private long calculateHotbarAnimationDuration() {
        long roundDurationTicks = round.getRoundDurationTime();
        return (long) (roundDurationTicks * controller.MATERIAL_SELECTION_DELAY);
    }
}