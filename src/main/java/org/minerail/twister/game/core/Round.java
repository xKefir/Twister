package org.minerail.twister.game.core;

public class Round {
    Round(int initialTime, double multiplier) {
        this.roundTime = initialTime * 1000.0;
        this.roundStartTime = System.currentTimeMillis();
        this.decrement = roundTime * (multiplier / 100.0);
        this.currentDecrement = decrement;
        this.remainingPlayers = players.size();
    }
}
