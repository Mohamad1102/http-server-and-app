package org.example.application.game.repository;

import org.example.application.game.data.ConnectionPool;
import org.example.application.game.entity.UserElo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserEloRepository {

    private final ConnectionPool connectionPool;

    public UserEloRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public UserElo findEloByUserId(UUID userId) {
        String query = "SELECT EloRating FROM Elo WHERE userID = ? ORDER BY";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int eloRating = rs.getInt("EloRating");
                    return new UserElo(userId, eloRating);
                } else {
                    throw new IllegalArgumentException("ELO data not found for user ID: " + userId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching ELO data from database", e);
        }
    }

    public void save(UserElo userElo) {
        String query = "INSERT INTO Elo (userID, EloRating) VALUES (?, ?)";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, userElo.getUserId());
            stmt.setInt(2, userElo.getEloRating());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving ELO data to database", e);
        }
    }

    public void updateEloRating(UUID userId, int newEloRating) {
        String query = "UPDATE Elo SET EloRating = ? WHERE userID = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, newEloRating);
            stmt.setObject(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new IllegalArgumentException("Failed to update ELO. User ID not found: " + userId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating ELO data in database", e);
        }
    }
}
