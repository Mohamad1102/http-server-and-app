package org.example.application.game.service;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.User;
import org.example.application.game.repository.BattleRepository;
import org.example.application.game.repository.DeckDbRepository;
import org.example.application.game.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BattleServiceTest {

    private BattleService battleService;
    private BattleRepository battleRepositoryMock;
    private UserRepository userRepositoryMock;
    private DeckDbRepository deckRepositoryMock;

    @BeforeEach
    void setUp() {
        battleRepositoryMock = mock(BattleRepository.class);
        userRepositoryMock = mock(UserRepository.class);
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
    void testDamageCalculationWaterAgainstFire() {
        // Arrange
        Card waterCard = new Card("Water Spell", 10.0, "SPELL");
        Card fireCard = new Card("Fire Spell", 5.0, "SPELL");

        // Act
        double damage = battleService.calculateDamage(waterCard, fireCard);

        // Assert
        // Fire is not effective against Water, so damage should be halved
        assertEquals(5.0, damage, "Damage should be halved due to the ineffectiveness of fire against water");
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

    @Test
    void testKnightVsWaterSpell() {
        // Arrange
        Card knightCard = new Card("Knight", 30.0, "MONSTER");
        Card waterSpellCard = new Card("Water Spell", 20.0, "SPELL");

        // Act
        double damage = battleService.calculateDamage(knightCard, waterSpellCard);

        // Assert
        // Water spells make knights drown instantly, so the knight should take max damage
        assertEquals(Double.MAX_VALUE, damage, "Knights drown instantly by water spells");
    }

    @Test
    void testKrakenVsSpell() {
        // Arrange
        Card krakenCard = new Card("Kraken", 50.0, "MONSTER");
        Card fireSpellCard = new Card("Fire Spell", 25.0, "SPELL");

        // Act
        double damage = battleService.calculateDamage(krakenCard, fireSpellCard);

        // Assert
        // Kraken is immune to spells, so damage should be 0
        assertEquals(0.0, damage, "Kraken is immune to spells, so no damage is dealt");
    }

    @Test
    void testFireElfVsDragon() {
        // Arrange
        Card fireElfCard = new Card("Fire Elf", 20.0, "MONSTER");
        Card dragonCard = new Card("Dragon", 30.0, "MONSTER");

        // Act
        double damage = battleService.calculateDamage(fireElfCard, dragonCard);

        // Assert
        // Fire Elves evade Dragon attacks, so damage should be 0
        assertEquals(0.0, damage, "Fire Elves can evade Dragon attacks");
    }

    @Test
    void testBattleBetweenTwoPlayers() throws InterruptedException {
        // Arrange
        String username1 = "Player1";
        String username2 = "Player2";
        Card card1 = new Card("Fire Spell", 10.0, "SPELL");
        Card card2 = new Card("Water Spell", 5.0, "SPELL");

        User player1 = new User(UUID.randomUUID(), username1, "password", 100, "John Doe", "Bio", "image.jpg");
        User player2 = new User(UUID.randomUUID(), username2, "password", 100, "Jane Doe", "Bio", "image.jpg");

        List<Card> player1Cards = new ArrayList<>();
        player1Cards.add(card1);
        List<Card> player2Cards = new ArrayList<>();
        player2Cards.add(card2);

        when(userRepositoryMock.findUserByUsername(username1)).thenReturn(player1);
        when(userRepositoryMock.findUserByUsername(username2)).thenReturn(player2);
        when(battleRepositoryMock.getDeckForUser(player1.getId())).thenReturn(player1Cards);
        when(battleRepositoryMock.getDeckForUser(player2.getId())).thenReturn(player2Cards);

        // Act
        String result = battleService.startBattle(username1);

        // Assert
        // Here we are testing that the battle starts, and the result is based on the card interaction.
        assertNotNull(result, "Battle result should not be null");

        // Timeout hinzufügen, um zu verhindern, dass der Test hängen bleibt
         result = battleService.startBattle("player1");
        assertNotNull(result, "Battle result should not be null");
    }
}
