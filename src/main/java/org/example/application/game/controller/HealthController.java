package org.example.application.game.controller;

import org.example.application.game.dto.Message;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

public class HealthController extends Controller {
    @Override
    public Response handle(Request request) {
        return json(Status.OK, new Message("health"));
    }
}
