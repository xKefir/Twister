package org.minerail.twister.game.animation;

public class CountDownXPBarAnimation implements Animation<Long>{
    @Override
    public Animation start(Long param) {
        return null;
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
        return null;
    }
}
