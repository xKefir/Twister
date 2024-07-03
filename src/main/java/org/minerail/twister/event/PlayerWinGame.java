package org.minerail.twister.event;

import org.bukkit.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerWinGame extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private Player player;

    public PlayerWinGame(Player player) {
        this.player = player;
    }


    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
