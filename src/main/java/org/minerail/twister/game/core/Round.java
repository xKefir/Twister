package org.minerail.twister.game.core;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.minerail.twister.Twister;
import org.minerail.twister.file.config.ConfigFile;

public class Round {
    private ConfigFile configFile = Twister.getConfigFile();
    private GameController controller;

    //Round variables
    protected volatile int currentRound = 0;
    protected long roundStartTime;
    protected volatile long elapsedRoundTime;
    private BukkitTask roundTask;

    public Round(GameController controller) {
        this.controller = controller;
    }

    //Round logic
    protected void startNewRound() {
        currentRound++;
        roundStartTime = controller.currentTime;
        elapsedRoundTime = 0;
        controller.transitionGameStateTo(GameController.GameState.ROUND_RUNNING);
        controller.getGameInstance().runHotbarAnimation();
        roundTask = Bukkit.getScheduler().runTaskTimer(Twister.get(), () -> {
            elapsedRoundTime = controller.currentTime - roundStartTime;
            if (elapsedRoundTime >= getRoundDurationTime()) endCurrentRound();
        }, 0, 1L);
    }

    protected void endCurrentRound() {
        roundTask.cancel();
        calculateRoundDuration();
        controller.transitionGameStateTo(GameController.GameState.ROUND_END);
    }

    protected void clear() {
        roundTask = null;
        currentRound = 0;
        roundStartTime = 0;
        elapsedRoundTime = 0;
    }

    private void calculateRoundDuration() {
        long baseDuration = getRoundDurationTime();
        double totalDecrement = 1.0 - (getDecrementPerRound() * (currentRound - 1));
        controller.ROUND_DURATION_TIME = Math.max((long)(baseDuration * totalDecrement), getMinRoundTime());
    }

    //Supporting methods
    protected int getCurrentRound() {
        return currentRound;
    }

    protected long getMinRoundTime() {
        return controller.MIN_ROUND_TIME;
    }
    protected long getRoundDurationTime() {
        return controller.ROUND_DURATION_TIME;
    }
    protected double getDecrementPerRound() {
        return controller.DECREMENT_PER_ROUND;
    }
}
