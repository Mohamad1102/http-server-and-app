package org.example.application.game.repository;

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
            stmt.setString(3, tradingDeal.getTradeType());
            stmt.setDouble(4, tradingDeal.getMinimumDamage());
            stmt.setObject(5, tradingDeal.getUserId());
            stmt.executeUpdate();
        }
    }

    public List<TradingDeal> getAllTradingDeals() throws SQLException {
        List<TradingDeal> tradingDeals = new ArrayList<>();
        String query = "SELECT * FROM trading_deals";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                UUID cardToTrade = (UUID) rs.getObject("card_to_trade");
                String tradeType = rs.getString("trade_type");
                double minimumDamage = rs.getDouble("minimum_damage");
                UUID userId = (UUID) rs.getObject("user_id", UUID.class);

                TradingDeal tradingDeal = new TradingDeal(id, cardToTrade, tradeType, minimumDamage, userId);
                tradingDeals.add(tradingDeal);
            }
        }

        return tradingDeals;
    }

    public void deleteTradingDeal(UUID id) throws SQLException {
        String query = "DELETE FROM trading_deals WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
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
}