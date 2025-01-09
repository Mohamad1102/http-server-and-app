package org.example.application.game.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.application.game.entity.Card;
import org.example.application.game.service.DeckService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.util.ArrayList;
import java.util.UUID;

public class DeckController extends Controller {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    public Response handle(Request request) {
        if (request.getMethod().equals(Method.PUT)) {
            if (request.getPath().equals("/deck")) {
                System.out.println("!!CREATE DECK!!");
                // Anfrage für das Erstellen eines neuen Pakets
                return createDeck(request);
            }
        } else if (request.getMethod().equals(Method.GET)) {
            if (request.getPath().equals("/deck")) {
                return getDeck(request);
            }else if(request.getPath().equals("/deck?format=plain")){
                return getDeckPlain(request);
            }
        }
        return json(Status.METHODE_NOT_FOUND, "{\"error\": \"Method or path not allowed\"}"); // TODO METHOD NOT FOUND
    }

    private String extractTokenFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        return authHeader.substring(7);
    }

    private Response createDeck(Request request){

        try {
            // Karten aus der Anfrage lesen
            ArrayList<UUID> cardsID = super.fromBody(request.getBody(), new TypeReference<ArrayList<UUID>>() {});

            // Token aus dem Header extrahieren
            String authHeader = request.getHeader("Authorization");
            String token = extractTokenFromAuthHeader(authHeader);

            System.out.println("Eingeloggte User is " + token);

            // Paket erstellen
            UUID deckID = deckService.createDeck(cardsID, token);

            // Erfolgreiche Antwort zurückgeben
            return json(Status.CREATED, "{\"deck_ID\": \"" + deckID + "\"}");
        } catch (IllegalArgumentException e) {
            return json(Status.NOT_FOUND, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response getDeck(Request request)
    {
        try {
            // Token aus dem Header extrahieren
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return json(Status.UNAUTHORIZED, "{\"error\": \"Authorization header is missing or invalid\"}");
            }

            // Extrahiere den Token ohne "Bearer "
            String token = authHeader.substring(7);
            System.out.println("The USERNAME: " + token);

            // Benutzerkarten über den Service abrufen
            ArrayList<Card> userDeck = deckService.getDeck(token);

            // Wenn keine Karten vorhanden sind
            if (userDeck == null) {
                return json(Status.NOT_FOUND, "{\"error\": \"No cards found for this user\"}");
            }

            // Erfolgreiche Antwort mit den Karten des Benutzers
            return json(Status.OK, userDeck);  // json mit den Karten des Benutzers

        } catch (IllegalArgumentException e) {
            // Fehlerbehandlung bei ungültigem Token oder anderen Fehlern
            return json(Status.CONFLICT, "{\"error\": \"" + e.getMessage() + "\"}");
        }

    }

    private Response getDeckPlain(Request request) {
        try {
            // Token aus dem Header extrahieren
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return json(Status.UNAUTHORIZED, "{\"error\": \"Authorization header is missing or invalid\"}");
            }

            // Extrahiere den Token ohne "Bearer "
            String token = authHeader.substring(7);
            System.out.println("The USERNAME: " + token);

            // Benutzerkarten über den Service abrufen
            ArrayList<Card> userDeck = deckService.getDeckPlain(token);

            // Wenn keine Karten vorhanden sind
            if (userDeck == null) {
                return json(Status.NOT_FOUND, "{\"error\": \"No cards found for this user\"}");
            }

            // StringBuilder, um die UUIDs als Plain Text zu speichern
            StringBuilder myCards = new StringBuilder();

            // Durchlaufen des userDecks und Hinzufügen jeder UUID als String
            for (Card cardId : userDeck) {
                myCards.append(cardId.toString()).append("\n");  // Jede UUID in eine neue Zeile einfügen
            }

            // Erfolgreiche Antwort mit den UUIDs als Plain Text
            Response response = new Response();
            response.setStatus(Status.OK);
            response.setBody(myCards.toString());
            return response;

        } catch (IllegalArgumentException e) {
            // Fehlerbehandlung bei ungültigem Token oder anderen Fehlern
            return json(Status.CONFLICT, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}
