package org.example.application.game.controller;

import com.fasterxml.jackson.databind.MapperFeature;
import org.example.application.game.exception.InvalidBodyException;
import org.example.application.game.exception.JsonParserException;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Controller {
    private final ObjectMapper objectMapper;

    public Controller() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(
                MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true
        );
    }

    public abstract Response handle(Request request) throws UserAlreadyExistsException;

    protected <T> T fromBody(String body, Class<T> type) {
        try {
            return objectMapper.readValue(body, type);
        } catch (JsonProcessingException e) {
            throw new InvalidBodyException(e);
        }
    }

    protected Response json(Status status, Object object) {
        Response response = new Response();
        response.setStatus(status);
        response.setHeader("Content-Type", "application/json");
        try {
            response.setBody(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new JsonParserException(e);
        }

        return response;
    }
}
