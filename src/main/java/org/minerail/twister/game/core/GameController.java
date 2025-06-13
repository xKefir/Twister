package org.minerail.twister.game.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.minerail.twister.Twister;
import org.minerail.twister.event.GameStateChangeEvent;
import org.minerail.twister.file.config.ConfigFile;
import org.minerail.twister.file.config.ConfigKey;
import org.minerail.twister.util.LogUtil;

import java.util.HashSet;
import java.util.Set;

public class GameController {
    private ConfigFile configFile = Twister.getConfigFile();

    //Variables
    protected final int MIN_PLAYERS = configFile.getInt(ConfigKey.SETTINGS_GAME_MIN_PLAYERS);
    protected final long COUNTDOWN = 5000; //Before starting game
    protected final long CLEAR_AREA_DELAY = 2000;
    public final long AREA_ANIM_TIME = 1000;
    public final double MATERIAL_SELECTION_DELAY = 0.20;
    protected volatile long ROUND_DURATION_TIME = configFile.getInt(ConfigKey.SETTINGS_GAME_SECONDS_DURATION_ROUND) * 1000L;
    public final double DECREMENT_PER_ROUND = configFile.getInt(ConfigKey.SETTINGS_GAME_SUBTRACTED_TIME_MULTIPLIER);
    protected final long MIN_ROUND_TIME = 1L;
    protected final long GAME_END_DELAY = 2000;
    public volatile long currentTime;

    //States
    private volatile GameController.GameState currentGameState = GameController.GameState.FINISHED;
    private volatile GameController.LobbyState currentLobbyState = GameController.LobbyState.CLOSED;

    //Game information
    private final Set<Player> players = new HashSet<>();

    //Init
    private Round roundHandler = new Round(this);
    private Game game = new Game(this, roundHandler);

    //Flags
    public enum LobbyState {
        OPEN,
        CLOSED;
    }

    private volatile boolean lobbyJoiningPhase = false;

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
    public GameController() {}

    //Open Lobby and setup area
    public void setupLobby(int fieldSize, String type) {
        transitionLobbyStateTo(GameController.LobbyState.OPEN);
        transitionGameStateTo(GameController.GameState.WAITING);
        game.initBoard(fieldSize, type);
        LogUtil.debug("Lobby is set to: OPEN");
    }

    //Prepare to start game
    public boolean prepareToStartGame() {
        transitionLobbyStateTo(GameController.LobbyState.CLOSED);
        LogUtil.debug("Lobby is set to: CLOSED");
        transitionGameStateTo(GameController.GameState.COUNTDOWN);
        if (players.size() >= MIN_PLAYERS) return false;
        game.runCountdown();
        return true;
    }

    //Round handler
    public Round getRoundHandler() {
        return roundHandler;
    }
    //Game instance getter
    public Game getGameInstance() {
        return this.game;
    }

    //LobbyState methods
    public void transitionLobbyStateTo(GameController.LobbyState newState) {
        this.currentLobbyState = newState;
        switch(newState) {
            case OPEN -> lobbyJoiningPhase = true;
            case CLOSED -> lobbyJoiningPhase = false;
        }
    }

    public GameController.LobbyState getCurrentLobbyState() {
        return this.currentLobbyState;
    }

    //GameState methods
    public void transitionGameStateTo(GameController.GameState newState) {
        this.currentGameState = newState;
        Bukkit.getServer().getPluginManager().callEvent(new GameStateChangeEvent(newState));
    }

    public GameController.GameState getCurrentGameState() {
        return this.currentGameState;
    }

    //Players Handler
    public void addPlayer(Player p) {
        players.add(p);
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    //Supporting code methods
    public Set<Player> getPlayersList() {
        return this.players;
    }


}
