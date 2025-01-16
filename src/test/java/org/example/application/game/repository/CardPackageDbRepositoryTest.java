package org.example.application.game.repository;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.Package;
import org.example.application.game.entity.User;
import org.example.application.game.data.ConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CardPackageDbRepositoryTest {

    private CardPackageDbRepository cardPackageDbRepository;
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
        cardPackageDbRepository = new CardPackageDbRepository(connectionPoolMock);

        // Mocking connection pool to return mocked connection
        try {
            when(connectionPoolMock.getConnection()).thenReturn(connectionMock);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCreatePackageForAdmin_Success() throws SQLException {
        // Setup
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card("Card 1", 10.0, "Type A"));
        cards.add(new Card("Card 2", 15.0, "Type B"));
        cards.add(new Card("Card 3", 20.0, "Type C"));
        cards.add(new Card("Card 4", 25.0, "Type D"));
        cards.add(new Card("Card 5", 30.0, "Type E"));
        User adminUser = new User(UUID.randomUUID(), "admin", "password123", 100, "Admin", "Admin Bio", "admin.png");

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getObject(1, UUID.class)).thenReturn(UUID.randomUUID());

        // Act
        UUID packageId = cardPackageDbRepository.createPackageForAdmin(cards, adminUser);

        // Assert
        assertNotNull(packageId);
        verify(preparedStatementMock, times(1)).executeQuery(); // Ensure SQL executed
        verify(preparedStatementMock, times(1)).executeBatch(); // Ensure batch execution
    }

    @Test
    void testCreatePackageForAdmin_Fail_NonAdmin() {
        // Setup
        ArrayList<Card> cards = new ArrayList<>();
        User nonAdminUser = new User(UUID.randomUUID(), "nonadmin", "password123", 100, "User", "User Bio", "user.png");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cardPackageDbRepository.createPackageForAdmin(cards, nonAdminUser);
        });

        assertEquals("Nur der Admin kann ein Paket erstellen.", exception.getMessage());
    }

    @Test
    void testFindAvailablePackage_Found() throws SQLException {
        // Setup
        UUID packageId = UUID.randomUUID();
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card("Card 1", 10.0, "Type A"));

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getObject("id", UUID.class)).thenReturn(packageId);

        // Simulating fetching cards for the package
        PreparedStatement cardStmtMock = mock(PreparedStatement.class);
        ResultSet cardResultSetMock = mock(ResultSet.class);
        when(connectionMock.prepareStatement(anyString())).thenReturn(cardStmtMock);
        when(cardStmtMock.executeQuery()).thenReturn(cardResultSetMock);
        when(cardResultSetMock.next()).thenReturn(true);
        when(cardResultSetMock.getString("name")).thenReturn("Card 1");
        when(cardResultSetMock.getDouble("damage")).thenReturn(10.0);
        when(cardResultSetMock.getString("card_type")).thenReturn("Type A");

        // Act
        Optional<Package> availablePackage = cardPackageDbRepository.findAvailablePackage();

        // Assert
        assertTrue(availablePackage.isPresent());
        assertEquals(packageId, availablePackage.get().getId());
        assertEquals(1, availablePackage.get().getCards().size());
    }

    @Test
    void testFindAvailablePackage_NotFound() throws SQLException {
        // Setup
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false); // No available package

        // Act
        Optional<Package> availablePackage = cardPackageDbRepository.findAvailablePackage();

        // Assert
        assertFalse(availablePackage.isPresent());
    }

    @Test
    void testAssignPackageToUser_Success() throws SQLException {
        // Setup
        UUID packageId = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "testUser", "password123", 100, "John", "Bio", "image.png");

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);

        // Act
        cardPackageDbRepository.assignPackageToUser(packageId, user);

        // Assert
        verify(preparedStatementMock, times(1)).executeUpdate(); // Ensure SQL statements executed
        verify(preparedStatementMock, times(1)).setObject(1, packageId);
    }

    @Test
    void testFindCardsByUsername() throws SQLException {
        // Setup
        UUID userId = UUID.randomUUID();
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card("Card 1", 10.0, "Type A"));
        cards.add(new Card("Card 2", 20.0, "Type B"));

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, true, false);  // Simulate two cards found
        when(resultSetMock.getString("name")).thenReturn("Card 1", "Card 2");
        when(resultSetMock.getDouble("damage")).thenReturn(10.0, 20.0);
        when(resultSetMock.getString("card_type")).thenReturn("Type A", "Type B");

        // Act
        ArrayList<Card> foundCards = cardPackageDbRepository.findCardsByUsername(userId);

        // Assert
        assertEquals(2, foundCards.size());
        assertEquals("Card 1", foundCards.get(0).getName());
        assertEquals("Card 2", foundCards.get(1).getName());
    }

    @Test
    void testGetCardCount() throws SQLException {
        // Setup
        String username = "testUser";
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getInt(1)).thenReturn(5); // User has 5 cards

        // Act
        int cardCount = cardPackageDbRepository.getCardCount(username);

        // Assert
        assertEquals(5, cardCount);
    }

    @Test
    void testIsCardOwnedByUser() throws SQLException {
        // Setup
        UUID cardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getInt(1)).thenReturn(1); // Card is owned by the user

        // Act
        boolean isOwned = cardPackageDbRepository.isCardOwnedByUser(cardId, userId);

        // Assert
        assertTrue(isOwned);
    }
}
