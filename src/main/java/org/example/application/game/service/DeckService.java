package org.example.application.game.service;

import org.example.application.game.controller.DeckController;
import org.example.application.game.entity.Card;
import org.example.application.game.entity.User;
import org.example.application.game.exception.BadRequestException;
import org.example.application.game.repository.CardPackageRepository;
import org.example.application.game.repository.DeckDbRepository;
import org.example.application.game.repository.UserRepository;

import java.util.ArrayList;
import java.util.UUID;

public class DeckService {

    private final DeckDbRepository deckRepository;

    private final UserRepository userRepository;
    private final CardPackageRepository cardPackageRepository;

    public DeckService(DeckDbRepository deckRepository, UserRepository userRepository, CardPackageRepository cardPackageRepository) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.cardPackageRepository = cardPackageRepository;
    }

    private String extractUsernameFromToken(String token) {
        if (token == null || !token.contains("-")) {
            throw new IllegalArgumentException("Invalid token format");
        }
        return token.split("-")[0];
    }

    public UUID createDeck(ArrayList<UUID> cardsID, String token)
    {
        String username = extractUsernameFromToken(token);
        User myUser = userRepository.findUserByUsername(username);
        if (myUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (cardsID.size() != 4) {
            throw new IllegalArgumentException("Ein Deck muss genau 4 Karten enthalten.");
        }

        ArrayList<Card> userCards = cardPackageRepository.findCardsByUsername(myUser.getId());
        for(UUID card : cardsID) {
            if (userCards.stream().noneMatch(c -> c.getId().equals(card))){
                throw new BadRequestException("BAD");
            }
        }
        UUID id = deckRepository.createDeck(cardsID, myUser);
        return id;
    }

    public ArrayList<Card> getDeck(String token) {
        // 1. Token prüfen (ob es null oder leer ist)
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("User not logged in! No token provided.");
        }

        // 2. Benutzername aus dem Token extrahieren
        String username = extractUsernameFromToken(token);

        System.out.println("USERNAME:" + username);
        // 3. Benutzer-Objekt aus der Datenbank laden
        User user = userRepository.findUserByUsername(username);
        if (username == null) {
            throw new IllegalArgumentException("User not found");
        }

        ArrayList<Card> deckCards = deckRepository.getDeck(user.getId());

        System.out.println("Deck geladen für Benutzer: " + username);

        System.out.println("NACH USERNAME");

        // 4. Alle Karten des Benutzers zurückgeben
        return deckCards;  // Hier nehmen wir an, dass User eine Methode getCards hat, die alle zugeordneten Karten liefert.
    }

    public ArrayList<Card> getDeckPlain(String token) {
        // 1. Token prüfen (ob es null oder leer ist)
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("User not logged in! No token provided.");
        }

        // 2. Benutzername aus dem Token extrahieren
        String username = extractUsernameFromToken(token);

        System.out.println("USERNAME:" + username);
        // 3. Benutzer-Objekt aus der Datenbank laden
        User user = userRepository.findUserByUsername(username);
        if (username == null) {
            throw new IllegalArgumentException("User not found");
        }

        ArrayList<Card> deckCards = deckRepository.getDeckPlain(user.getId());

        System.out.println("NACH USERNAME");

        // 4. Alle Karten des Benutzers zurückgeben
        return deckCards;  // Hier nehmen wir an, dass User eine Methode getCards hat, die alle zugeordneten Karten liefert.
    }
}
