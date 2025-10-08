package org.minerail.twister.game.animation;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.minerail.twister.Twister;
import org.minerail.twister.game.board.Board;
import org.minerail.twister.game.core.GameController;
import org.minerail.twister.util.LogUtil;

public class BoardAnimation implements Animation<Board> {
    private GameController controller = Twister.get().getGameController();
    private Runnable nextAction;
    private BukkitTask animTask;
    private long localTickCounter;
    private long currentGoalTick;
    private long ticksToAdd;
    public boolean animationRunning = false;

    @Override
    public Animation start(Board param) {
        animationRunning = true;
        localTickCounter = 0;

        ticksToAdd = (long) (controller.AREA_ANIM_TIME * 0.20);
        currentGoalTick = ticksToAdd;

        LogUtil.debug("BoardAnimation started, duration: " + controller.AREA_ANIM_TIME + " ticks, " +
                "step interval: " + ticksToAdd + " ticks");

        animTask = Bukkit.getScheduler().runTaskTimer(Twister.get(), () -> {
            localTickCounter++;

            // Zakończ animację
            if (localTickCounter >= controller.AREA_ANIM_TIME) {
                LogUtil.debug("BoardAnimation completed after " + localTickCounter + " ticks");
                stop();
                animationRunning = false;
                if (nextAction != null) {
                    LogUtil.debug("Calling BoardAnimation callback");
                    nextAction.run();
                } else {
                    LogUtil.debug("WARNING: BoardAnimation nextAction is NULL!");
                }
                return;
            }

            // Wykonaj krok animacji
            if (localTickCounter >= currentGoalTick) {
                param.fillAreasWithMaterials();
                currentGoalTick += ticksToAdd;
                LogUtil.debug("BoardAnimation step at tick " + localTickCounter);
            }
        }, 0, 1L);

        return this;
    }

    @Override
    public void stop() {
        if (animTask != null && !animTask.isCancelled()) {
            animTask.cancel();
        }
        animationRunning = false;
        localTickCounter = 0;
        currentGoalTick = 0;
    }

    @Override
    public boolean isRunning() {
        return animationRunning;
    }

    @Override
    public Animation then(Runnable nextAction) {
        this.nextAction = nextAction;
        return this;
    }
}