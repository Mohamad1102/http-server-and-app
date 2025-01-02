package org.example.application.game.service;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.Package;
import org.example.application.game.entity.User;
import org.example.application.game.repository.CardPackageRepository;
import org.example.application.game.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class CardPackageService {

    private final CardPackageRepository cardPackageRepository;
    private final UserRepository userRepository;


    // Konstruktor-Injektion
    public CardPackageService(CardPackageRepository cardPackageRepository, UserRepository userRepository) {
        this.cardPackageRepository = cardPackageRepository;
        this.userRepository = userRepository;
    }

    private String extractUsernameFromToken(String token) {
        if (token == null || !token.contains("-")) {
            throw new IllegalArgumentException("Invalid token format");
        }
        return token.split("-")[0];
    }

    public boolean createPackage(List<Card> cards) {
        if (cards.size() != 5) {
            throw new IllegalArgumentException("Es müssen genau 5 Karten im Paket sein.");
        }
        // Paket erstellen und speichern
        Package cardPackage = new Package(cards);  // Vermeide java.lang.Package
        cardPackageRepository.savePackage(cardPackage);
        return true;
    }
    public boolean buyPackage(String token) {
        // 1. Token prüfen
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("User not logged in! No token provided.");
        }

        // 2. Benutzername aus Token extrahieren
        String username = extractUsernameFromToken(token);
        System.out.println("USERNAME IS: " + username);

        // 3. Benutzer validieren
        boolean userLoggedIn = userRepository.findByUsername(username);
        if (!userLoggedIn) {
            throw new IllegalArgumentException("User not logged in or invalid username!");
        }

        // 4. Benutzer-Objekt aus der Datenbank laden
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        System.out.println("Aktuelle Coins aus der Datenbank: " + user.getCoins());

        // 5. Coins prüfen
        if (user.getCoins() < 5) {
            throw new IllegalArgumentException("Not enough coins!");
        }

        // 6. Coins abziehen und lokal aktualisieren
        user.deductCoins(5);
        System.out.println("Coins nach Abzug: " + user.getCoins());

        // 7. Coins in der Datenbank aktualisieren
        userRepository.updateCoins(username, user.getCoins());

        // 8. Paket prüfen und zuweisen
        Optional<Package> packageOpt = cardPackageRepository.findAvailablePackage();
        if (packageOpt.isEmpty()) {
            throw new IllegalArgumentException("No packages available!");
        }
        Package cardPackage = packageOpt.get();
        cardPackageRepository.assignPackageToUser(cardPackage, user); // Benutzer mit aktualisierten Coins übergeben
        cardPackageRepository.removePackage(cardPackage); // Paket entfernen

        return true;
    }

}
