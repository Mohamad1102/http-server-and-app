package org.example.application.game.service;

import org.example.application.game.entity.Card;
import org.example.application.game.repository.BattleRepository;
import org.example.application.game.repository.DeckDbRepository;
import org.example.application.game.repository.UserDbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BattleServiceTest {

    private BattleService battleService;
    private BattleRepository battleRepositoryMock;
    private UserDbRepository userRepositoryMock;
    private DeckDbRepository deckRepositoryMock;

    @BeforeEach
    void setUp() {
        battleRepositoryMock = mock(BattleRepository.class);
        userRepositoryMock = mock(UserDbRepository.class);
        deckRepositoryMock = mock(DeckDbRepository.class);
        battleService = new BattleService(battleRepositoryMock, userRepositoryMock, deckRepositoryMock);
    }

    @Test
    void testDamageCalculationFireAgainstWater() {
        // Arrange
        Card fireCard = new Card("Fire Spell", 10.0, "SPELL");
        Card waterCard = new Card("Water Spell", 5.0, "SPELL");

        // Act
        double damage = battleService.calculateDamage(fireCard, waterCard);

        // Assert
        // Water is effective against Fire, so damage should be doubled
        assertEquals(10.0, damage, "Damage should be doubled due to the effectiveness of water against fire");
    }

    @Test
    void testGoblinVsDragon() {
        // Arrange
        Card goblinCard = new Card("Goblin", 10.0, "MONSTER");
        Card dragonCard = new Card("Dragon", 20.0, "MONSTER");

        // Act
        double damage = battleService.calculateDamage(goblinCard, dragonCard);

        // Assert
        // Goblins are afraid of Dragons, so damage should be 0
        assertEquals(0.0, damage, "Goblins are too afraid of Dragons to attack");
    }

    @Test
    void testWizardVsOrk() {
        // Arrange
        Card wizardCard = new Card("Wizard", 15.0, "MONSTER");
        Card orkCard = new Card("Ork", 12.0, "MONSTER");

        // Act
        double damage = battleService.calculateDamage(wizardCard, orkCard);

        // Assert
        // Wizards can control Orks, so the Ork cannot do damage
        assertEquals(0.0, damage, "Wizards control Orks, so Ork cannot do damage");
    }
}