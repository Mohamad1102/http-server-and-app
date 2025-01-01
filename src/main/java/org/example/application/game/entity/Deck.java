package org.example.application.game.entity;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>(4);
    }

    public void addCard(Card card) {
        if (cards.size() < 4) {
            cards.add(card);
        } else {
            System.out.println("The deck is already full. Cannot pick more cards.");
        }
    }

    public Card getCard(int index) {
        if (index >= 0 && index < cards.size()) {
            return cards.get(index);
        } else {
            System.out.println("Invalid index. Please provide a valid index between 0 and " + (cards.size() - 1));
            return null;
        }
    }

    public boolean isFull() {
        return cards.size() == 4;
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public int getDeckSize() {
        return cards.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Deck:\n");
        for (Card card : cards) {
            sb.append(card.getName()).append(" - Damage: ").append(card.getDamage()).append(", Element: ").append(card.getId()).append("\n");
        }
        return sb.toString();
    }
}

