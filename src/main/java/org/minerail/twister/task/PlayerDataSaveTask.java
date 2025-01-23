package org.minerail.twister.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.minerail.twister.Twister;
import org.minerail.twister.file.playerdata.PlayerData;

public class PlayerDataSaveTask extends BukkitRunnable {
    @Override
    public void run() {
        PlayerData.saveAll();
    }

    public void start(long interval) {
        this.runTaskTimerAsynchronously(Twister.get(), 0L, interval);

    }
}
