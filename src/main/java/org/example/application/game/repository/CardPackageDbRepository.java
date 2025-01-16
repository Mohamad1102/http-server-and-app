package org.example.application.game.repository;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.Package;
import org.example.application.game.data.ConnectionPool;
import org.example.application.game.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CardPackageDbRepository implements CardPackageRepository {

    private final ConnectionPool connectionPool;

    public CardPackageDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    // Methode, um ein neues Package mit genau 5 Karten zu erstellen
    public UUID createPackageForAdmin(ArrayList<Card> cards, User adminUser) {
        if (!adminUser.getUsername().equals("admin")) {
            throw new IllegalArgumentException("Nur der Admin kann ein Paket erstellen.");
        }

        System.out.println("ES SIND 5 Karten");
        String insertPackageSQL = "INSERT INTO packages (availability) VALUES (TRUE) RETURNING id";
        String insertCardSQL = "INSERT INTO cards (id, name, damage, card_type, package_id) VALUES (?, ?, ?, ?, ?)";
        System.out.println("VERBINDUNG erstellen!!");
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement packageStatement = connection.prepareStatement(insertPackageSQL);
                PreparedStatement cardStatement = connection.prepareStatement(insertCardSQL)
        ) {
            if (connection == null) {
                System.out.println("Verbindung zur Datenbank konnte nicht hergestellt werden.");
                return null; // Frühzeitig die Methode beenden
            } else {
                System.out.println("Verbindung zur Datenbank erfolgreich.");
            }
            connection.setAutoCommit(false); // Transaktion starten

            System.out.println("PAKET UND ID erstellen!!");
            // Paket erstellen und ID erhalten
            ResultSet packageResultSet = packageStatement.executeQuery();
            if (!packageResultSet.next()) {
                System.out.println("VOR IF!!");
                throw new SQLException("Fehler beim Erstellen des Pakets.");
            }
            System.out.println("NACH IF !!");
            UUID packageId = packageResultSet.getObject(1, UUID.class);

            System.out.println("NACH IF 1!");
            System.out.println(cards.size());

            System.out.println("Cards: " + cards);

            if (!(cards instanceof List)) {
                System.out.println("'cards' ist kein List-Typ.");
            } else if (!cards.isEmpty() && !(cards.get(0) instanceof Card)) {
                System.out.println("Die Elemente in 'cards' sind nicht vom Typ Card.");
            }

            for (int i = 0; i < cards.size(); i++) {
                Card card = cards.get(i);
                if (card == null) {
                    System.out.println("Index " + i + ": card ist null!");
                } else {
                    System.out.println("Index " + i + ": Karte Name=" + card.getName() +card.getDamage() + card.getCardType());
                }
            }

            // Karten in die Datenbank einfügen und mit dem Paket verknüpfen
            for (Card card : cards)  {
                cardStatement.setObject(1, card.getId());
                System.out.println("FOR LOOP!!");
                cardStatement.setString(2, card.getName());
                System.out.println("LOOP 2!");
                cardStatement.setDouble(3, card.getDamage());
                System.out.println("LOOP 3!");
                cardStatement.setString(4, card.getCardType());
                cardStatement.setObject(5, packageId);  // Verknüpft die Karte mit dem Paket
                System.out.println("LOOP 5!");
                cardStatement.addBatch(); // Karten zu einer Batch-Verarbeitung hinzufügen
            }

            System.out.println("NACH FOR LOOP!!");

            cardStatement.executeBatch(); // Alle Karten auf einmal einfügen

            connection.commit(); // Transaktion abschließen
            System.out.println("PACKET WURDE ERSTELLT");
            return packageId; // Paket-ID zurückgeben


        } catch (SQLException e) {
            System.out.println("ERROR");
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Speichern des Pakets und der Karten.", e);
        }
    }


    // Methode, um ein verfügbares Paket zu finden
    public Optional<Package> findAvailablePackage() {
        String query = "SELECT id FROM packages WHERE availability = TRUE LIMIT 1";  // Nur ein verfügbares Paket

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                UUID packageId = resultSet.getObject("id", UUID.class);

                // Jetzt die Karten für dieses Paket laden
                String cardQuery = "SELECT name, damage, card_type FROM cards WHERE package_id = ?";
                try (PreparedStatement cardStmt = connection.prepareStatement(cardQuery)) {
                    cardStmt.setObject(1, packageId);
                    ResultSet cardResultSet = cardStmt.executeQuery();

                    ArrayList<Card> cards = new ArrayList<>();
                    while (cardResultSet.next()) {
                        String name = cardResultSet.getString("name");
                        double damage = cardResultSet.getDouble("damage");
                        String cardType = cardResultSet.getString("card_type");
                        // Karte erstellen
                        Card card = new Card(name, damage, cardType);  // Hier setzt du den Konstruktor für die Karte ein
                        cards.add(card);
                    }

                    // Rückgabe des Pakets mit den geladenen Karten
                    Package pkg = new Package(packageId, cards);  // Hier wird das Package mit den Karten erstellt
                    return Optional.of(pkg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();  // Kein verfügbares Paket gefunden
    }

    // Methode, um einem User ein Package zuzuweisen
    public void assignPackageToUser(UUID packageId, User user) {
        String sqlUpdatePackage = "UPDATE packages SET availability = FALSE WHERE id = ?";
        String sqlUpdateCards = "UPDATE cards SET user_id = ? WHERE package_id = ?";

        System.out.println("KAUF PACKAGE SQL STATMENT");

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement updatePackageStmt = connection.prepareStatement(sqlUpdatePackage);
             PreparedStatement updateCardsStmt = connection.prepareStatement(sqlUpdateCards)) {

            connection.setAutoCommit(false);

            // Paket als nicht verfügbar setzen
            updatePackageStmt.setObject(1, packageId);
            updatePackageStmt.executeUpdate();

            // Karten dem Benutzer zuweisen
            updateCardsStmt.setObject(1, user.getId());
            updateCardsStmt.setObject(2, packageId);
            updateCardsStmt.executeUpdate();

            connection.commit();  // Transaktion abschließen

            System.out.println("Paket und Karten erfolgreich dem Benutzer zugewiesen.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Zuweisen des Pakets an den Benutzer", e);
        }
    }

    public ArrayList<Card> findCardsByUsername(UUID userid)
    {
        String sqlSchowCards = "SELECT * From cards WHERE user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement sqlSchowCardsstatment = connection.prepareStatement(sqlSchowCards)) {

            sqlSchowCardsstatment.setObject(1, userid);

            ResultSet rs = sqlSchowCardsstatment.executeQuery();

            ArrayList<Card> cards = new ArrayList<>();
            while (rs.next()){
                Card card= new Card();
                UUID id = UUID.fromString(rs.getString("id"));
                String name = rs.getString("name");
                double damage = rs.getDouble("damage");
                String cardtype = rs.getString("card_type");

                card.setId(id);
                card.setName(name);
                card.setDamage(damage);
                card.setCardType(cardtype);

                cards.add(card);
            }

            return cards;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Show Cards", e);
        }

    }

    public Card findCardById(UUID cardId) {
        String query = "SELECT * FROM cards WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setObject(1, cardId); // Parameter setzen

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Neue Card-Instanz erstellen und mit Werten füllen
                    Card card = new Card();
                    card.setId(UUID.fromString(rs.getString("id")));
                    card.setName(rs.getString("name"));
                    card.setDamage(rs.getDouble("damage"));
                    card.setCardType(rs.getString("card_type"));

                    return card; // Karte zurückgeben
                } else {
                    return null; // Keine Karte gefunden, null zurückgeben
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Abrufen der Karte mit ID: " + cardId, e);
        }
    }

    public boolean isCardOwnedByUser(UUID cardId, UUID userId) {
        String query = "SELECT COUNT(*) FROM cards WHERE id = ? AND user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setObject(1, cardId);
            stmt.setObject(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // True, wenn mindestens ein Eintrag gefunden wurde
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error validating card ownership", e);
        }

        return false;
    }

    public int getCardCount(String username) {
        String query = "SELECT COUNT(*) FROM cards WHERE user_id = (SELECT id FROM users WHERE username = ?)";
        int cardCount = 0;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                cardCount = resultSet.getInt(1); // Anzahl der Karten
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cardCount;
    }

}
