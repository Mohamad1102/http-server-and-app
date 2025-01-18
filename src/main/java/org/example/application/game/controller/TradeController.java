package org.example.application.game.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.application.game.entity.TradingDeal;
import org.example.application.game.service.TradeService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class TradeController extends Controller {

    private TradeService tradeService;



    // Konstruktor-Injektion des TradeService
    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    public Response handle(Request request) {
        if (request.getMethod().equals(Method.POST)) {
            if (request.getPath().equals("/tradings")) {
                // Anfrage für das Erstellen eines neuen Pakets
                return createTradingDeal(request);
            } else if (request.getPath().startsWith("/tradings/")) {
                String[] pathParts = request.getPath().split("/");
                if (pathParts.length == 3)
                    {
                        return trading(request);
                    }
            }
        } else if (request.getMethod().equals(Method.GET)) {
            if (request.getPath().equals("/tradings")) {
                return getAvailableTrades(request);
            }
        } else if (request.getMethod().equals(Method.DELETE)) {
            if (request.getPath().startsWith("/tradings/")) {
                return deleteTradeDeal(request);
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

    private Response createTradingDeal(Request request) {
        try {
            TypeReference<TradingDeal> typeRef = new TypeReference<TradingDeal>() {
            };
            System.out.println(request.getBody());
            TradingDeal tradingDeal = fromBody(request.getBody(), typeRef);

            // Token aus dem Header extrahieren
            String authHeader = request.getHeader("Authorization");
            String token = extractTokenFromAuthHeader(authHeader);

            // Service aufrufen, um den Handelsdeal zu erstellen
            tradeService.createTradingDeal(tradingDeal, token);

            // Erfolgreiche Antwort zurückgeben
            return json(Status.CREATED, "{\"message\": \"Trading deal created successfully\", \"tradingDealId\": \"" + tradingDeal.getId() + "\"}");
        } catch (IllegalArgumentException e) {
            return json(Status.BAD_REQUEST, "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Response getAvailableTrades(Request request) {
        try {
            // Token aus dem Header extrahieren
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return json(Status.UNAUTHORIZED, "{\"error\": \"Authorization header is missing or invalid\"}");
            }

            // Extrahiere den Token ohne "Bearer "
            String token = authHeader.substring(7);

            // Alle verfügbaren Trading-Deals über den Service abrufen
            List<TradingDeal> availableTrades = tradeService.getAvailableTrades(token);

            // Wenn keine Trading-Deals vorhanden sind
            if (availableTrades == null) {
                return json(Status.NOT_FOUND, "");
            }

            // Erfolgreiche Antwort mit den verfügbaren Trading-Deals
            return json(Status.OK, availableTrades); // json mit den Trading-Deals
        } catch (IllegalArgumentException e) {
            // Fehlerbehandlung bei ungültigem Token oder anderen Fehlern
            return json(Status.CONFLICT, "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (RuntimeException e) {
            // Allgemeine Fehlerbehandlung
            return json(Status.INTERNAL_SERVER_ERROR, "{\"error\": \"An error occurred while retrieving trading deals\"}");
        }
    }

    private Response deleteTradeDeal(Request request) {
        try {
            // 1. Extrahiere den Token aus dem Authorization-Header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return json(Status.UNAUTHORIZED, "{\"error\": \"Authorization header is missing or invalid\"}");
            }
            String token = authHeader.substring(7);

            // 2. Extrahiere die Trade-ID aus der URL
            String[] pathParts = request.getPath().split("/");
            if (pathParts.length != 3) {
                return json(Status.BAD_REQUEST, "{\"error\": \"Invalid trading deal ID in the URL\"}");
            }
            String tradeIdStr = pathParts[2];

            // 3. Konvertiere die Trade-ID zu UUID
            UUID tradeId;
            try {
                tradeId = UUID.fromString(tradeIdStr);
            } catch (IllegalArgumentException e) {
                return json(Status.BAD_REQUEST, "{\"error\": \"Invalid UUID format for trading deal ID\"}");
            }

            // 4. Service-Methode aufrufen, um den Trading-Deal zu löschen
            tradeService.deleteTradingDeal(tradeId, token);

            // 5. Erfolgreiche Antwort zurückgeben
            return json(Status.OK, "{\"message\": \"Trading deal deleted successfully\"}");
        } catch (IllegalArgumentException e) {
            // Fehler bei Token oder ungültigen Eingaben
            return json(Status.BAD_REQUEST, "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (RuntimeException e) {
            // Allgemeine Fehlerbehandlung
            return json(Status.INTERNAL_SERVER_ERROR, "{\"error\": \"An error occurred while deleting the trading deal\"}");
        }
    }

    private Response trading(Request request) {
        try {
            // 1. Extrahiere den Token aus dem Authorization-Header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return json(Status.UNAUTHORIZED, "{\"error\": \"Authorization header is missing or invalid\"}");
            }
            String token = authHeader.substring(7);

            // 2. Extrahiere die Trading-Deal-ID aus der URL
            String[] pathParts = request.getPath().split("/");
            if (pathParts.length != 3) {
                return json(Status.BAD_REQUEST, "{\"error\": \"Invalid trading deal ID in the URL\"}");
            }
            String tradingDealIdStr = pathParts[2];

            // 3. Konvertiere die Trading-Deal-ID zu UUID
            UUID tradingDealId;
            try {
                tradingDealId = UUID.fromString(tradingDealIdStr);
            } catch (IllegalArgumentException e) {
                return json(Status.BAD_REQUEST, "{\"error\": \"Invalid UUID format for trading deal ID\"}");
            }

            // 4. Extrahiere die User-Card-ID aus dem Body
            String cardIdStr = request.getBody();
            if (cardIdStr == null || cardIdStr.isEmpty()) {
                return json(Status.BAD_REQUEST, "{\"error\": \"User card ID is missing in the request body\"}");
            }

            UUID userCardId;
            try {
                userCardId = UUID.fromString(cardIdStr.replace("\"", "").trim());
            } catch (IllegalArgumentException e) {
                return json(Status.BAD_REQUEST, "{\"error\": \"Invalid UUID format for user card ID\"}");
            }

            // 5. Service-Methode aufrufen, um den Handel durchzuführen
            tradeService.performTrade(tradingDealId, userCardId, token);

            // 6. Erfolgreiche Antwort zurückgeben
            return json(Status.OK, "{\"message\": \"Trade executed successfully\"}");
        } catch (IllegalArgumentException e) {
            // Fehler bei ungültigem Token, Trading-Deal-ID oder User-Card-ID
            return json(Status.BAD_REQUEST, "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (RuntimeException e) {
            // Allgemeine Fehlerbehandlung
            return json(Status.INTERNAL_SERVER_ERROR, "{\"error\": \"An error occurred while executing the trade\"}");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
