package org.example.application.game.controller;

import org.example.application.game.service.StatsService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;
import org.example.application.game.entity.UserStats;

import java.sql.SQLException;

public class StatsController extends Controller {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    public Response handle(Request request) throws SQLException {

        if (request.getMethod().equals(Method.GET)) {
            if (request.getPath().equals("/stats")) {
                return getUserStats(request);
            }
        }
        return null;
    }
    private String extractTokenFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        return authHeader.substring(7);
    }
    private Response getUserStats(Request request) throws SQLException {
        String authHeader = request.getHeader("Authorization");
        String token = extractTokenFromAuthHeader(authHeader);

        if (token == null) {
            return json(Status.UNAUTHORIZED, "{\"error\": \"UNAUTHORIZED User\"}");
        }

        System.out.println("Zur Service");
        UserStats stats = statsService.getUserStats(token);

        // Debugging-Ausgabe
        System.out.println("Statistik zurückgegeben: " + stats);

        // Immer eine Antwort zurückgeben
        return json(Status.OK, stats);
    }


}
