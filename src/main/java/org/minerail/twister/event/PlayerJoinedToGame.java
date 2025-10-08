package org.minerail.twister.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinedToGame extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private Player player;

    public PlayerJoinedToGame(Player p) {
        this.player = p;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
