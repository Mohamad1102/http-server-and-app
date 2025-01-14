package org.example.application.game.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.application.game.entity.TradingDeal;
import org.example.application.game.repository.TradingDealRepository;
import org.example.application.game.repository.UserRepository;
import org.example.application.game.service.TradeService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.sql.*;
import java.util.UUID;

public class TradeController extends Controller {

    private TradeService tradeService;
    private ObjectMapper objectMapper = new ObjectMapper();



    // Konstruktor-Injektion des TradeService
    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    public Response handle(Request request) {
        if (request.getMethod().equals(Method.POST)) {
            if (request.getPath().equals("/tradings")) {
                System.out.println("!!CREATE TREADING DEAL!!");
                // Anfrage für das Erstellen eines neuen Pakets
                return createTradingDeal(request);
            } else if (request.getPath().startsWith("/tradings/")) {
                String[] pathParts = request.getPath().split("/");
                if (pathParts.length == 3) {
                    String tradingDealIdStr = pathParts[2];
                    try {
                        UUID tradingDealId = UUID.fromString(tradingDealIdStr);
                        //return carryTrade(request, tradingDealId); // Übergabe an CarryTrade-Methode
                    } catch (IllegalArgumentException e) {
                        return json(Status.BAD_REQUEST, "{\"error\": \"Invalid trading deal ID format\"}");
                    }
                }
            }
        } else if (request.getMethod().equals(Method.GET)) {
            if (request.getPath().equals("/tradings")) {
                // return getAvailableTrades(request);
            }
        } else if (request.getMethod().equals(Method.DELETE)) {
            if (request.getPath().startsWith("/tradings/")) {
                String[] pathParts = request.getPath().split("/");
                if (pathParts.length == 3) {
                    String tradingDealIdStr = pathParts[2];
                    try {
                        UUID tradingDealId = UUID.fromString(tradingDealIdStr);
                        // return deleteTradeDeal(tradingDealId); // Übergabe an deleteTradeDeal-Methode
                    } catch (IllegalArgumentException e) {
                        return json(Status.BAD_REQUEST, "{\"error\": \"Invalid trading deal ID format\"}");
                    }
                }
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
            System.out.println("create Trade 1");
            TypeReference<TradingDeal> typeRef = new TypeReference<TradingDeal>() {
            };
            System.out.println("create Trade 2");
            TradingDeal tradingDeal = objectMapper.readValue(request.getBody(), TradingDeal.class);

            System.out.println("create Trade 3");

            // Token aus dem Header extrahieren
            String authHeader = request.getHeader("Authorization");
            System.out.println("create Trade 4");
            String token = extractTokenFromAuthHeader(authHeader);

            System.out.println("token is: " + token);

            // Service aufrufen, um den Handelsdeal zu erstellen
            tradeService.createTradingDeal(tradingDeal, token);

            // Erfolgreiche Antwort zurückgeben
            return json(Status.CREATED, "{\"message\": \"Trading deal created successfully\", \"tradingDealId\": \"" + tradingDeal.getId() + "\"}");
        } catch (IllegalArgumentException e) {
            return json(Status.BAD_REQUEST, "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
