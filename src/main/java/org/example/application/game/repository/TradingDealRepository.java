package org.example.application.game.repository;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.TradingDeal;
import org.example.application.game.data.ConnectionPool;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Repository für Handelsangebote
public class TradingDealRepository {

    private ConnectionPool connectionPool;

    // Konstruktor-Injektion der ConnectionPool
    public TradingDealRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void createTradingDeal(TradingDeal tradingDeal) throws SQLException {
        String query = "INSERT INTO trading_deals (id, card_to_trade, trade_type, minimum_damage, user_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, tradingDeal.getId());
            stmt.setObject(2, tradingDeal.getCardToTrade());
            stmt.setString(3, tradingDeal.getType());
            stmt.setDouble(4, tradingDeal.getMinimumDamage());
            stmt.setObject(5, tradingDeal.getUserId());
            stmt.executeUpdate();
        }
    }

    public List<TradingDeal> getAllTradingDeals() throws SQLException {
        List<TradingDeal> tradingDeals = new ArrayList<>();
        String query = "SELECT * FROM trading_deals WHERE accepted = FALSE";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                UUID cardToTrade = (UUID) rs.getObject("card_to_trade");
                String tradeType = rs.getString("trade_type");
                double minimumDamage = rs.getDouble("minimum_damage");
                UUID userId = rs.getObject("user_id", UUID.class);

                TradingDeal tradingDeal = new TradingDeal(id, cardToTrade, tradeType, minimumDamage, userId);
                tradingDeals.add(tradingDeal);
            }
        }

        return tradingDeals;
    }

    public boolean deleteById(UUID tradeId) throws SQLException {
        String query = "DELETE FROM trading_deals WHERE id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, tradeId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }


    public TradingDeal getTradingDealById(UUID id) throws SQLException {
        String query = "SELECT * FROM trading_deals WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UUID cardToTrade = rs.getObject("card_to_trade", UUID.class);
                    String tradeType = rs.getString("trade_type");
                    double minimumDamage = rs.getDouble("minimum_damage");
                    UUID userId = rs.getObject("user_id", UUID.class);

                    return new TradingDeal(id, cardToTrade, tradeType, minimumDamage, userId);
                }
            }
        }
        return null;
    }

    public void executeTrade(TradingDeal deal, Card userCard) throws SQLException {
        // Query für das Aktualisieren des Kartenbesitzers
        String updateCardOwnerQuery = "UPDATE cards SET user_id = ? WHERE id = ?";
        // Query, um den Handelsdeal als abgeschlossen zu markieren
        String markTradeAsCompletedQuery = "UPDATE trading_deals SET accepted = TRUE WHERE id = ?";
        // Query, um den Besitzer einer Karte zu ermitteln
        String findCardOwnerQuery = "SELECT user_id FROM cards WHERE id = ?";

        try (Connection connection = connectionPool.getConnection()) {
            connection.setAutoCommit(false); // Start der Transaktion

            try (PreparedStatement findOwnerStmt = connection.prepareStatement(findCardOwnerQuery);
                 PreparedStatement updateCardStmt = connection.prepareStatement(updateCardOwnerQuery);
                 PreparedStatement markTradeStmt = connection.prepareStatement(markTradeAsCompletedQuery)) {

                // 1. Besitzer der Handelskarte ermitteln
                UUID tradingCardId = deal.getCardToTrade(); // ID der Handelskarte
                UUID tradingCardNewOwnerId;
                findOwnerStmt.setObject(1, tradingCardId);
                try (ResultSet resultSet = findOwnerStmt.executeQuery()) {
                    if (resultSet.next()) {
                        tradingCardNewOwnerId = resultSet.getObject("user_id", UUID.class);
                    } else {
                        throw new IllegalArgumentException("Owner of the trading card not found in the database.");
                    }
                }

                System.out.println("Verbindung zu Treading Deals Erfolgreich");

                // 2. Besitzer der Benutzerkarte ändern
                UUID userCardNewOwnerId = deal.getUserId(); // Der Benutzer, der die Benutzerkarte erhält
                updateCardStmt.setObject(1, userCardNewOwnerId);
                updateCardStmt.setObject(2, userCard.getId()); // ID der Benutzerkarte
                updateCardStmt.executeUpdate();

                System.out.println("2. Besitzer der Benutzerkarte ändern");

                // 3. Besitzer der Handelskarte ändern
                updateCardStmt.setObject(1, tradingCardNewOwnerId);
                updateCardStmt.setObject(2, tradingCardId);
                updateCardStmt.executeUpdate();

                System.out.println("3. Besitzer der Handelskarte ändern");
                // 4. Handelsdeal als abgeschlossen markieren
                markTradeStmt.setObject(1, deal.getId());
                markTradeStmt.executeUpdate();

                System.out.println("ERFOLGREICH");
                // Transaktion erfolgreich abschließen
                connection.commit();
            } catch (SQLException e) {
                connection.rollback(); // Transaktion bei Fehler zurücksetzen
                throw e;
            }
        }
    }
}