package org.example.application.game;

import org.example.application.game.controller.*;
import org.example.application.game.data.ConnectionPool;
import org.example.application.game.exception.BadRequestException;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.repository.*;
import org.example.application.game.exception.ControllerNotFoundException;
import org.example.application.game.routing.Router;
import org.example.application.game.service.CardPackageService;
import org.example.application.game.service.DeckService;
import org.example.application.game.service.TokenService;
import org.example.application.game.service.UserService;
import org.example.server.Application;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

public class Game implements Application {

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
            // TODO: better exception handling, map exception to http code?
            Response response = new Response();
            response.setStatus(Status.NOT_FOUND);

            response.setHeader("Content-Type", "application/json");
            response.setBody("{\"error\": \"Path: %s not found\" }".formatted(e.getMessage()));

            return response;
        } catch (UserAlreadyExistsException e) {
            Response response = new Response();
            response.setStatus(Status.CONFLICT);
            return response;
        } catch (BadRequestException e) {
            Response response = new Response();
            response.setStatus(Status.BAD_REQUEST);
            return response;

        }
    }

    private void initializeRoutes() {

        ConnectionPool connectionPool = new ConnectionPool();

        UserRepository userRepository = new UserDbRepository(connectionPool);
        TokenService tokenService = new TokenService();
        UserService userService = new UserService(userRepository, tokenService);
        CardPackageRepository cardRepository = new CardPackageDbRepository(connectionPool); // Verwende die konkrete Implementierung
        CardPackageService cardPackageService = new CardPackageService(cardRepository, userRepository);
        DeckRepository deckRepository = new DeckDbRepository(connectionPool);
        DeckService deckService = new DeckService(deckRepository, userRepository, cardRepository);

        this.router.addRoute("/users", new UserController(userService));
        this.router.addRoute("/sessions", new SessionController(userService));
        this.router.addRoute("/wait", new WaitController());
        this.router.addRoute("/health", new HealthController());
        this.router.addRoute("/packages", new CardPackageController(cardPackageService)); // Stelle sicher, dass die Controller-Instanziierung korrekt ist
        this.router.addRoute("/transactions/packages", new CardPackageController(cardPackageService)); // Route für Paket kaufen
        this.router.addRoute("/cards", new CardPackageController(cardPackageService));
        this.router.addRoute("/deck", new DeckController(deckService));


    }

}


