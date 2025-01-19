package org.example.application.game.service;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.Package;
import org.example.application.game.entity.User;
import org.example.application.game.repository.CardPackageRepository;
import org.example.application.game.repository.UserDbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardPackageServiceTest {
    private CardPackageRepository cardPackageRepository;
    private UserDbRepository userRepository;
    private CardPackageService cardPackageService;

    @BeforeEach
    void setUp() {
        cardPackageRepository = mock(CardPackageRepository.class);
        userRepository = mock(UserDbRepository.class);
        cardPackageService = new CardPackageService(cardPackageRepository, userRepository);
    }

    @Test
    void testCreatePackage_Success() {
        // Setup
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cards.add(new Card("Card" + i, 10.0, "MONSTER"));
        }
        String token = "admin-12345";
        User adminUser = new User();
        adminUser.setUsername("admin");

        when(userRepository.findUserByUsername("admin")).thenReturn(adminUser);
        when(cardPackageRepository.createPackageForAdmin(cards, adminUser)).thenReturn(UUID.randomUUID());

        // Act
        UUID packageId = cardPackageService.createPackage(cards, token);

        // Assert
        assertNotNull(packageId);
        verify(cardPackageRepository, times(1)).createPackageForAdmin(eq(cards), eq(adminUser));
    }

    @Test
    void testCreatePackage_NotAdmin() {
        // Setup
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cards.add(new Card("Card" + i, 10.0, "MONSTER"));
        }
        String token = "user-12345";
        User regularUser = new User();
        regularUser.setUsername("user");

        when(userRepository.findUserByUsername("user")).thenReturn(regularUser);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cardPackageService.createPackage(cards, token);
        });
        assertEquals("Nur der Admin kann ein Paket erstellen.", exception.getMessage());
        verify(cardPackageRepository, never()).createPackageForAdmin(eq(cards), any(User.class));  // Verwende "any(User.class)"
    }

    @Test
    void testBuyPackage_Success() {
        // Setup
        String token = "user-12345";
        User user = new User();
        user.setUsername("user");
        user.setCoins(10);

        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card("Card1", 10.0, "MONSTER"));
        cards.add(new Card("Card2", 20.0, "SPELL"));

        Package cardPackage = new Package(UUID.randomUUID(), cards);

        when(userRepository.findByUsername("user")).thenReturn(true);
        when(userRepository.findUserByUsername("user")).thenReturn(user);
        when(cardPackageRepository.findAvailablePackage()).thenReturn(Optional.of(cardPackage));

        // Act
        boolean success = cardPackageService.buyPackage(token);

        // Assert
        assertTrue(success);
        verify(userRepository, times(1)).updateCoins(eq("user"), eq(5));  // User should have 5 coins left
        verify(cardPackageRepository, times(1)).assignPackageToUser(eq(cardPackage.getId()), eq(user));
    }

    @Test
    void testBuyPackage_NoPackagesAvailable() {
        // Setup
        String token = "user-12345";
        User user = new User();
        user.setUsername("user");
        user.setCoins(10);

        when(userRepository.findByUsername("user")).thenReturn(true);
        when(userRepository.findUserByUsername("user")).thenReturn(user);
        when(cardPackageRepository.findAvailablePackage()).thenReturn(Optional.empty());  // Keine Pakete verfügbar

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cardPackageService.buyPackage(token);
        });
        assertEquals("No packages available!", exception.getMessage());
        verify(cardPackageRepository, never()).assignPackageToUser(any(UUID.class), any(User.class));
    }

    @Test
    void testGetUserCards_Success() {
        // Setup
        String token = "user-12345";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");

        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card("Card1", 10.0, "MONSTER"));
        cards.add(new Card("Card2", 20.0, "SPELL"));

        when(userRepository.findUserByUsername("user")).thenReturn(user);
        when(cardPackageRepository.findCardsByUsername(user.getId())).thenReturn(cards);

        // Act
        ArrayList<Card> userCards = cardPackageService.getUserCards(token);

        // Assert
        assertNotNull(userCards);
        assertEquals(2, userCards.size());
        assertEquals("Card1", userCards.get(0).getName());
        assertEquals("Card2", userCards.get(1).getName());
        verify(cardPackageRepository, times(1)).findCardsByUsername(user.getId());
    }
}
