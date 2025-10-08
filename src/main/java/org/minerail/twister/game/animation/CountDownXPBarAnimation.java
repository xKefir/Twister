package org.minerail.twister.game.animation;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.minerail.twister.Twister;
import org.minerail.twister.game.core.GameController;
import org.minerail.twister.util.LogUtil;

public class CountDownXPBarAnimation implements Animation<Long> {
    private GameController controller = Twister.get().getGameController();
    private Runnable nextAction;
    private BukkitTask animTask;
    private long localTickCounter;
    private long durationTicks;
    public boolean animationRunning = false;

    @Override
    public Animation start(Long durationTicks) {
        this.durationTicks = durationTicks;
        animationRunning = true;
        localTickCounter = 0;

        LogUtil.debug("XPBarAnimation started, duration: " + durationTicks + " ticks");


        controller.getPlayerHandler().getPlayersList().forEach(player -> {
            player.setExp(1.0f);
            player.setLevel(0);
        });

        animTask = Bukkit.getScheduler().runTaskTimer(Twister.get(), () -> {
            localTickCounter++;

            if (localTickCounter >= durationTicks) {
                LogUtil.debug("XPBarAnimation completed after " + localTickCounter + " ticks");
                stop();
                animationRunning = false;
                if (nextAction != null) {
                    LogUtil.debug("Calling XPBarAnimation callback");
                    nextAction.run();
                } else {
                    LogUtil.debug("WARNING: XPBarAnimation nextAction is NULL!");
                }
                return;
            }


            double fraction = (double) localTickCounter / durationTicks;
            float progress = (float) (1.0 - fraction);


            controller.getPlayerHandler().getPlayersList().forEach(player -> {
                player.setExp(progress);
            });

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

        controller.getPlayerHandler().getPlayersList().forEach(player -> {
            player.setExp(0.0f);
            player.setLevel(0);
        });
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