package org.example.application.game.repository;

import org.example.application.game.data.ConnectionPool;
import org.example.application.game.entity.Card;
import org.example.application.game.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class UserDbRepository implements UserRepository{
    private final ConnectionPool connectionPool;
    private final static String NEW_USER
            = "INSERT INTO users VALUES (?, ?, ?)";
    public UserDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
    @Override
    public User save(User user) {
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(NEW_USER)
        ) {
            preparedStatement.setObject(1, user.getId());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.execute();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public boolean findByUsername(String username) {
        String query = "SELECT COUNT(*) FROM users u WHERE u.username = ?";
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long count = rs.getLong(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean isValidUser(String username, String password) {
        String query = "SELECT COUNT(*) FROM users u WHERE u.username = ? AND u.password = ?";
        try (
                Connection conn = connectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long count = rs.getLong(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateCoins(String username, int coins) {
        String query = "UPDATE users SET coins = ? WHERE username = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, coins);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("Updating coins for user: " + username + " to " + coins);
    }
    public User findUserByUsername(String username) {
        String query = "SELECT id, username, password, coins, name, bio, image FROM users WHERE username = ?";
        System.out.println("findUserByUsername STATMENT");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            System.out.println("TRY 1");
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("TRY 2");
                if (rs.next()) {
                    System.out.println("2.1");
                    UUID id = UUID.fromString(rs.getString("id"));
                    System.out.println("TRY 2.2");
                    String password = rs.getString("password");
                    int coins = rs.getInt("coins");
                    System.out.println("TRY 2.3");
                    String name = rs.getString("name");
                    String bio = rs.getString("bio");
                    String image = rs.getString("image");

                    User user = new User(id, username, password, coins, name, bio, image); // Erstelle User mit aktuellen Coins
                    return user;
                }
                System.out.println("Verbindung Erfolgreich für findUserByUsername");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UUID getUserIdByUsername(String username) throws RuntimeException {
        String query = "SELECT id FROM users WHERE username = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("id"));
                } else {
                    throw new IllegalArgumentException("User not found for username: " + username);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public User updateUser(User user) {
        String query = "UPDATE users SET name = ?, bio = ?, image = ? WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Setze die neuen Werte für die Felder name, bio und image
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getBio());
            stmt.setString(3, user.getImage());
            stmt.setObject(4, user.getId());

            // Ausführung des Updates
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User successfully updated: " + user.getUsername());
                return user; // Aktualisierter Benutzer zurückgeben
            } else {
                System.out.println("No user found with ID: " + user.getId());
                return null; // Kein Benutzer aktualisiert
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }

    public User getUserData(String username) {
        String query = "SELECT id, username, password, coins, name, bio, image FROM users WHERE username = ?";
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UUID id = UUID.fromString(rs.getString("id"));
                    String password = rs.getString("password");
                    int coins = rs.getInt("coins");
                    String name = rs.getString("name");
                    String bio = rs.getString("bio");
                    String image = rs.getString("image");

                    User user = new User(id, username, password, coins, name, bio, image);

                    // Abfrage der Karten des Benutzers
                    String cardQuery = "SELECT c.id, c.name, c.damage, c.card_type FROM cards c "
                            + "JOIN cards uc ON uc.id = c.id "
                            + "WHERE uc.user_id = ?";
                    try (PreparedStatement cardStmt = conn.prepareStatement(cardQuery)) {
                        cardStmt.setObject(1, id);  // UUID des Benutzers
                        try (ResultSet cardRs = cardStmt.executeQuery()) {
                            while (cardRs.next()) {
                                UUID cardId = UUID.fromString(cardRs.getString("id"));
                                String cardName = cardRs.getString("name");
                                double cardDamage = cardRs.getDouble("damage");
                                String cardType = cardRs.getString("card_type");
                                Card card = new Card(cardName, cardDamage, cardType);
                                card.setId(cardId);  // Setze die UUID der Karte
                                user.addCard(card);  // Füge die Karte zur Liste des Benutzers hinzu
                            }
                        }
                    }

                    return user;  // Gibt den Benutzer mit den Karten zurück
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Kein Benutzer gefunden
    }
}
