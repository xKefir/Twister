package org.minerail.twister.game.animation;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.minerail.twister.Twister;
import org.minerail.twister.game.board.Board;
import org.minerail.twister.game.core.GameController;

public class BoardAnimation implements Animation<Board> {
    private GameController controller = Twister.getGameController();
    private Runnable nextction;
    private BukkitTask animTask;
    private long animStartTime;
    long currentGoal;
    long amountToAdd;

    @Override
    public Animation start(Board param) {
        animStartTime = controller.currentTime;
        currentGoal = (long) (controller.AREA_ANIM_TIME * 0.20 * 1);
        amountToAdd = currentGoal;
        animTask = Bukkit.getScheduler().runTaskTimer(Twister.get(), () -> {
            long elapsedTime = controller.currentTime - animStartTime;
            if (elapsedTime >= controller.AREA_ANIM_TIME) stop();
            if (elapsedTime >= currentGoal) {
                param.fillAreasWithMaterials();
                currentGoal += amountToAdd;
            }
        },0,1L);
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
