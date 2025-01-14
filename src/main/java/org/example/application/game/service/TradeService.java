package org.example.application.game.service;

import org.example.application.game.entity.TradingDeal;
import org.example.application.game.exception.SQLException;
import org.example.application.game.repository.CompletedTradeRepository;
import org.example.application.game.repository.TradingDealRepository;
import org.example.application.game.repository.UserRepository;

import java.util.UUID;

public class TradeService {

    private TradingDealRepository tradingDealRepo;
    private CompletedTradeRepository completedTradeRepo;
    private UserRepository userRepository;

    // Konstruktor-Injektion der Repositories
    public TradeService(TradingDealRepository tradingDealRepo, CompletedTradeRepository completedTradeRepo, UserRepository userRepository) {
        this.tradingDealRepo = tradingDealRepo;
        this.completedTradeRepo = completedTradeRepo;
        this.userRepository = userRepository;
    }

    private String extractUsernameFromToken(String token) {
        if (token == null || !token.contains("-")) {
            throw new IllegalArgumentException("Invalid token format");
        }
        return token.split("-")[0];
    }

    public void createTradingDeal(TradingDeal tradingDeal, String token) throws SQLException, java.sql.SQLException {
        // Benutzername aus dem Token extrahieren
        String username = extractUsernameFromToken(token);

        System.out.println("THE USERNAME IS: " + username);
        // Benutzer-ID anhand des Benutzernamens abrufen
        UUID userId = userRepository.getUserIdByUsername(username);

        // Benutzer-ID im TradingDeal setzen
        tradingDeal.setUserId(userId);

        // Validierung des Handelsdeals
        validateTradingDeal(tradingDeal);

        // Handelsdeal speichern
        tradingDealRepo.createTradingDeal(tradingDeal);
    }
    private void validateTradingDeal(TradingDeal tradingDeal) {
        if (tradingDeal.getId() == null || tradingDeal.getCardToTrade() == null ||
                tradingDeal.getTradeType() == null || tradingDeal.getMinimumDamage() <= 0) {
            throw new IllegalArgumentException("Invalid trading deal data");
        }
    }
}
