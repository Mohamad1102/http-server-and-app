package org.example.application.game.service;

import org.example.application.game.entity.TradingDeal;
import org.example.application.game.repository.TradingDealRepository;
import org.example.application.game.repository.UserRepository;
import org.example.application.game.repository.CardPackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TradeServiceTest {

    private TradeService tradeService;
    private TradingDealRepository tradingDealRepo;
    private UserRepository userRepository;
    private CardPackageRepository cardPackageRepo;

    @BeforeEach
    void setUp() {
        tradingDealRepo = mock(TradingDealRepository.class);
        userRepository = mock(UserRepository.class);
        cardPackageRepo = mock(CardPackageRepository.class);

        tradeService = new TradeService(tradingDealRepo, userRepository, cardPackageRepo);
    }

    @Test
    void createTradingDeal_CardInDeck_ThrowsException() throws SQLException {
        // Arrange
        String token = "user-12345";
        UUID userId = UUID.randomUUID();
        UUID cardToTrade = UUID.randomUUID();
        TradingDeal tradingDeal = new TradingDeal();
        tradingDeal.setId(UUID.randomUUID());
        tradingDeal.setCardToTrade(cardToTrade);
        tradingDeal.setTradeType("SPELL");
        tradingDeal.setMinimumDamage(10.0);

        // Mocking
        when(userRepository.getUserIdByUsername("user")).thenReturn(userId);
        when(tradingDealRepo.isCardInDeck(userId, cardToTrade)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tradeService.createTradingDeal(tradingDeal, token),
                "Expected exception for card being in the deck");

        // Verify interactions
        verify(userRepository).getUserIdByUsername("user");
        verify(tradingDealRepo).isCardInDeck(userId, cardToTrade);
        verifyNoMoreInteractions(tradingDealRepo);
    }

    @Test
    void createTradingDeal_InvalidTradingDeal_ThrowsException() throws SQLException {
        // Arrange
        String token = "user-12345";
        UUID userId = UUID.randomUUID();
        TradingDeal tradingDeal = new TradingDeal(); // Invalid because fields are null or zero

        // Mocking
        when(userRepository.getUserIdByUsername("user")).thenReturn(userId);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tradeService.createTradingDeal(tradingDeal, token),
                "Expected exception for invalid trading deal");

        // Verify interactions
        verify(userRepository).getUserIdByUsername("user");
        verifyNoMoreInteractions(tradingDealRepo);
    }

    @Test
    void createTradingDeal_ValidTradingDeal_Success() throws SQLException {
        // Arrange
        String token = "user-12345";
        UUID userId = UUID.randomUUID();
        UUID cardToTrade = UUID.randomUUID();
        TradingDeal tradingDeal = new TradingDeal();
        tradingDeal.setId(UUID.randomUUID());
        tradingDeal.setCardToTrade(cardToTrade);
        tradingDeal.setTradeType("MONSTER");
        tradingDeal.setMinimumDamage(50.0);

        // Mocking
        when(userRepository.getUserIdByUsername("user")).thenReturn(userId);
        when(tradingDealRepo.isCardInDeck(userId, cardToTrade)).thenReturn(false);

        // Act
        tradeService.createTradingDeal(tradingDeal, token);

        // Assert
        verify(userRepository).getUserIdByUsername("user");
        verify(tradingDealRepo).isCardInDeck(userId, cardToTrade);
        verify(tradingDealRepo).createTradingDeal(tradingDeal);
    }
}
