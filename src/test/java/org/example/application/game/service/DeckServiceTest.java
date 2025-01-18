package org.example.application.game.service;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.User;
import org.example.application.game.exception.BadRequestException;
import org.example.application.game.repository.CardPackageRepository;
import org.example.application.game.repository.DeckDbRepository;
import org.example.application.game.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeckServiceTest {

    private DeckService deckService;
    private DeckDbRepository deckRepositoryMock;
    private UserRepository userRepositoryMock;
    private CardPackageRepository cardPackageRepositoryMock;

    @BeforeEach
    void setUp() {
        deckRepositoryMock = mock(DeckDbRepository.class);
        userRepositoryMock = mock(UserRepository.class);
        cardPackageRepositoryMock = mock(CardPackageRepository.class);
        deckService = new DeckService(deckRepositoryMock, userRepositoryMock, cardPackageRepositoryMock);
    }

    @Test
    void createDeck_ValidData_CreatesDeckSuccessfully() {
        // Arrange
        String token = "testUser-mtcgToken";
        String username = "testUser";
        UUID userId = UUID.randomUUID();

        // Erstellen von Beispielkarten
        UUID card1Id = UUID.randomUUID();
        UUID card2Id = UUID.randomUUID();
        UUID card3Id = UUID.randomUUID();
        UUID card4Id = UUID.randomUUID();
        ArrayList<UUID> cardsID = new ArrayList<>();
        cardsID.add(card1Id);
        cardsID.add(card2Id);
        cardsID.add(card3Id);
        cardsID.add(card4Id);

        // Erstellen eines Users
        User user = new User(userId, username, "password", 100, "John Doe", "Bio", "image.jpg");

        // Karten des Benutzers (dieser besitzt genau die Karten, die er im Deck nutzen möchte)
        ArrayList<Card> userCards = new ArrayList<>();
        userCards.add(new Card("Card1", 5.0, "MONSTER"));
        userCards.add(new Card("Card2", 6.0, "MONSTER"));
        userCards.add(new Card("Card3", 7.0, "SPELL"));
        userCards.add(new Card("Card4", 8.0, "SPELL"));

        // Sicherstellen, dass die Karten-IDs von userCards mit denen in cardsID übereinstimmen
        userCards.get(0).setId(card1Id);
        userCards.get(1).setId(card2Id);
        userCards.get(2).setId(card3Id);
        userCards.get(3).setId(card4Id);

        // Mocks vorbereiten
        when(userRepositoryMock.findUserByUsername(username)).thenReturn(user);
        when(cardPackageRepositoryMock.findCardsByUsername(userId)).thenReturn(userCards);
        when(deckRepositoryMock.createDeck(cardsID, user)).thenReturn(UUID.randomUUID()); // Simuliere die Erstellung eines Decks

        // Act
        UUID deckId = deckService.createDeck(cardsID, token);

        // Assert
        assertNotNull(deckId, "Deck ID sollte nicht null sein.");
        verify(userRepositoryMock, times(1)).findUserByUsername(username);
        verify(cardPackageRepositoryMock, times(1)).findCardsByUsername(userId);
        verify(deckRepositoryMock, times(1)).createDeck(cardsID, user);
    }


    @Test
    void createDeck_InvalidToken_ThrowsIllegalArgumentException() {
        // Arrange
        String token = "invalidTokenFormat";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> deckService.createDeck(new ArrayList<>(), token),
                "Invalid token should throw IllegalArgumentException");
    }

    @Test
    void createDeck_UserNotFound_ThrowsIllegalArgumentException() {
        // Arrange
        String token = "testUser-mtcgToken";
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        ArrayList<UUID> cardsID = new ArrayList<>();
        cardsID.add(UUID.randomUUID());
        cardsID.add(UUID.randomUUID());
        cardsID.add(UUID.randomUUID());
        cardsID.add(UUID.randomUUID());

        when(userRepositoryMock.findUserByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> deckService.createDeck(cardsID, token),
                "User not found should throw IllegalArgumentException");
        verify(userRepositoryMock, times(1)).findUserByUsername(username);
    }

    @Test
    void createDeck_DeckSizeInvalid_ThrowsIllegalArgumentException() {
        // Arrange
        String token = "testUser-mtcgToken";
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        ArrayList<UUID> cardsID = new ArrayList<>();
        cardsID.add(UUID.randomUUID()); // Only 1 card instead of 4
        User user = new User(userId, username, "password", 100, "John Doe", "Bio", "image.jpg");
        when(userRepositoryMock.findUserByUsername(username)).thenReturn(user);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> deckService.createDeck(cardsID, token),
                "Deck must contain exactly 4 cards");
    }

    @Test
    void createDeck_InvalidCard_ThrowsBadRequestException() {
        // Arrange
        String token = "testUser-mtcgToken";
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        UUID validCardId = UUID.randomUUID();
        UUID invalidCardId = UUID.randomUUID();
        ArrayList<UUID> cardsID = new ArrayList<>();
        cardsID.add(validCardId);
        cardsID.add(validCardId);
        cardsID.add(validCardId);
        cardsID.add(invalidCardId); // Invalid card
        User user = new User(userId, username, "password", 100, "John Doe", "Bio", "image.jpg");
        ArrayList<Card> userCards = new ArrayList<>();
        userCards.add(new Card("Card1", 5.0, "MONSTER"));
        userCards.add(new Card("Card2", 6.0, "MONSTER"));
        userCards.add(new Card("Card3", 7.0, "SPELL"));
        userCards.add(new Card("Card4", 8.0, "SPELL"));
        when(userRepositoryMock.findUserByUsername(username)).thenReturn(user);
        when(cardPackageRepositoryMock.findCardsByUsername(userId)).thenReturn(userCards);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> deckService.createDeck(cardsID, token),
                "Card not owned by user should throw BadRequestException");
    }

    @Test
    void getDeck_ValidToken_ReturnsDeck() {
        // Arrange
        String token = "testUser-mtcgToken";
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        User user = new User(userId, username, "password", 100, "John Doe", "Bio", "image.jpg");
        ArrayList<Card> expectedDeck = new ArrayList<>();
        expectedDeck.add(new Card("Card1", 5.0, "MONSTER"));
        expectedDeck.add(new Card("Card2", 6.0, "MONSTER"));

        when(userRepositoryMock.findUserByUsername(username)).thenReturn(user);
        when(deckRepositoryMock.getDeck(userId)).thenReturn(expectedDeck);

        // Act
        ArrayList<Card> actualDeck = deckService.getDeck(token);

        // Assert
        assertNotNull(actualDeck, "Deck should not be null");
        assertEquals(expectedDeck, actualDeck, "Deck should match the expected value");
        verify(userRepositoryMock, times(1)).findUserByUsername(username);
        verify(deckRepositoryMock, times(1)).getDeck(userId);
    }

    @Test
    void getDeck_TokenNotProvided_ThrowsIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> deckService.getDeck(null),
                "Null token should throw IllegalArgumentException");
    }

    @Test
    void getDeckPlain_ValidToken_ReturnsDeck() {
        // Arrange
        String token = "testUser-mtcgToken";
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        User user = new User(userId, username, "password", 100, "John Doe", "Bio", "image.jpg");
        ArrayList<Card> expectedDeck = new ArrayList<>();
        expectedDeck.add(new Card("Card1", 5.0, "MONSTER"));
        expectedDeck.add(new Card("Card2", 6.0, "MONSTER"));

        when(userRepositoryMock.findUserByUsername(username)).thenReturn(user);
        when(deckRepositoryMock.getDeckPlain(userId)).thenReturn(expectedDeck);

        // Act
        ArrayList<Card> actualDeck = deckService.getDeckPlain(token);

        // Assert
        assertNotNull(actualDeck, "Deck should not be null");
        assertEquals(expectedDeck, actualDeck, "Deck should match the expected value");
        verify(userRepositoryMock, times(1)).findUserByUsername(username);
        verify(deckRepositoryMock, times(1)).getDeckPlain(userId);
    }

    @Test
    void getDeckPlain_TokenNotProvided_ThrowsIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> deckService.getDeckPlain(null),
                "Null token should throw IllegalArgumentException");
    }
}
