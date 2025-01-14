package org.example.application.game.repository;

import org.example.application.game.data.ConnectionPool;
import org.example.application.game.entity.CompletedTrade;

import java.sql.*;
import java.util.UUID;

public class CompletedTradeRepository {

    private ConnectionPool connectionPool;

    public CompletedTradeRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void createCompletedTrade(CompletedTrade completedTrade) throws SQLException {
        String query = "INSERT INTO completed_trades (id, trading_deal_id, offering_user_id, accepting_user_id, trade_card, trade_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, completedTrade.getId());
            stmt.setObject(2, completedTrade.getTradingDealId());
            stmt.setObject(3, completedTrade.getOfferingUserId());
            stmt.setObject(4, completedTrade.getAcceptingUserId());
            stmt.setObject(5, completedTrade.getTradeCardId());
            stmt.setString(6, completedTrade.getTradeDate());
            stmt.executeUpdate();
        }
    }
}
