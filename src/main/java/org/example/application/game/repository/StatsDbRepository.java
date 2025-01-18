package org.example.application.game.repository;

import org.example.application.game.data.ConnectionPool;
import org.example.application.game.entity.UserStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class StatsDbRepository {
    private final ConnectionPool connectionPool;

    public StatsDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public UserStats getUserStats(UUID userid) {
        String query = "SELECT numberofbattles, wins, losses FROM stats WHERE userid = ?";
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setObject(1, userid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Statistikdaten aus dem ResultSet extrahieren
                    int battles = rs.getInt("numberofbattles");
                    int wins = rs.getInt("wins");
                    int losses = rs.getInt("losses");

                    // UserStats-Objekt erstellen und zurückgeben
                    return new UserStats(battles, wins, losses);
                } else {
                    // Standard-Statistik zurückgeben, wenn keine Daten vorhanden sind
                    System.out.println("Keine Statistik für Benutzer gefunden.");
                    return new UserStats(0, 0, 0); // Leere Statistik
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user stats from the database", e);
        }
    }
}
