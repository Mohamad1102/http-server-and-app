package org.example.application.game.service;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.TradingDeal;
import org.example.application.game.entity.User;
import org.example.application.game.exception.SQLException;
import org.example.application.game.repository.CardPackageRepository;
import org.example.application.game.repository.TradingDealRepository;
import org.example.application.game.repository.UserDbRepository;

import java.util.List;
import java.util.UUID;

public class TradeService {

    private TradingDealRepository tradingDealRepo;
    private UserDbRepository userRepository;
    private CardPackageRepository cardPackageRepo;

    // Konstruktor-Injektion der Repositories
    public TradeService(TradingDealRepository tradingDealRepo, UserDbRepository userRepository, CardPackageRepository cardPackageRepo) {
        this.tradingDealRepo = tradingDealRepo;
        this.userRepository = userRepository;
        this.cardPackageRepo = cardPackageRepo;
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

        // Benutzer-ID anhand des Benutzernamens abrufen
        UUID userId = userRepository.getUserIdByUsername(username);

        // Benutzer-ID im TradingDeal setzen
        tradingDeal.setUserId(userId);

        // Validierung des Handelsdeals
        validateTradingDeal(tradingDeal, userId);

        // Handelsdeal speichern
        tradingDealRepo.createTradingDeal(tradingDeal);
    }

    private void validateTradingDeal(TradingDeal tradingDeal, UUID userId) throws SQLException {
        if (tradingDeal.getId() == null || tradingDeal.getCardToTrade() == null ||
                tradingDeal.getType() == null || tradingDeal.getMinimumDamage() <= 0) {
            throw new IllegalArgumentException("Invalid trading deal data");
        }

        // Prüfen, ob die Karte im Deck des Benutzers enthalten ist
        if (tradingDealRepo.isCardInDeck(userId, tradingDeal.getCardToTrade())) {
            throw new IllegalArgumentException("The card to trade is currently in the user's deck and cannot be traded.");
        }
    }

    public List<TradingDeal> getAvailableTrades(String token) {
        // 1. Token prüfen (ob es null oder leer ist)
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("User not logged in! No token provided.");
        }

        // 2. Benutzername aus dem Token extrahieren
        String username = extractUsernameFromToken(token);

        // 3. Benutzer-Objekt aus der Datenbank laden
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        try {
            // 4. Alle verfügbaren Trading-Deals abrufen
            return tradingDealRepo.getAllTradingDeals();
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving trading deals", e);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTradingDeal(UUID tradeId, String token) {
        // 1. Token prüfen
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("User not logged in! No token provided.");
        }

        // 2. Benutzername aus dem Token extrahieren
        String username = extractUsernameFromToken(token);

        // 3. Benutzer validieren
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // 4. Trading-Deal löschen
        try {
            boolean deleted = tradingDealRepo.deleteById(tradeId);
            if (!deleted) {
                throw new IllegalArgumentException("Trading deal not found or could not be deleted");
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void performTrade(UUID tradingDealId, UUID userCardId, String token) throws SQLException, java.sql.SQLException {
        // 1. Token prüfen
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("User not logged in! No token provided.");
        }

        // 2. Benutzername aus dem Token extrahieren
        String username = extractUsernameFromToken(token);

        // 3. Benutzer validieren
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // 4. Trading-Deal validieren
        TradingDeal deal = tradingDealRepo.getTradingDealById(tradingDealId);
        if (deal == null) {
            throw new IllegalArgumentException("Trading deal not found");
        }

        // 5. Benutzerkarte validieren
        boolean userOwnsCard = cardPackageRepo.isCardOwnedByUser(userCardId, user.getId());
        if (!userOwnsCard) {
            throw new IllegalArgumentException("User card not found or does not belong to the user");
        }

        // Karte des Benutzers aus der Datenbank abrufen
        Card userCard = cardPackageRepo.findCardById(userCardId);

        // 6. Mindestschaden und Kartentyp prüfen
        if (userCard.getDamage() < deal.getMinimumDamage()) {
            throw new IllegalArgumentException("User card damage is less than the minimum required damage for the trade.");
        }

        if (!userCard.getCardType().equalsIgnoreCase(deal.getType())) {
            throw new IllegalArgumentException("User card type does not match the required card type for the trade.");
        }

        // 7. Karte aus dem Trading-Deal prüfen
        boolean dealCardExists = cardPackageRepo.isCardOwnedByUser(deal.getCardToTrade(), deal.getUserId());
        if (!dealCardExists) {
            throw new IllegalArgumentException("Card in trading deal not found or does not belong to the trading user");
        }

        // 8. Überprüfen, ob beide Karten unterschiedliche Besitzer haben
        if (deal.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("Cannot trade: Both cards belong to the same user");
        }

        // 9. Karten tauschen
        tradingDealRepo.executeTrade(deal, userCard);
    }
}
