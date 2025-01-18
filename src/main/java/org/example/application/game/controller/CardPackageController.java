package org.example.application.game.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.application.game.entity.Card;
import org.example.application.game.exception.CardPackageCreationException;
import org.example.application.game.service.CardPackageService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.util.ArrayList;
import java.util.UUID;

public class CardPackageController extends Controller {

    private final CardPackageService cardPackageService;

    public CardPackageController(CardPackageService cardPackageService) {
        this.cardPackageService = cardPackageService;
    }

    @Override
    public Response handle(Request request) {
        if (request.getMethod().equals(Method.POST)) {
            if (request.getPath().equals("/packages")) {
                // Anfrage für das Erstellen eines neuen Pakets
                return createPackage(request);
            } else if (request.getPath().equals("/transactions/packages")) {
                // Anfrage für den Kauf eines Pakets
                return buyPackage(request);
            }
        } else if (request.getMethod().equals(Method.GET)) {
            if (request.getPath().equals("/cards")) {
                return getCards(request);
            }
        }
        return json(Status.NOT_FOUND, "{\"error\": \"Method or path not allowed\"}");
    }

    private String extractTokenFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        return authHeader.substring(7);
    }

    private Response createPackage(Request request) throws CardPackageCreationException {
        try {
            // Karten aus der Anfrage lesen
            ArrayList<Card> cards = super.fromBody(request.getBody(), new TypeReference<ArrayList<Card>>() {
            });

            // Karten überprüfen und initialisieren
            for (Card card : cards) {
                if (card.getName() != null && card.getName().toLowerCase().endsWith("spell")) {
                    card.setCardType("Spell");
                } else {
                    card.setCardType("Monster");
                }
            }

            // Token aus dem Header extrahieren
            String authHeader = request.getHeader("Authorization");
            String token = extractTokenFromAuthHeader(authHeader);

            // Paket erstellen
            UUID packageId = cardPackageService.createPackage(cards, token);

            // Erfolgreiche Antwort zurückgeben
            return json(Status.CREATED, "{\"packageId\": \"" + packageId + "\"}");
        } catch (IllegalArgumentException e) {
            return json(Status.NOT_FOUND, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response buyPackage(Request request) {
        try {
            // Token aus dem Header extrahieren
            String authHeader = request.getHeader("Authorization");
            String token = extractTokenFromAuthHeader(authHeader);

            // Paket kaufen
            boolean isPurchased = cardPackageService.buyPackage(token);

            if (isPurchased) {
                return json(Status.CREATED, "{\"message\": \"Package successfully purchased\"}");
            } else {
                return json(Status.CONFLICT, "{\"error\": \"Package purchase failed\"}");
            }
        } catch (IllegalArgumentException e) {
            return json(Status.CONFLICT, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response getCards(Request request) {
        try {
            // Token aus dem Header extrahieren
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return json(Status.UNAUTHORIZED, "{\"error\": \"Authorization header is missing or invalid\"}");
            }

            // Extrahiere den Token ohne "Bearer "
            String token = authHeader.substring(7);

            // Benutzerkarten über den Service abrufen
            ArrayList<Card> userCards = cardPackageService.getUserCards(token);

            // Wenn keine Karten vorhanden sind
            if (userCards == null || userCards.isEmpty()) {
                return json(Status.NOT_FOUND, "{\"error\": \"No cards found for this user\"}");
            }

            // Erfolgreiche Antwort mit den Karten des Benutzers
            return json(Status.OK, userCards);  // json mit den Karten des Benutzers

        } catch (IllegalArgumentException e) {
            // Fehlerbehandlung bei ungültigem Token oder anderen Fehlern
            return json(Status.CONFLICT, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
