package org.minerail.twister.game.animation;

import org.minerail.twister.game.board.Board;

public class BoardAnimation implements Animation<Board> {
    private Runnable callback;
    private Board board;
    private boolean running = false;

    @Override
    public Animation start(Board param) {

        return this;
    }

    @Override
    public void stop() {
        running = false;
        if (callback != null) callback.run();
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public Animation then(Runnable nextAction) {
        this.callback = nextAction;
        return this;
    }
}
