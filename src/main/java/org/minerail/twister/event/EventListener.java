package org.minerail.twister.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.minerail.twister.file.PlayerData;

public class EventListener implements Listener {
    public EventListener() {

    }
    @EventHandler
    public void onPlayerLose(PlayerLoseGame e) {
        if (!e.isCancelled()) {
            PlayerData.get(e.getPlayer()).setLoses(1);

        }
    }
    @EventHandler
    public void onPlayerQuitGame(PlayerQuitGame e) {

    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {

    }
    @EventHandler
    public void onPlayerWin(PlayerWinGame e) {

    }
}
