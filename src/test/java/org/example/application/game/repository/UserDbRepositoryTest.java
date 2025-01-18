package org.example.application.game.repository;

import org.example.application.game.data.ConnectionPool;
import org.example.application.game.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserDbRepositoryTest {

    private UserDbRepository userDbRepository;
    private ConnectionPool connectionPoolMock;
    private Connection connectionMock;
    private PreparedStatement preparedStatementMock;
    private ResultSet resultSetMock;

    @BeforeEach
    void setUp() {
        connectionPoolMock = mock(ConnectionPool.class);
        connectionMock = mock(Connection.class);
        preparedStatementMock = mock(PreparedStatement.class);
        resultSetMock = mock(ResultSet.class);
        userDbRepository = new UserDbRepository(connectionPoolMock);

        // Mocking connection pool to return mocked connection
        try {
            when(connectionPoolMock.getConnection()).thenReturn(connectionMock);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSave() throws SQLException {
        // Setup
        User user = new User(UUID.randomUUID(), "testUser", "password123", 100, "John Doe", "Bio", "image.png");

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);

        // Act
        User savedUser = userDbRepository.save(user);

        // Assert
        assertNotNull(savedUser);
        verify(preparedStatementMock, times(1)).setObject(1, user.getId());
        verify(preparedStatementMock, times(1)).setString(2, user.getUsername());
        verify(preparedStatementMock, times(1)).setString(3, user.getPassword());
        verify(preparedStatementMock, times(1)).execute();
    }

    @Test
    void testFindByUsername_UserExists() throws SQLException {
        // Setup
        String username = "testUser";
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getLong(1)).thenReturn(1L);

        // Act
        boolean exists = userDbRepository.findByUsername(username);

        // Assert
        assertTrue(exists);
        verify(preparedStatementMock, times(1)).setString(1, username);
        verify(preparedStatementMock, times(1)).executeQuery();
    }

    @Test
    void testFindByUsername_UserNotFound() throws SQLException {
        // Setup
        String username = "nonExistentUser";
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        // Act
        boolean exists = userDbRepository.findByUsername(username);

        // Assert
        assertFalse(exists);
        verify(preparedStatementMock, times(1)).setString(1, username);
        verify(preparedStatementMock, times(1)).executeQuery();
    }

    @Test
    void testIsValidUser_ValidCredentials() throws SQLException {
        // Setup
        String username = "testUser";
        String password = "password123";

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getLong(1)).thenReturn(1L);

        // Act
        boolean isValid = userDbRepository.isValidUser(username, password);

        // Assert
        assertTrue(isValid);
        verify(preparedStatementMock, times(1)).setString(1, username);
        verify(preparedStatementMock, times(1)).setString(2, password);
        verify(preparedStatementMock, times(1)).executeQuery();
    }

    @Test
    void testIsValidUser_InvalidCredentials() throws SQLException {
        // Setup
        String username = "testUser";
        String password = "wrongPassword";

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        // Act
        boolean isValid = userDbRepository.isValidUser(username, password);

        // Assert
        assertFalse(isValid);
        verify(preparedStatementMock, times(1)).setString(1, username);
        verify(preparedStatementMock, times(1)).setString(2, password);
        verify(preparedStatementMock, times(1)).executeQuery();
    }

    @Test
    void testUpdateCoins() throws SQLException {
        // Setup
        String username = "testUser";
        int coins = 200;

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);

        // Act
        userDbRepository.updateCoins(username, coins);

        // Assert
        verify(preparedStatementMock, times(1)).setInt(1, coins);
        verify(preparedStatementMock, times(1)).setString(2, username);
        verify(preparedStatementMock, times(1)).executeUpdate();
    }

    @Test
    void testFindUserByUsername_Found() throws SQLException {
        // Setup
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        User user = new User(userId, username, "password123", 100, "John", "Bio", "image.png");

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString("id")).thenReturn(userId.toString());
        when(resultSetMock.getString("username")).thenReturn(username);
        when(resultSetMock.getString("password")).thenReturn("password123");
        when(resultSetMock.getInt("coins")).thenReturn(100);
        when(resultSetMock.getString("name")).thenReturn("John");
        when(resultSetMock.getString("bio")).thenReturn("Bio");
        when(resultSetMock.getString("image")).thenReturn("image.png");

        // Act
        User foundUser = userDbRepository.findUserByUsername(username);

        // Assert
        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
    }

    @Test
    void testFindUserByUsername_NotFound() throws SQLException {
        // Setup
        String username = "nonExistentUser";

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        // Act
        User foundUser = userDbRepository.findUserByUsername(username);

        // Assert
        assertNull(foundUser);
    }

    @Test
    void testGetUserIdByUsername() throws SQLException {
        // Setup
        String username = "testUser";
        UUID expectedId = UUID.randomUUID();

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString("id")).thenReturn(expectedId.toString());

        // Act
        UUID userId = userDbRepository.getUserIdByUsername(username);

        // Assert
        assertEquals(expectedId, userId);
    }
}

/*

Erklärung der Tests:
testSave: Testet das Speichern eines neuen Benutzers in der Datenbank.
testFindByUsername_UserExists: Testet die Existenz eines Benutzers anhand des Benutzernamens.
testFindByUsername_UserNotFound: Testet die Nicht-Existenz eines Benutzers anhand des Benutzernamens.
testIsValidUser_ValidCredentials: Testet die Validierung von Benutzeranmeldedaten.
testIsValidUser_InvalidCredentials: Testet die ungültige Benutzeranmeldung.
testUpdateCoins: Testet das Aktualisieren der Coins eines Benutzers.
testFindUserByUsername_Found: Testet das Abrufen eines Benutzers anhand des Benutzernamens.
testFindUserByUsername_NotFound: Testet den Fall, dass der Benutzer nicht gefunden wird.
testGetUserIdByUsername: Testet das Abrufen der Benutzer-ID anhand des Benutzernamens.
Weitere Hinweise:
Mockito wird verwendet, um alle externen Abhängigkeiten zu mocken (z. B. Connection, PreparedStatement, ResultSet).
JUnit 5 wird für die Teststruktur verwendet, aber du kannst auch JUnit 4 verwenden, wenn du es bevorzugst.
Diese Tests bieten eine gute Grundlage, um sicherzustellen, dass die UserDbRepository-Klasse korrekt funktioniert, ohne dass eine echte Datenbankverbindung erforderlich ist.
Mit diesen Tests kannst du sicherstellen, dass deine Repository-Methoden korrekt arbeiten und die Interaktion mit der Datenbank korrekt simuliert wird.
 */