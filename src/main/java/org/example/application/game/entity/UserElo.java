package org.example.application.game.entity;

import java.util.UUID;

public class UserElo {
    private UUID userId;
    private int eloRating;

    public UserElo(UUID userId, int eloRating) {
        this.userId = userId;
        this.eloRating = eloRating;
    }

    public UUID getUserId() {
        return userId;
    }

    public int getEloRating() {
        return eloRating;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setEloRating(int eloRating) {
        this.eloRating = eloRating;
    }
}
