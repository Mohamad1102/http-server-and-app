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

    public boolean createPackage(List<Card> cards) {
        if (cards.size() != 5) {
            throw new IllegalArgumentException("Es müssen genau 5 Karten im Paket sein.");
        }
        // Paket erstellen und speichern
        Package cardPackage = new Package(cards);  // Vermeide java.lang.Package
        cardPackageRepository.savePackage(cardPackage);
        return true;
    }
    public boolean buyPackage(String username) {
        // Überprüfen, ob der Benutzer existiert
        boolean userExists = userRepository.findByUsername(username);
        if (!userExists) {
            System.out.println("Ich bin "+username);
            throw new IllegalArgumentException("User not logged in or invalid username!");
        }

        // Benutzer aus der Liste finden
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Benutzer-Münzen prüfen
        if (user.getCoins() < 5) {
            throw new IllegalArgumentException("Not enough coins!");
        }

        // Verfügbarkeit des Pakets prüfen
        Optional<Package> packageOpt = cardPackageRepository.findAvailablePackage();
        if (packageOpt.isEmpty()) {
            throw new IllegalArgumentException("No packages available!");
        }

        // Paket kaufen
        Package cardPackage = packageOpt.get();
        user.deductCoins(5); // Münzen abziehen
        userRepository.save(user); // Benutzer aktualisieren
        cardPackageRepository.assignPackageToUser(cardPackage, user); // Paket dem Benutzer zuweisen

        return true;
    }
}
