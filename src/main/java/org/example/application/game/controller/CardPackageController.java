package org.example.application.game.controller;

import org.example.application.game.entity.Card;
import org.example.application.game.service.CardPackageService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.util.List;

public class CardPackageController extends Controller {
    private final CardPackageService cardPackageService;

    public CardPackageController(CardPackageService cardPackageService) {
        this.cardPackageService = cardPackageService;
    }

    @Override
    public Response handle(Request request) {
        // Überprüfen, ob es sich um eine POST-Anfrage handelt
        if (request.getMethod().equals(Method.POST)) {
            if (request.getPath().equals("/packages")) {
                // Anfrage für das Erstellen eines neuen Pakets
                return createPackage(request);
            } else if (request.getPath().equals("/transactions/packages")) {
                // Anfrage für den Kauf eines Pakets
                return buyPackage(request);
            }
        }

        // Überprüfen, ob es sich um eine GET-Anfrage für Karten handelt
        if (request.getMethod().equals(Method.GET)) {
            if (request.getPath().equals("/cards")) {
                // Anfrage für das Abrufen der Karten eines Benutzers
                return getCards(request);
            }
        }

        // Für andere Anfragen (Methoden oder Pfade) eine Fehlerantwort zurückgeben
        return json(Status.NOT_FOUND, "{\"error\": \"Method or path not allowed\"}");
    }

    private Response createPackage(Request request) {
        try {
            // request --> Liste von Karten
            List<Card> cards = fromBody(request.getBody(), List.class);  // Deserialisierung von JSON zu List<Card>

            // Paket erstellen und speichern
            boolean isCreated = cardPackageService.createPackage(cards);

            if (isCreated) {
                System.out.println("IS CREATED!!");
                return json(Status.CREATED, "{\"message\": \"Card package successfully created\"}");

            } else {
                return json(Status.CONFLICT, "{\"error\": \"Conflict: Card package creation failed\"}");
            }
        } catch (IllegalArgumentException e) {
            // Falls eine Ausnahme geworfen wird (z.B. ungültige Kartenzahl)
            return json(Status.CONFLICT, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response buyPackage(Request request) {
        try {
            // Token aus dem Header extrahieren
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return json(Status.UNAUTHORIZED, "{\"error\": \"Authorization header is missing or invalid\"}");
            }

            // Extrahiere den Token ohne "Bearer "
            String token = authHeader.substring(7);

            // Paket kaufen (Token direkt verwenden)
            boolean isPurchased = cardPackageService.buyPackage(token);

            System.out.println("BUY PACKAGE!!!!");
            if (isPurchased) {
                return json(Status.CREATED, "{\"message\": \"Package successfully purchased\"}");
            } else {
                return json(Status.CONFLICT, "{\"error\": \"Package purchase failed\"}");
            }

        } catch (IllegalArgumentException e) {
            // Fehlerbehandlung bei ungültigem Token oder anderen Fehlern
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
            List<Card> userCards = cardPackageService.getUserCards(token);

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
