package org.minerail.twister.game.animation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.minerail.twister.Twister;
import org.minerail.twister.game.core.GameController;
import org.minerail.twister.util.LogUtil;

public class HotbarAnimation implements Animation<Long> {
    private GameController controller = Twister.get().getGameController();
    private Runnable nextAction;
    private BukkitTask animTask;
    private long localTickCounter;
    private long currentGoalTick;
    private long ticksToAdd;
    public boolean animationRunning = false;

    @Override
    public Animation start(Long durationTicks) {
        animationRunning = true;
        localTickCounter = 0;

        ticksToAdd = (long) (durationTicks * controller.MATERIAL_SELECTION_DELAY);
        currentGoalTick = ticksToAdd;

        LogUtil.debug("HotbarAnimation started, duration: " + durationTicks + " ticks, " +
                "change interval: " + ticksToAdd + " ticks");

        animTask = Bukkit.getScheduler().runTaskTimer(Twister.get(), () -> {
            localTickCounter++;

            if (localTickCounter >= durationTicks) {
                LogUtil.debug("HotbarAnimation completed after " + localTickCounter + " ticks");
                stop();
                animationRunning = false;
                if (nextAction != null) {
                    LogUtil.debug("Calling HotbarAnimation callback");
                    nextAction.run();
                } else {
                    LogUtil.debug("WARNING: HotbarAnimation nextAction is NULL!");
                }
                return;
            }

            if (localTickCounter >= currentGoalTick) {
                ItemStack item = new ItemStack(
                        controller.getGameInstance().board.getRandomMaterial()
                );

                for (Player p : controller.getPlayerHandler().getPlayersList()) {
                    for (int i = 0; i < 9; i++) {
                        p.getInventory().setItem(i, item);
                    }
                }

                currentGoalTick += ticksToAdd;
                LogUtil.debug("HotbarAnimation changed item at tick " + localTickCounter);
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