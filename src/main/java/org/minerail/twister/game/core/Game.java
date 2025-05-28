package org.minerail.twister.game.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.minerail.twister.Twister;
import org.minerail.twister.event.GameStateChangeEvent;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.file.config.ConfigFile;
import org.minerail.twister.game.animation.BoardAnimation;
import org.minerail.twister.game.animation.HotbarAnimation;
import org.minerail.twister.game.board.Board;
import org.minerail.twister.game.board.BoardBuilder;
import org.minerail.twister.util.LogUtil;

import java.util.HashSet;
import java.util.Set;

public class Game {
    //Flags
    public enum LobbyState {
        OPEN,
        CLOSED;
    }
    private volatile boolean lobbyJoiningPhase = false;

//    public enum PlayerState {
//        IN_LOBBY,
//        PLAYING,
//        ELIMINATED,
//        SPECTATING,
//        OUT_OF_GAME;
//    }

    public enum GameState {
        //Waiting for running game
        WAITING,
        RUNNING,
        //Before starting round - runs Board Animation
        COUNTDOWN,
        ROUND_RUNNING,
        ROUND_END,
        FINISHED;
    }
    //States
    private volatile GameState currentGameState = GameState.FINISHED;
    private volatile LobbyState currentLobbyState = LobbyState.CLOSED;
//    private volatile PlayerState currentPlayerState = PlayerState.OUT_OF_GAME;

    //Game information
    private final Set<Player> players = new HashSet<>();
    private int round;

    //Init
    private ConfigFile configFile = Twister.getConfigFile();

    //Game config
    private final long CLEAR_AREA_DELAY = 2000L;
    private final long GAME_END_DELAY = 2000L;
    private final double MATERIAL_SELECTION_DELAY = 0.35;
    private final double INCREMENT_PER_ROUND = 0.06;
    private final long MIN_ROUND_TIME = 1L;
    private Board board;
    private BukkitTask gameTask;
    private volatile Set<String> materials;

    //Animations
    BoardAnimation boardAnimation = new BoardAnimation();
    HotbarAnimation hotbarAnimation = new HotbarAnimation();


    //Open Lobby and setup area
    public void setupLobby(int fieldSize, String type) {
        transitionLobbyStateTo(LobbyState.OPEN);
        transitionGameStateTo(GameState.WAITING);
        initBoard(fieldSize, type);
        LogUtil.debug("Lobby is set to: OPEN");
    }
    //Initialize and setup area
    private void initBoard(int fieldSize, String type) {
        materials = Blocks.getMaterialList(type);
        board = new BoardBuilder().setCorners().setFieldSize(fieldSize).setMaterials(materials).build();
        board.fillAreasWithMaterials();
    }
    //Prepare to start game
    public void setupGame() {
        transitionLobbyStateTo(LobbyState.CLOSED);
        transitionGameStateTo(GameState.COUNTDOWN);
        runBoardAnimation();
    }
    //Run BoardAnimation
    private void runBoardAnimation() {
        boardAnimation.start(board).then(() -> {
            runGame();
            LogUtil.debug("BoardAnimation completed! Running game...");
        });
    }

    //Run Game
    private void runGame() {
        transitionGameStateTo(GameState.RUNNING);
        Bukkit.getScheduler().runTaskTimer(Twister.get(), () -> {

        }, 0, delay);
    }

    //Players Handler
    public void addPlayer(Player p) {
        players.add(p);
    }
    public void removePlayer(Player p) {
        players.remove(p);
    }

    //Supporting code methods
    Set<Player> getPlayersList() {
        return this.players;
    }

    public int getCurrentRound() {
        return round;
    }

    //PlayerState methods
//    public void transitionPlayerStateTo(PlayerState newState) {
//        this.currentPlayerState = newState;
//    }
//    public PlayerState getCurrentPlayerState() {
//        return this.currentPlayerState;
//    }
    //LobbyState methods
    public void transitionLobbyStateTo(LobbyState newState) {
        this.currentLobbyState = newState;
        switch(newState) {
            case OPEN -> lobbyJoiningPhase = true;
            case CLOSED -> lobbyJoiningPhase = false;
        }
    }
    public LobbyState getCurrentLobbyState() {
        return this.currentLobbyState;
    }
    //GameState methods
    public void transitionGameStateTo(GameState newState) {
        this.currentGameState = newState;
        Bukkit.getServer().getPluginManager().callEvent(new GameStateChangeEvent(newState));
    }
    public GameState getCurrentGameState() {
        return this.currentGameState;
    }
}