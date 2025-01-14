package org.example.application.game.entity;

import java.util.UUID;

public class CompletedTrade {
    private UUID id;  // Eindeutige ID des abgeschlossenen Handels
    private UUID tradingDealId;  // Die ID des Handelsangebots, das akzeptiert wurde
    private UUID offeringUserId;  // Der Spieler, der das Angebot gemacht hat
    private UUID acceptingUserId;  // Der Spieler, der das Angebot angenommen hat
    private UUID tradeCardId;  // Die ID der Karte, die der akzeptierende Spieler gegeben hat
    private String tradeDate;  // Das Datum des abgeschlossenen Handels

    // Konstruktor für abgeschlossene Trades
    public CompletedTrade(UUID id, UUID tradingDealId, UUID offeringUserId, UUID acceptingUserId, UUID tradeCardId, String tradeDate) {
        this.id = id;
        this.tradingDealId = tradingDealId;
        this.offeringUserId = offeringUserId;
        this.acceptingUserId = acceptingUserId;
        this.tradeCardId = tradeCardId;
        this.tradeDate = tradeDate;
    }

    // Getter und Setter
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTradingDealId() {
        return tradingDealId;
    }

    public void setTradingDealId(UUID tradingDealId) {
        this.tradingDealId = tradingDealId;
    }

    public UUID getOfferingUserId() {
        return offeringUserId;
    }

    public void setOfferingUserId(UUID offeringUserId) {
        this.offeringUserId = offeringUserId;
    }

    public UUID getAcceptingUserId() {
        return acceptingUserId;
    }

    public void setAcceptingUserId(UUID acceptingUserId) {
        this.acceptingUserId = acceptingUserId;
    }

    public UUID getTradeCardId() {
        return tradeCardId;
    }

    public void setTradeCardId(UUID tradeCardId) {
        this.tradeCardId = tradeCardId;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    @Override
    public String toString() {
        return "CompletedTrade{" +
                "id=" + id +
                ", tradingDealId=" + tradingDealId +
                ", offeringUserId=" + offeringUserId +
                ", acceptingUserId=" + acceptingUserId +
                ", tradeCardId=" + tradeCardId +
                ", tradeDate='" + tradeDate + '\'' +
                '}';
    }
}
