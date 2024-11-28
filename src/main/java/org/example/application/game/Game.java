package org.example.application.game;

import org.example.application.game.controller.Controller;
import org.example.application.game.controller.UserController;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.routing.ControllerNotFoundException;
import org.example.application.game.routing.Router;
import org.example.server.Application;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

public class Game implements Application {
    private final UserController userController = new UserController();

    private final Router router;

    public Game() {
        this.router = new Router();
        this.initializeRoutes();
    }

    @Override
    public Response handle(Request request) throws RuntimeException {

        try {
            Controller controller = this.router.getController(request.getPath());
            return controller.handle(request);

        } catch (ControllerNotFoundException e) {
            Response response = new Response();
            response.setStatus(Status.NOT_FOUND);

            response.setHeader("Content-Type", "application/json");
            response.setBody("{\"error\": \"Path: %s not found\" }".formatted(e.getMessage()));

            return response;
        } catch (UserAlreadyExistsException e)
        {
            Response response = new Response();
            Status status = e.getStatus(); // Hole den zugehörigen Status
            response.setStatus(Status.CONFLICT);
            System.out.println("HTTP Code: " + status.getCode());
            System.out.println("Message: " + status.getMessage());
            return response;
        }
    }

    private void initializeRoutes() {
        this.router.addRoute("/users", new UserController());
    }
}


