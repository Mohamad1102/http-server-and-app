package org.example.application.game.entity;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;

    private String username;

    private String password;
    private int coins = 20;
    private List<Card> stack = new ArrayList<>();

    private List<Card> deck = new ArrayList<>();

    public User(){}
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public List<Card> getStack() {
        return stack;
    }

    public void setStack(List<Card> stack) {
        this.stack = stack;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    // Methode zum Handhaben des Kaufens von Kartenpaketen
    public void buyCardPackage(List<Card> packageCards) {
        if (coins < 5) {
            throw new IllegalStateException("Not enough coins!");
        }
        coins -= 5;
        stack.addAll(packageCards);
    }

}
