package org.example.application.game;

import org.example.application.game.controller.Controller;
import org.example.application.game.controller.SessionController;
import org.example.application.game.controller.UserController;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.repository.UserMemoryRepository;
import org.example.application.game.routing.ControllerNotFoundException;
import org.example.application.game.routing.Router;
import org.example.application.game.service.UserService;
import org.example.server.Application;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

public class Game implements Application {
    private final UserController userController = new UserController(new UserService(new UserMemoryRepository()));

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
        } catch (UserAlreadyExistsException e) {
            Response response = new Response();
            response.setStatus(Status.CONFLICT);
            return response;
        }
    }

    private void initializeRoutes() {
        UserMemoryRepository userRepository = new UserMemoryRepository();
        UserService userService = new UserService(userRepository);

        this.router.addRoute("/users", new UserController(userService));
        this.router.addRoute("/sessions", new SessionController(userService));
    }

}


