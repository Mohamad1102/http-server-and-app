package org.example.application.game;

import org.example.application.game.controller.*;
import org.example.application.game.data.ConnectionPool;
import org.example.application.game.exception.BadRequestException;
import org.example.application.game.exception.CardPackageCreationException;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.repository.*;
import org.example.application.game.exception.ControllerNotFoundException;
import org.example.application.game.routing.Router;
import org.example.application.game.service.*;
import org.example.server.Application;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

import java.sql.SQLException;

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
        } catch (CardPackageCreationException e) {
            Response response = new Response();
            response.setStatus(Status.CONFLICT);
            return response;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void initializeRoutes() {

        ConnectionPool connectionPool = new ConnectionPool();

        UserRepository userRepository = new UserDbRepository(connectionPool);
        TokenService tokenService = new TokenService();
        UserService userService = new UserService(userRepository, tokenService);
        CardPackageRepository cardRepository = new CardPackageDbRepository(connectionPool); // Verwende die konkrete Implementierung
        CardPackageService cardPackageService = new CardPackageService(cardRepository, userRepository);
        DeckDbRepository deckRepository = new DeckDbRepository(connectionPool);
        DeckService deckService = new DeckService(deckRepository, userRepository, cardRepository);
        TradingDealRepository tradingDealRepo = new TradingDealRepository(connectionPool);
        TradeService tradeService = new TradeService(tradingDealRepo, userRepository, cardRepository);
        StatsDbRepository statsDbRepository = new StatsDbRepository(connectionPool);
        StatsService statsService = new StatsService(statsDbRepository, userRepository);
        BattleRepository battleRepository = new BattleRepository(connectionPool);
        EloDbRepository eloDbRepository = new EloDbRepository(connectionPool);
        BattleService battleService = new BattleService(battleRepository, userRepository, deckRepository);
        EloService eloService = new EloService(eloDbRepository);



        this.router.addRoute("/users", new UserController(userService));
        this.router.addRoute("/sessions", new SessionController(userService));
        this.router.addRoute("/packages", new CardPackageController(cardPackageService)); // Stelle sicher, dass die Controller-Instanziierung korrekt ist
        this.router.addRoute("/transactions/packages", new CardPackageController(cardPackageService)); // Route für Paket kaufen
        this.router.addRoute("/cards", new CardPackageController(cardPackageService));
        this.router.addRoute("/deck", new DeckController(deckService));
        this.router.addRoute("/tradings", new TradeController(tradeService));
        this.router.addRoute("/tradings/:tradingdealid", new TradeController(tradeService));
        this.router.addRoute("/stats", new StatsController(statsService));
        this.router.addRoute("/battles", new BattleController(battleService));
        this.router.addRoute("/scoreboard", new EloController(eloService));




    }

}


