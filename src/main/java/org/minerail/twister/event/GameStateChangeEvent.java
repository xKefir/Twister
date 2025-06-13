package org.minerail.twister.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.minerail.twister.game.core.GameController;

public class GameStateChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private GameController.GameState state;

    public GameStateChangeEvent(GameController.GameState state) {
        this.state = state;
    }
    public GameController.GameState getState() {
        return this.state;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
