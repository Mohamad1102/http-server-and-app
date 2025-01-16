package org.example.application.game.repository;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.User;
import org.example.application.game.data.ConnectionPool;
import org.example.application.game.exception.BadRequestException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BattleRepository {
    private final ConnectionPool connectionPool;

    public BattleRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void updateStats(String username, boolean isWinner, String battleResult) {
        String query = "UPDATE stats SET numberOfBattles = numberOfBattles + 1, " +
                "wins = wins + ?, losses = losses + ? WHERE userid = (SELECT id FROM users WHERE username = ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, isWinner ? 1 : 0);  // Gewinne erhöhen
            stmt.setInt(2, isWinner ? 0 : 1);  // Verluste erhöhen
            stmt.setString(3, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateElo(String username, int eloChange) {
        String query = "UPDATE Elo SET EloRating = EloRating + ? WHERE userID = (SELECT id FROM users WHERE username = ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, eloChange);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Card> getDeckForUser(UUID userId) {
        // SQL-Abfrage, um die Karten eines Decks mit ihren Eigenschaften abzurufen
        String sqlGetDeck = "SELECT " +
                "c1.name AS card1_name, c1.damage AS card1_damage, c1.card_type AS card1_card_type, " +
                "c2.name AS card2_name, c2.damage AS card2_damage, c2.card_type AS card2_card_type, " +
                "c3.name AS card3_name, c3.damage AS card3_damage, c3.card_type AS card3_card_type, " +
                "c4.name AS card4_name, c4.damage AS card4_damage, c4.card_type AS card4_card_type " +
                "FROM decks d " +
                "JOIN cards c1 ON d.card1 = c1.id " +
                "JOIN cards c2 ON d.card2 = c2.id " +
                "JOIN cards c3 ON d.card3 = c3.id " +
                "JOIN cards c4 ON d.card4 = c4.id " +
                "WHERE d.user_id = ?";

        List<Card> deckCards = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement sqlGetDeckStatement = connection.prepareStatement(sqlGetDeck)) {

            // Benutzer-ID in das PreparedStatement einsetzen
            sqlGetDeckStatement.setObject(1, userId);

            ResultSet rs = sqlGetDeckStatement.executeQuery();

            if (rs.next()) {
                // Kartenobjekte erstellen und zur Liste hinzufügen
                deckCards.add(new Card(rs.getString("card1_name"), rs.getDouble("card1_damage"), rs.getString("card1_card_type")));
                deckCards.add(new Card(rs.getString("card2_name"), rs.getDouble("card2_damage"), rs.getString("card2_card_type")));
                deckCards.add(new Card(rs.getString("card3_name"), rs.getDouble("card3_damage"), rs.getString("card3_card_type")));
                deckCards.add(new Card(rs.getString("card4_name"), rs.getDouble("card4_damage"), rs.getString("card4_card_type")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new BadRequestException("Error retrieving deck: " + e.getMessage());
        }

        return deckCards;
    }

}
