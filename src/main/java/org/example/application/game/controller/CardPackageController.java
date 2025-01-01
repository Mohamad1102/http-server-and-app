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
            String token = request.getHeader("Authorization");

            // Paket kaufen
            boolean isPurchased = cardPackageService.buyPackage(token);

            System.out.println("BUY PAKAGE!!!!");
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

    // Hilfsmethode für JSON-Antworten
    private Response json(Status status, String body) {
        Response response = new Response();
        response.setStatus(status);
        response.setBody(body);
        return response;
    }
}
