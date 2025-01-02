package org.example.application.game.entity;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;

    private String username;

    private String password;

    private int coins;

    public User() {
    }

    public User(String id, String username, String password, int coins) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.coins = coins;
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

    public void addCoins(int amount) {
        this.coins += amount;
    }

    // Methode zum Abziehen von Coins
    public void deductCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
        } else {
            throw new IllegalArgumentException("Not enough coins!");
        }
    }
}
