package org.minerail.twister.game.core;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.minerail.twister.Twister;
import org.minerail.twister.file.config.ConfigFile;
import org.minerail.twister.file.config.ConfigKey;
import org.minerail.twister.util.LogUtil;
import org.minerail.twister.util.TickConverter;

public class Round {
    private ConfigFile configFile = Twister.get().getConfigFile();
    private GameController controller;

    //Flags
    private boolean firstRound = true;

    //Round variables
    protected volatile int currentRound = 0;
    protected long roundStartTick;
    protected volatile long elapsedRoundTicks;

    public Round(GameController controller) {
        this.controller = controller;
    }

    //Round logic
    protected void startNewRound() {
        if (firstRound) firstRound = false;
        currentRound++;
        roundStartTick = controller.currentTick;
        elapsedRoundTicks = 0;

        LogUtil.debug("Starting round #" + currentRound +
                " at tick " + controller.currentTick +
                " with duration: " + getRoundDurationTime() + " ticks");

        controller.transitionGameStateTo(GameController.GameState.ROUND_RUNNING);
        controller.getGameInstance().runHotbarAnimation();
        controller.getGameInstance().runXPBarAnimation();

    }

    protected void endCurrentRound() {
        LogUtil.debug("Round #" + currentRound + " ended");
        calculateRoundDuration();

        controller.transitionGameStateTo(GameController.GameState.ROUND_END);
    }

    protected void clear() {
        currentRound = 0;
        roundStartTick = 0;
        elapsedRoundTicks = 0;
        firstRound = true;

        long baseSeconds = configFile.getInt(ConfigKey.SETTINGS_GAME_SECONDS_DURATION_ROUND);
        controller.ROUND_DURATION_TIME = TickConverter.msToTicks(baseSeconds * 1000L);

    }

    private void calculateRoundDuration() {
        long currentDurationTicks = controller.ROUND_DURATION_TIME;

        long reductionTicks = (long)(currentDurationTicks * getDecrementPerRound());
        LogUtil.debug("Reduction amount: " + reductionTicks + " ticks");

        long newDurationTicks = currentDurationTicks - reductionTicks;

        controller.ROUND_DURATION_TIME = Math.max(newDurationTicks, getMinRoundTime());
    }

    //Supporting methods
    protected int getCurrentRound() {
        return currentRound;
    }

    protected long getMinRoundTime() {
        return controller.MIN_ROUND_TIME;
    }

    public long getRoundDurationTime() {
        return controller.ROUND_DURATION_TIME;
    }

    protected double getDecrementPerRound() {
        return controller.DECREMENT_PER_ROUND;
    }

    public boolean isFirstRound() {
        return firstRound;
    }
}