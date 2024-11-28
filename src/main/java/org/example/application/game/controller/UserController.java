package org.example.application.game.controller;

import org.example.application.game.entity.User;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.service.UserService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.util.List;


public class UserController extends Controller{
    private final UserService userService = new UserService();

    @Override
    public Response handle(Request request) throws UserAlreadyExistsException {

        System.out.println("here");
        if (request.getMethod().equals(Method.POST)) {
            return create(request);
        }
        if (request.getMethod().equals(Method.GET)) {
            return readAll();
        }

        return null;
    }

    private Response create(Request request) throws UserAlreadyExistsException {
        System.out.println("here 2");
        // request --> user
        User user = fromBody(request.getBody(), User.class);
        user = userService.create(user);

        return json(Status.CREATED, user);
    }

    private Response readAll() {
        List<User> users = userService.getAll();

        return json(Status.OK, users);
    }
}

