package org.example.application.game.repository;

import org.example.application.game.data.ConnectionPool;
import org.example.application.game.entity.Card;
import org.example.application.game.entity.User;
import org.example.application.game.exception.BadRequestException;

import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.UUID;

public class DeckDbRepository{

    private final ConnectionPool connectionPool;

    public DeckDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public UUID createDeck(ArrayList<UUID> cardsID, User myUser) {
        // SQL-Statement für das Einfügen eines neuen Decks
        String insertDeckSQL = "INSERT INTO decks (id, user_id, card1, card2, card3, card4) VALUES (?, ?, ?, ?, ?, ?)";

        if (cardsID == null || cardsID.size() != 4) {
            throw new IllegalArgumentException("Es müssen genau 4 Karten-IDs angegeben werden.");
        }

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement deckStatement = connection.prepareStatement(insertDeckSQL);
        ) {
            if (connection == null) {
                System.out.println("Verbindung zur Datenbank konnte nicht hergestellt werden.");
                return null;
            }

            System.out.println("Verbindung zur Datenbank erfolgreich.");
            connection.setAutoCommit(false); // Transaktion starten

            // Generiere eine neue Deck-ID
            UUID deckID = UUID.randomUUID();

            // Fülle das PreparedStatement mit Werten
            deckStatement.setObject(1, deckID); // ID des Decks
            deckStatement.setObject(2, myUser.getId()); // ID des Benutzers
            deckStatement.setObject(3, cardsID.get(0)); // Erste Karte
            deckStatement.setObject(4, cardsID.get(1)); // Zweite Karte
            deckStatement.setObject(5, cardsID.get(2)); // Dritte Karte
            deckStatement.setObject(6, cardsID.get(3)); // Vierte Karte

            // Führe die Einfügeoperation aus
            int rowsAffected = deckStatement.executeUpdate();

            if (rowsAffected != 1) {
                throw new SQLException("Fehler beim Einfügen des Decks in die Datenbank.");
            }

            // Transaktion erfolgreich abschließen
            connection.commit();
            System.out.println("Deck wurde erfolgreich erstellt mit ID: " + deckID);
            return deckID;

        } catch (SQLException e) {
            System.out.println("Fehler beim Erstellen des Decks.");
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Speichern des Decks.", e);
        }
    }

    public ArrayList<Card> getDeck(UUID userId) {
        // SQL-Abfrage, die die relevanten Eigenschaften der Karten abruft
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

        ArrayList<Card> deckCards = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement sqlGetDeckStatement = connection.prepareStatement(sqlGetDeck)) {

            // Setze die Benutzer-ID in das PreparedStatement ein
            sqlGetDeckStatement.setObject(1, userId);

            ResultSet rs = sqlGetDeckStatement.executeQuery();

            if (rs.next()) {
                // Erstelle Kartenobjekte für jede Karte und füge sie zur Liste hinzu
                deckCards.add(new Card(rs.getString("card1_name"), rs.getDouble("card1_damage"), rs.getString("card1_card_type")));
                deckCards.add(new Card(rs.getString("card2_name"), rs.getDouble("card2_damage"), rs.getString("card2_card_type")));
                deckCards.add(new Card(rs.getString("card3_name"), rs.getDouble("card3_damage"), rs.getString("card3_card_type")));
                deckCards.add(new Card(rs.getString("card4_name"), rs.getDouble("card4_damage"), rs.getString("card4_card_type")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new BadRequestException("BADREQUEST: " + e.getMessage());
        }

        return deckCards;
    }


    public ArrayList<Card> getDeckPlain(UUID userid) {
        String sqlGetDeck =
                "SELECT " +
                        "  c1.name AS card1_name, c1.damage AS card1_damage, c1.card_type AS card1_type, " +
                        "  c2.name AS card2_name, c2.damage AS card2_damage, c2.card_type AS card2_type, " +
                        "  c3.name AS card3_name, c3.damage AS card3_damage, c3.card_type AS card3_type, " +
                        "  c4.name AS card4_name, c4.damage AS card4_damage, c4.card_type AS card4_type " +
                        "FROM decks d " +
                        "JOIN cards c1 ON d.card1 = c1.id " +
                        "JOIN cards c2 ON d.card2 = c2.id " +
                        "JOIN cards c3 ON d.card3 = c3.id " +
                        "JOIN cards c4 ON d.card4 = c4.id " +
                        "WHERE d.user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement sqlGetDeckStatement = connection.prepareStatement(sqlGetDeck)) {

            // Setze die Benutzer-ID in das PreparedStatement ein
            sqlGetDeckStatement.setObject(1, userid);

            ResultSet rs = sqlGetDeckStatement.executeQuery();

            ArrayList<Card> deckCards = new ArrayList<>();
            if (rs.next()) {
                // Füge die Karten mit ihren Eigenschaften (ohne ID) in die Liste ein
                deckCards.add(new Card(
                        rs.getString("card1_name"),
                        rs.getDouble("card1_damage"),
                        rs.getString("card1_type")
                ));
                deckCards.add(new Card(
                        rs.getString("card2_name"),
                        rs.getDouble("card2_damage"),
                        rs.getString("card2_type")
                ));
                deckCards.add(new Card(
                        rs.getString("card3_name"),
                        rs.getDouble("card3_damage"),
                        rs.getString("card3_type")
                ));
                deckCards.add(new Card(
                        rs.getString("card4_name"),
                        rs.getDouble("card4_damage"),
                        rs.getString("card4_type")
                ));
            }

            return deckCards;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new BadRequestException("BADREQUEST: " + e.getMessage());
        }
    }

    public void updateCardUserId(UUID winnerId, List<UUID> cardIds) {
        String updateCardUserIdSQL = "UPDATE cards SET user_id = ? WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateCardUserIdSQL)) {

            connection.setAutoCommit(false); // Transaktion starten

            for (UUID cardId : cardIds) {
                stmt.setObject(1, winnerId);
                stmt.setObject(2, cardId);
                stmt.addBatch(); // Batch-Update für alle Karten
            }

            // Alle Updates in einer Transaktion ausführen
            int[] rowsAffected = stmt.executeBatch();

            // Falls nicht alle Karten aktualisiert wurden, Fehler werfen
            if (rowsAffected.length != cardIds.size()) {
                throw new SQLException("Nicht alle Karten konnten übertragen werden.");
            }

            connection.commit(); // Transaktion erfolgreich abschließen

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Übertragen der Karten.", e);
        }
    }

    public List<UUID> getDeckCardIds(UUID userId) {
        String sqlGetDeckCardIds = "SELECT card1, card2, card3, card4 FROM decks WHERE user_id = ?";

        List<UUID> cardIds = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sqlGetDeckCardIds)) {

            // Setze die Benutzer-ID in das PreparedStatement ein
            stmt.setObject(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Füge die IDs der Karten zum List hinzu
                cardIds.add((UUID) rs.getObject("card1"));
                cardIds.add((UUID) rs.getObject("card2"));
                cardIds.add((UUID) rs.getObject("card3"));
                cardIds.add((UUID) rs.getObject("card4"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Abrufen der Karten-IDs.", e);
        }

        return cardIds;
    }
}

