package org.example.application.game.entity;

import java.util.UUID;

public class Card {
    private UUID id;  // UUID statt String
    private String name;
    private double damage;
    private String cardType;  // Verwendet Enum statt boolean

    // Constructor
    public Card(){}
    public Card(String name, double damage, String cardType) {
        this.id = UUID.randomUUID();  // automatisch eine neue UUID erzeugen
        this.name = name;
        this.damage = damage;
        this.cardType = cardType;
    }


    // Getter und Setter
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardType() {
        return cardType;
    }

    // Überschreibe toString() für eine lesbare Ausgabe
    @Override
    public String toString() {
        return "Card{name='" + name + "', damage=" + damage + ", cardType='" + cardType + "'}";
    }
}
