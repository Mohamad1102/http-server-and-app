package org.example.application.game.controller;

import org.example.application.game.entity.UserElo;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.service.EloService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.util.List;
import java.util.Map;

public class EloController extends Controller{
    private final EloService eloService;
    public EloController(EloService eloService) {
        this.eloService = eloService;
    }
    @Override
    public Response handle(Request request) throws UserAlreadyExistsException {

        if (request.getMethod().equals(Method.GET)) {
            System.out.println("GETSCOREBORD");
            return getScoreBboard(request);
        }
        return null;
    }
    private Response getScoreBboard(Request request)
    {
        List<Map<String, Object>> scorebord = eloService.getScoreBord();
        System.out.println("LIST ERSTELLT");
        return json(Status.OK, scorebord);
    }
}
