package org.example.application.game.controller;

import org.example.application.game.entity.User;
import org.example.application.game.service.BattleService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

public class BattleController extends Controller {
    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @Override
    public Response handle(Request request) throws InterruptedException {
        if (request.getMethod().equals(Method.POST)) {
            return startBattle(request);
        }
        return null;
    }

    private Response startBattle(Request request) throws InterruptedException {
        // Extrahiere den Benutzernamen aus dem Authorization-Header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return json(Status.UNAUTHORIZED, "{\"error\": \"Missing or invalid token\"}");
        }
        String token = authorizationHeader.substring("Bearer ".length());
        String username = token.replace("-mtcgToken", ""); // Token zu Username umwandeln

        // Battle-Request an den BattleService weiterleiten
        String result = battleService.startBattle(username);

        // Rückgabe der Antwort, ob der Benutzer wartet oder das Battle gestartet wurde
        return json(Status.OK, "{\"message\": \"" + result + "\"}");
    }

}
