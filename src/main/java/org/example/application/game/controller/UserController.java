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
            return readAll();
        }
        if (request.getMethod().equals(Method.GET)) {
            //return update(request);
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
    private Response readAll() {
        ArrayList<User> users = userService.getAll();

        return json(Status.OK, users);
    }
   /* private Response update(Request request) throws SQLException {
        // Benutzername aus der URL extrahieren
        String username = request.getPath().substring(request.getPath().lastIndexOf("/") + 1);

        // Benutzerobjekt aus dem Request-Body extrahieren
        User updatedUser = fromBody(request.getBody(), new TypeReference<User>() {});

        // Benutzer im Service aktualisieren
        User user = userService.updateUser(username, updatedUser);
        if (user != null) {
            return json(Status.OK, "{\"message\": \"" + username + " was successfully updated!\"}");
        }
        return json(Status.NOT_FOUND, "{\"error\": \"User not found\"}");
    }

    */

}