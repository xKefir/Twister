package org.minerail.twister.file.playerdata;

public class PlayerStats {
    private final PlayerData playerData;
    private int wins;
    private int losses;
    private int gamesPlayed;

    public PlayerStats(PlayerData playerData) {
        this.playerData = playerData;
        this.wins = 0;
        this.losses = 0;
        this.gamesPlayed = 0;
    }
    public void writeData(int wins, int losses, int gamesPlayed) {
        this.wins = wins;
        this.losses = losses;
        this.gamesPlayed = gamesPlayed;

    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }
    public void addGamesPlayed() {
        gamesPlayed++;
    }

    public void addWin() {
        wins++;
        gamesPlayed++;
    }

    public void addLoss() {
        losses++;
        gamesPlayed++;
    }
    public void removeLoss() {
        if (losses != 0) {
            losses--;
        }
    }
    public void resetStats() {
        this.wins = 0;
        this.losses = 0;
        this.gamesPlayed = 0;
    }
}
