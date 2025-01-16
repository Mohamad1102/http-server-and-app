package org.example.application.game.entity;

public class UserStats {
    private int numberOfBattles;
    private int wins;
    private int losses;

    public UserStats(int numberOfBattles, int wins, int losses) {
        this.numberOfBattles = numberOfBattles;
        this.wins = wins;
        this.losses = losses;
    }

    public int getNumberOfBattles() {
        return numberOfBattles;
    }

    public void setNumberOfBattles(int numberOfBattles) {
        this.numberOfBattles = numberOfBattles;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }
}