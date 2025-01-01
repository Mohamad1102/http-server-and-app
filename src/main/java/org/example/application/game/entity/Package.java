package org.example.application.game.entity;

import java.util.List;

public class Package {
    private List<Card> cards;

    // Constructor
    public Package(List<Card> cards) {
        this.cards = cards;
    }

    // Getter und Setter
    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}

