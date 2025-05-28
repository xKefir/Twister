package org.minerail.twister.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.minerail.twister.game.core.Game;

public class GameStateChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private Game.GameState state;


    public GameStateChangeEvent(Game.GameState state) {
        this.state = state;
    }
    public Game.GameState getState() {
        return this.state;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
