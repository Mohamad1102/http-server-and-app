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
        if (request.getMethod().equals(Method.POST)) {
            return createPackage(request);
        }

        return null;  // Hier nur POST-Anfragen unterstützt, daher keine anderen Methoden behandeln
    }

    private Response createPackage(Request request) {
        try {
            // request --> Liste von Karten
            List<Card> cards = fromBody(request.getBody(), List.class);  // Deserialisierung von JSON zu List<Card>

            // Paket erstellen und speichern
            boolean isCreated = cardPackageService.createPackage(cards);

            if (isCreated) {
                return json(Status.CREATED, "{\"message\": \"Card package successfully created\"}");
            } else {
                return json(Status.CONFLICT, "{\"error\": \"Conflict: Card package creation failed\"}");
            }
        } catch (IllegalArgumentException e) {
            // Falls eine Ausnahme geworfen wird (z.B. ungültige Kartenzahl)
            return json(Status.CONFLICT, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
