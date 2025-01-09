package org.example.application.game.entity;

import java.util.ArrayList;
import java.util.UUID;

public class Package {
    private UUID id;  // Eindeutige ID des Pakets
    private ArrayList<Card> cards;  // Die Karten, die zu diesem Paket gehören

    // Konstruktor
    public Package(UUID id ,ArrayList<Card> cards) {
        this.cards = cards;
        this.id = id;
    }

    // Getter und Setter
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }
}
