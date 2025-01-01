package org.example.application.game.entity;

public class Card {
    private String id;
    private String name;
    private int damage;
    private String elementType;
    private CardType type;

    public enum CardType {
        SPELL, MONSTER
    }

    public Card(String name, int damage, String elementType, CardType type) {
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
        this.type = type;
    }

    // Getter und Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }
}
