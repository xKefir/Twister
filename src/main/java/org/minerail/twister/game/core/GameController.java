package org.minerail.twister.game.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.minerail.twister.Twister;
import org.minerail.twister.event.GameStateChangeEvent;
import org.minerail.twister.file.config.ConfigFile;
import org.minerail.twister.file.config.ConfigKey;
import org.minerail.twister.util.LocationUtil;
import org.minerail.twister.util.LogUtil;
import org.minerail.twister.util.TickConverter;

public class GameController {
    private ConfigFile configFile = Twister.get().getConfigFile();

    //Variables
    protected int MIN_PLAYERS = configFile.getInt(ConfigKey.SETTINGS_GAME_MIN_PLAYERS);
    protected final long COUNTDOWN = TickConverter.msToTicks(5000); // 100 ticków (5 sekund)
    protected final long CLEAR_AREA_DELAY = TickConverter.msToTicks(2000); // 40 ticków (2 sekundy)
    public final long AREA_ANIM_TIME = TickConverter.msToTicks(1000); // 20 ticków (1 sekunda)
    public final double MATERIAL_SELECTION_DELAY = 0.20; // 20% czasu rundy
    protected volatile long ROUND_DURATION_TIME = TickConverter.msToTicks(configFile.getInt(ConfigKey.SETTINGS_GAME_SECONDS_DURATION_ROUND) * 1000L);
    public final double DECREMENT_PER_ROUND = configFile.getDouble(ConfigKey.SETTINGS_GAME_SUBTRACTED_TIME_MULTIPLIER);
    protected final long MIN_ROUND_TIME = TickConverter.msToTicks(2000); // Minimum 2 sekundy (40 ticków)
    //protected final long GAME_END_DELAY = TickConverter.msToTicks(2000); // 40 ticków (2 sekundy)

    // TICK COUNTER zamiast currentTime
    public volatile long currentTick = 0;

    //States
    private volatile GameState currentGameState = GameState.FINISHED;
    private volatile LobbyState currentLobbyState = LobbyState.CLOSED;

    //Init
    private Round roundHandler;
    private PlayerHandler playerHandler;
    private Game game;

    //Locations
    private Location loseLocation;
    private Location tpLocation;

    //Enums
    public enum LobbyState {
        NONE,
        OPEN,
        CLOSED;
    }

    private volatile boolean lobbyJoiningPhase = false;

    public enum GameState {
        WAITING,
        BEFORE_ROUND,
        COUNTDOWN,
        ROUND_RUNNING,
        ROUND_END,
        ELIMINATION_PHASE, //TODO
        FINISHED;
    }

    public GameController() {
    }

    public void afterRunServer() {
        transitionLobbyStateTo(LobbyState.NONE);
        transitionGameStateTo(GameState.FINISHED);
        init();
        createLocations();
        LogUtil.debug("GameController initialized after server start");
    }

    //Open Lobby and setup area
    public void setupLobby(int fieldSize, String type) {
        transitionLobbyStateTo(LobbyState.OPEN);
        transitionGameStateTo(GameState.WAITING);
        currentTick = 0;
        init();
        game.initBoard(fieldSize, type);
        createLocations();
        LogUtil.debug("Lobby is set to: OPEN, field size: " + fieldSize + ", type: " + type);
    }

    private void init() {
        roundHandler = new Round(this);
        playerHandler = new PlayerHandler(this);
        game = new Game(this, roundHandler, playerHandler);
        LogUtil.debug("Game components initialized");
    }

    public void stopLobby() {
        transitionLobbyStateTo(LobbyState.NONE);
        transitionGameStateTo(GameState.FINISHED);
        playerHandler.removeAllPlayers();
        getGameInstance().cleanup();
        currentTick = 0;
        LogUtil.debug("Lobby stopped");
    }

    //Prepare to start game
    public boolean prepareToStartGame() {
        if (playerHandler.getPlayersList().size() < MIN_PLAYERS) {
            LogUtil.debug("Cannot start game: not enough players (" +
                    playerHandler.getPlayersList().size() + "/" + MIN_PLAYERS + ")");
            return false;
        }

        currentTick = 0;
        game.runCountdown();
        transitionLobbyStateTo(LobbyState.CLOSED);
        LogUtil.debug("Lobby is set to: CLOSED");
        transitionGameStateTo(GameState.COUNTDOWN);
        LogUtil.debug("Game countdown started with " + playerHandler.getPlayersList().size() + " players");
        return true;
    }

    //Round handler
    public Round getRoundHandler() {
        return roundHandler;
    }

    //Player Handler
    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    //Game instance getter
    public Game getGameInstance() {
        return this.game;
    }

    //LobbyState methods
    public synchronized void transitionLobbyStateTo(LobbyState newState) {
        LobbyState oldState = this.currentLobbyState;
        this.currentLobbyState = newState;

        switch (newState) {
            case OPEN -> lobbyJoiningPhase = true;
            case CLOSED, NONE -> lobbyJoiningPhase = false;
        }

        LogUtil.debug("Lobby state transition: " + oldState + " -> " + newState);
    }

    public LobbyState getCurrentLobbyState() {
        return this.currentLobbyState;
    }

    public boolean isLobbyJoiningPhase() {
        return this.lobbyJoiningPhase;
    }

    //GameState methods
    public synchronized void transitionGameStateTo(GameState newState) {
        GameState oldState = this.currentGameState;
        this.currentGameState = newState;

        Bukkit.getServer().getPluginManager().callEvent(new GameStateChangeEvent(newState));
        LogUtil.debug("Game state transition: " + oldState + " -> " + newState + " (tick: " + currentTick + ")");
    }

    public GameState getCurrentGameState() {
        return this.currentGameState;
    }

    //Location methods
    private void createLocations() {
        loseLocation = LocationUtil.createLocation(
                ConfigKey.ARENA_LOSE_POS_X,
                ConfigKey.ARENA_LOSE_POS_Y,
                ConfigKey.ARENA_LOSE_POS_Z
        );
        tpLocation = LocationUtil.createLocation(
                ConfigKey.ARENA_TP_POS_X,
                ConfigKey.ARENA_TP_POS_Y,
                ConfigKey.ARENA_TP_POS_Z
        );
        LogUtil.debug("Locations created - Lose: " + loseLocation + ", TP: " + tpLocation);
    }

    public Location getLoseLocation() {
        return loseLocation;
    }

    public Location getTpLocation() {
        return tpLocation;
    }
}