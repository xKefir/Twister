package org.minerail.twister.game.animation;

public interface Animation<T> {
    Animation start(T param);
    void stop();
    boolean isRunning();
    Animation then(Runnable nextAction);
}
