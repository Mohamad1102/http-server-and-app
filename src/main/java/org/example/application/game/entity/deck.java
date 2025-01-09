package org.example.application.game.entity;

import java.util.UUID;

public class deck {

    private UUID id;
    private UUID user_ID;
    private UUID card1;
    private UUID card2;
    private UUID card3;
    private UUID card4;

    public deck(UUID id, UUID user_ID, UUID card1, UUID card2, UUID card3, UUID card4) {
        this.id = id;
        this.user_ID = user_ID;
        this.card1 = card1;
        this.card2 = card2;
        this.card3 = card3;
        this.card4 = card4;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(UUID user_ID) {
        this.user_ID = user_ID;
    }

    public UUID getCard1() {
        return card1;
    }

    public void setCard1(UUID card1) {
        this.card1 = card1;
    }

    public UUID getCard2() {
        return card2;
    }

    public void setCard2(UUID card2) {
        this.card2 = card2;
    }

    public UUID getCard3() {
        return card3;
    }

    public void setCard3(UUID card3) {
        this.card3 = card3;
    }

    public UUID getCard4() {
        return card4;
    }

    public void setCard4(UUID card4) {
        this.card4 = card4;
    }
}
