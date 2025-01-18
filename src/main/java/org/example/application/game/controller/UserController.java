package org.example.application.game.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.application.game.entity.User;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.service.UserService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.util.ArrayList;


public class UserController extends Controller{
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Response handle(Request request) throws UserAlreadyExistsException {

        if (request.getMethod().equals(Method.POST)) {
            return create(request);
        }
        if (request.getMethod().equals(Method.GET)) {
            return getUserData(request);
        }
        if (request.getMethod().equals(Method.PUT)) {
            return updateUser(request);
        }
        return null;
    }

    private Response create(Request request) {
        // request --> user
        User user = fromBody(request.getBody(), new TypeReference<User>() {
        });
        user = userService.create(user);

        return json(Status.CREATED, user.getUsername() + " was successfully created!");
    }
    private Response getUserData(Request request) {
        // Benutzername aus der URL extrahieren
        String usernameFromUrl = request.getPath().substring(request.getPath().lastIndexOf("/") + 1);

        // Token aus den Headern extrahieren
        String authorizationHeader = request.getHeader("Authorization"); // Verwende getHeader statt getHeaders, da es sich um eine einzelne Header-Abfrage handelt
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return json(Status.UNAUTHORIZED, "{\"error\": \"Missing or invalid token\"}");
        }

        // Token verarbeiten und den Benutzernamen extrahieren
        String token = authorizationHeader.substring("Bearer ".length());
        String usernameFromToken = token.replace("-mtcgToken", ""); // Entferne das Präfix "-mtcgToken" aus dem Token

        // Überprüfung: Username im Token und URL müssen übereinstimmen
        if (!usernameFromToken.equals(usernameFromUrl)) {
            return json(Status.FORBIDDEN, "{\"error\": \"Token does not match username in URL\"}");
        }

        // Benutzer über den Service abrufen
        User user = userService.getUserData(usernameFromUrl);

        // Prüfen, ob der Benutzer existiert
        if (user != null) {
            return json(Status.OK, user); // Benutzer gefunden
        } else {
            return json(Status.NOT_FOUND, "{\"error\": \"User not found\"}"); // Benutzer nicht gefunden
        }
    }

    private Response updateUser(Request request) {
        // Überprüfung, ob der Body leer ist
        if (request.getBody() == null || request.getBody().trim().isEmpty()) {
            return json(Status.BAD_REQUEST, "{\"error\": \"Request body cannot be empty\"}");
        }

        // Benutzername aus der URL extrahieren
        String usernameFromUrl = request.getPath().substring(request.getPath().lastIndexOf("/") + 1);

        // Token aus den Headern extrahieren
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return json(Status.UNAUTHORIZED, "{\"error\": \"Missing or invalid token\"}");
        }

        // Token verarbeiten und den Benutzernamen extrahieren
        String token = authorizationHeader.substring("Bearer ".length());
        String usernameFromToken = token.replace("-mtcgToken", "");

        // Überprüfung: Username im Token und URL müssen übereinstimmen
        if (!usernameFromToken.equals(usernameFromUrl)) {
            return json(Status.FORBIDDEN, "{\"error\": \"Token does not match username in URL\"}");
        }

        // Benutzerobjekt aus dem Request-Body extrahieren
        User updatedUser = fromBody(request.getBody(), new TypeReference<User>() {});

        // Benutzer im Service aktualisieren
        try {
            User user = userService.updateUser(usernameFromUrl, updatedUser);
            if (user != null) {
                return json(Status.OK, "{\"message\": \"" + usernameFromUrl + " was successfully updated!\"}");
            } else {
                return json(Status.NOT_FOUND, "{\"error\": \"User not found\"}");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return json(Status.INTERNAL_SERVER_ERROR, "{\"error\": \"An error occurred while updating the user.\"}");
        }
    }

}