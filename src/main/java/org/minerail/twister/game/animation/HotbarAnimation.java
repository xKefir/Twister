package org.minerail.twister.game.animation;

public class HotbarAnimation implements Animation{
    private Runnable nextction;
    @Override
    public Animation start(Object param) {

        return this;
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public Animation then(Runnable nextAction) {
        this.nextction = nextAction;
        return this;
    }
}
