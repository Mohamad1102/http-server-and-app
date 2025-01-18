package org.example.application.game.service;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.Package;
import org.example.application.game.entity.User;
import org.example.application.game.repository.CardPackageRepository;
import org.example.application.game.repository.UserDbRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class CardPackageService {

    private final CardPackageRepository cardPackageRepository;
    private final UserDbRepository userRepository;

    // Konstruktor-Injektion
    public CardPackageService(CardPackageRepository cardPackageRepository, UserDbRepository userRepository) {
        this.cardPackageRepository = cardPackageRepository;
        this.userRepository = userRepository;
    }

    private String extractUsernameFromToken(String token) {
        if (token == null || !token.contains("-")) {
            throw new IllegalArgumentException("Invalid token format");
        }
        return token.split("-")[0];
    }

    // Methode, um ein neues Package zu erstellen (nur für admin)
    public UUID createPackage(ArrayList<Card> cards, String token) {
        // 1. Benutzer validieren
        String username = extractUsernameFromToken(token);
        User adminUser = userRepository.findUserByUsername(username);
        if (username == null) {
            throw new IllegalArgumentException("User not found");
        }

        // 2. Nur der Admin kann ein Paket erstellen
        if (!adminUser.getUsername().equals("admin")) {
            throw new IllegalArgumentException("Nur der Admin kann ein Paket erstellen.");
        }

        if (cards.size() != 5) {
            throw new IllegalArgumentException("Ein Paket muss genau 5 Karten enthalten.");
        }

        // 3. Paket erstellen
        UUID id = cardPackageRepository.createPackageForAdmin(cards, adminUser);
        return id;
    }

    // Methode, um ein Paket zu kaufen
    public boolean buyPackage(String token) {
        // 1. Token prüfen
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("User not logged in! No token provided.");
        }

        // 2. Benutzername aus Token extrahieren
        String username = extractUsernameFromToken(token);

        // 3. Benutzer validieren
        boolean userLoggedIn = userRepository.findByUsername(username);
        if (!userLoggedIn) {
            throw new IllegalArgumentException("User not logged in or invalid username!");
        }

        // 4. Benutzer-Objekt aus der Datenbank laden
        User user = userRepository.findUserByUsername(username);
        if (username == null) {
            throw new IllegalArgumentException("User not found");
        }

        // 5. Paket prüfen
        Optional<Package> packageOpt = cardPackageRepository.findAvailablePackage();
        if (packageOpt.isEmpty()) {
            throw new IllegalArgumentException("No packages available!");
        }

        Package cardPackage = packageOpt.get();

        // 6. Coins prüfen
        if (user.getCoins() < 5) {
            throw new IllegalArgumentException("Not enough coins!");
        }

        // 7. Coins abziehen und lokal aktualisieren
        user.deductCoins(5);

        // 8. Coins in der Datenbank aktualisieren
        userRepository.updateCoins(username, user.getCoins());

        // 9. Paket zuweisen
        cardPackageRepository.assignPackageToUser(cardPackage.getId(), user);

        return true;
    }
    public ArrayList<Card> getUserCards(String token) {
        // 1. Token prüfen (ob es null oder leer ist)
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("User not logged in! No token provided.");
        }

        // 2. Benutzername aus dem Token extrahieren
        String username = extractUsernameFromToken(token);

        // 3. Benutzer-Objekt aus der Datenbank laden
        User user = userRepository.findUserByUsername(username);
        if (username == null) {
            throw new IllegalArgumentException("User not found");
        }

        ArrayList<Card> mycards = cardPackageRepository.findCardsByUsername(user.getId());

        // 4. Alle Karten des Benutzers zurückgeben
        return mycards;  // Hier nehmen wir an, dass User eine Methode getCards hat, die alle zugeordneten Karten liefert.
    }
}
