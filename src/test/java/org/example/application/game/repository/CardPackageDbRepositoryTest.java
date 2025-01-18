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
        when(connectionPoolMock.getConnection()).thenReturn(connectionMock);
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
