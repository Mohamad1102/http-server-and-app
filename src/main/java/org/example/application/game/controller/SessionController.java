package org.example.application.game.controller;
import org.example.application.game.entity.User;
import org.example.application.game.exception.EntityNotFoundException;
import org.example.application.game.repository.UserMemoryRepository;
import org.example.application.game.repository.UserRepository;
import org.example.application.game.service.UserService;
import org.example.server.http.Method;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

public class SessionController extends Controller{
    UserService userService;

    public SessionController(UserService userService) {
        this.userService  = userService;
    }

    public Response handle(Request request){

        if (request.getMethod().equals(Method.POST)) {
            return login(request);
        }
        return null;
    }
    private Response login(Request request) {
        // Request-Body in User-Objekt konvertieren
        User user = fromBody(request.getBody(), User.class);

        try {
            String token = userService.login(user);

            if (token != null) {
                System.out.println(token);
                return json(Status.OK, "{\"message\": \"Login successful\", \"token\": \"" + token + "\"}");
            } else {
                return json(Status.UNAUTHORIZED, "{\"error\": \"Invalid username or password\"}");
            }
        } catch (EntityNotFoundException e) {
            return json(Status.NOT_FOUND, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}
