package org.example.application.game.controller;

import org.example.application.game.dto.Message;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.time.Duration;

public class WaitController extends Controller {
    @Override
    public Response handle(Request request) {
        try {
            Thread.sleep(Duration.ofSeconds(10));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return json(Status.OK, new Message("wait"));
    }
}