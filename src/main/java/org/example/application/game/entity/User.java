package org.example.application.game.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private UUID id;

    private String username;

    private String password;

    private int coins;

    private ArrayList<Card> cards;  // Liste der Karten des Benutzers

    private String name;
    private String bio;

    private String image;

    public User() {
        this.cards = new ArrayList<>();  // Initialisieren der Kartenliste
    }

    public User(UUID id, String username, String password, int coins, String name, String bio, String image) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.coins = coins;
        this.cards = new ArrayList<>();  // Initialisieren der Kartenliste
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public void addCoins(int amount) {
        this.coins += amount;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // Methode zum Abziehen von Coins
    public void deductCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
        } else {
            throw new IllegalArgumentException("Not enough coins!");
        }
    }

    // Getter und Setter für die Kartenliste
    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    // Methode, um eine Karte hinzuzufügen
    public void addCard(Card card) {
        this.cards.add(card);
    }

}
