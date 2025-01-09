package org.example.application.game.service;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.Package;
import org.example.application.game.entity.User;
import org.example.application.game.repository.CardPackageRepository;
import org.example.application.game.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CardPackageServiceTest {

    @Test
    public void assign_Package_to_User() {
        // Arrange: Mock-Objekte erstellen
        CardPackageRepository cardPackageRepository = mock(CardPackageRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        CardPackageService cardPackageService = new CardPackageService(cardPackageRepository, userRepository);

        // Benutzer erstellen
        User user = new User("1", "TestUser", "password123", 20);

        // Karten und Paket erstellen
        Card card1 = new Card("Fire Dragon", 50, Card.CardType.MONSTER);
        Card card2 = new Card("Water Blast", 30, Card.CardType.SPELL);
        Card card3 = new Card("Earth Golem", 40, Card.CardType.MONSTER);
        Card card4 = new Card("Wind Slash", 20, Card.CardType.SPELL);
        Card card5 = new Card("Inferno Phoenix", 60, Card.CardType.MONSTER);

        List<Card> cards = Arrays.asList(card1, card2, card3, card4, card5);
        Package cardPackage = new Package(cards);

        // Mock-Verhalten definieren
        doNothing().when(cardPackageRepository).assignPackageToUser(cardPackage, user);

        // Act: Methode aufrufen
        cardPackageService.assignPackageToUser(cardPackage, user);

        // Assert: Überprüfen, ob die Methode aufgerufen wurde
        verify(cardPackageRepository, times(1)).assignPackageToUser(cardPackage, user);
    }
}
