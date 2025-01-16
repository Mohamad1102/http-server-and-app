package org.example.application.game.entity;

import java.util.UUID;
public class TradingDeal {
    private UUID id;
    private UUID cardToTrade;
    private String type;
    private double minimumDamage;
    private UUID userId;

    // Standardkonstruktor
    public TradingDeal() {
    }

    // Parameterisierter Konstruktor
    public TradingDeal(UUID id, UUID cardToTrade, String tradeType, double minimumDamage, UUID userId) {
        this.id = id;
        this.cardToTrade = cardToTrade;
        this.type = tradeType;
        this.minimumDamage = minimumDamage;
        this.userId = userId;
    }

    // Getter und Setter für alle Felder
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCardToTrade() {
        return cardToTrade;
    }

    public void setCardToTrade(UUID cardToTrade) {
        this.cardToTrade = cardToTrade;
    }

    public String getType() {
        return type;
    }

    public void setTradeType(String tradeType) {
        this.type = tradeType;
    }

    public double getMinimumDamage() {
        return minimumDamage;
    }

    public void setMinimumDamage(double minimumDamage) {
        this.minimumDamage = minimumDamage;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
