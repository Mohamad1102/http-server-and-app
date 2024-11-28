package org.example.application.game.entity;

public class Card {
    private String Name;
    private double Damage;
    private final boolean isSpell;

    // Constructor
    public Card(String Name, double Damage, boolean isSpell){
        this.Name = Name;
        this.Damage = Damage;
        this.isSpell = isSpell;
    }

    // Getters and setters
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public double getDamage() {
        return Damage;
    }

    public void setDamage(double Damage) {
        this.Damage = Damage;
    }

    public boolean isSpell() {
        return isSpell;
    }
}
