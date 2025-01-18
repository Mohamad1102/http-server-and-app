package org.example.application.game.repository;

import org.example.application.game.data.ConnectionPool;
import org.example.application.game.entity.UserElo;
import org.example.application.game.exception.SQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class EloDbRepository {
    private final ConnectionPool connectionPool;
    public EloDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public List<Map<String, Object>> getScoreBord() {
        String query = """
        SELECT u.username, e.EloRating
        FROM Elo e
        INNER JOIN users u ON e.userID = u.id
    """;
        List<Map<String, Object>> eloList = new ArrayList<>();

        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Map<String, Object> eloData = new HashMap<>();
                eloData.put("username", rs.getString("username"));
                eloData.put("eloRating", rs.getInt("EloRating"));

                eloList.add(eloData);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all ELO ratings with usernames from the database", e);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }

        return eloList;
    }

}
