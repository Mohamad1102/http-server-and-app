package org.example.application.game.repository;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.User;
import org.example.application.game.data.ConnectionPool;
import org.example.application.game.exception.BadRequestException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BattleRepository {
    private final ConnectionPool connectionPool;

    public BattleRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public int getEloByUserId(UUID userId) {
        String query = "SELECT EloRating FROM Elo WHERE userID = ?";

        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setObject(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("EloRating");
                } else {
                    return 100; // Standard-Elo-Wert
                }
            }
        } catch (org.example.application.game.exception.SQLException | java.sql.SQLException e) {
            throw new RuntimeException("Error fetching ELO rating for user", e);
        }
    }

    public void updateElo(UUID userId, int newElo) {
        String queryExists = "SELECT COUNT(*) FROM Elo WHERE userID = ?";
        String queryUpdate = "UPDATE Elo SET EloRating = ? WHERE userID = ?";
        String queryInsert = "INSERT INTO Elo (userID, EloRating) VALUES (?, ?)";

        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement stmtExists = conn.prepareStatement(queryExists);
                PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdate);
                PreparedStatement stmtInsert = conn.prepareStatement(queryInsert)
        ) {
            stmtExists.setObject(1, userId);

            try (ResultSet rs = stmtExists.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Benutzer existiert in der Tabelle -> Update
                    stmtUpdate.setInt(1, newElo);
                    stmtUpdate.setObject(2, userId);
                    stmtUpdate.executeUpdate();
                } else {
                    // Benutzer existiert nicht in der Tabelle -> Insert
                    stmtInsert.setObject(1, userId);
                    stmtInsert.setInt(2, newElo);
                    stmtInsert.executeUpdate();
                }
            }
        } catch (org.example.application.game.exception.SQLException | java.sql.SQLException e) {
            throw new RuntimeException("Error updating ELO rating for user", e);
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
    // Überträgt alle Karten des Verlierers auf den Gewinner und entfernt sie aus dem Deck

    public void updateStats(UUID userId, boolean isWin) {
        String queryExists = "SELECT COUNT(*) FROM stats WHERE userid = ?";
        String queryUpdateWin = "UPDATE stats SET wins = wins + 1, numberofbattles = numberofbattles + 1 WHERE userid = ?";
        String queryUpdateLoss = "UPDATE stats SET losses = losses + 1, numberofbattles = numberofbattles + 1 WHERE userid = ?";
        String queryInsertWin = "INSERT INTO stats (userid, numberofbattles, wins, losses) VALUES (?, 1, 1, 0)";
        String queryInsertLoss = "INSERT INTO stats (userid, numberofbattles, wins, losses) VALUES (?, 1, 0, 1)";

        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement stmtExists = conn.prepareStatement(queryExists);
                PreparedStatement stmtUpdate = conn.prepareStatement(isWin ? queryUpdateWin : queryUpdateLoss);
                PreparedStatement stmtInsert = conn.prepareStatement(isWin ? queryInsertWin : queryInsertLoss)
        ) {
            stmtExists.setObject(1, userId);

            try (ResultSet rs = stmtExists.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Benutzer existiert in der Tabelle -> Update
                    stmtUpdate.setObject(1, userId);
                    stmtUpdate.executeUpdate();
                } else {
                    // Benutzer existiert nicht in der Tabelle -> Insert
                    stmtInsert.setObject(1, userId);
                    stmtInsert.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating stats for user", e);
        }
    }

    public void resetDeckCardsForLoser(UUID loserId) {
        // SQL-Abfrage, um alle Karten des Decks des Verlierers auf NULL zu setzen
        String sqlUpdateDeck = "UPDATE decks " +
                "SET card1 = NULL, card2 = NULL, card3 = NULL, card4 = NULL " +
                "WHERE user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sqlUpdateDeck)) {

            // Setze die ID des Verlierers in das PreparedStatement ein
            stmt.setObject(1, loserId);

            // Führe das Update aus
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("Kein Deck für den angegebenen Verlierer gefunden.");
            } else {
                System.out.println("Deck des Verlierers wurde erfolgreich zurückgesetzt.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Zurücksetzen der Karten im Deck des Verlierers.", e);
        }
    }


}
