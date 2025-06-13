package org.minerail.twister.game.animation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.minerail.twister.Twister;
import org.minerail.twister.game.core.GameController;

public class HotbarAnimation implements Animation<Long> {
    private GameController controller = Twister.getGameController();
    private Runnable nextction;
    private BukkitTask animTask;
    private long animStartTime;
    long currentGoal;
    long amountToAdd;

    @Override
    public Animation start(Long param) {
        animStartTime = controller.currentTime;
        currentGoal = (long) (param * controller.MATERIAL_SELECTION_DELAY * 1);
        amountToAdd = currentGoal;
        animTask = Bukkit.getScheduler().runTaskTimer(Twister.get(), () -> {
            long elapsedTime = controller.currentTime - animStartTime;
            if (elapsedTime >= param) stop();
            if (elapsedTime >= currentGoal) {
                ItemStack item = new ItemStack(controller.getGameInstance().board.getRandomMaterial());
                for (Player p : controller.getPlayersList()) {
                    for (int i = 0; i < 9; i++) {
                        p.getInventory().setItem(i, item);
                    }
                }
                this.currentGoal += amountToAdd;
            }
        }, 0, 1L);
        return this;
    }

    @Override
    public void stop() {
        animTask.cancel();
        animTask = null;
        animStartTime = 0;
    }

    @Override
    public boolean isRunning() {
        return animTask != null;
    }

    @Override
    public Animation then(Runnable nextAction) {
        this.nextction = nextAction;
        return this;
    }
}
