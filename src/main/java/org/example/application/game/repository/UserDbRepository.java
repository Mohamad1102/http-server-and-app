package org.example.application.game.repository;

import org.example.application.game.data.ConnectionPool;
import org.example.application.game.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
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

    @Override
    public ArrayList<User> findAll() {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT id, username, password, coins FROM users";  // Abfrage für alle Benutzer

        System.out.println("Vor der Verbindung zur Datenbank");

        try (
                // Verbindung zur Datenbank herstellen
                Connection conn = connectionPool.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()
        ) {
            System.out.println("Verbindung zur Datenbank hergestellt");

            // Durch die ResultSet-Ergebnisse iterieren
            while (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);          // ID des Benutzers
                String username = rs.getString("username"); // Username des Benutzers
                String password = rs.getString("password"); // Passwort des Benutzers
                int coins = rs.getInt("coins");           // Coins des Benutzers

                System.out.println("Benutzer gefunden: " + username);

                // Benutzer-Objekt erstellen
                User user = new User(id, username, password, coins);
                users.add(user);  // Benutzer zur Liste hinzufügen
            }

            // Falls keine Benutzer in der Datenbank gefunden wurden
            if (users.isEmpty()) {
                System.out.println("Keine Benutzer gefunden in der Datenbank.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Fehler beim Abrufen der Daten: " + e.getMessage());
        }

        System.out.println("Rückgabe der Benutzer: " + users.size() + " Benutzer gefunden.");
        return users;  // Alle Benutzer zurückgeben
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
        String query = "SELECT id, username, password, coins FROM users WHERE username = ?";
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
                    User user = new User(id, username, password, coins); // Erstelle User mit aktuellen Coins
                    return user;
                }
                System.out.println("Verbindung Erfolgreich für findUserByUsername");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
