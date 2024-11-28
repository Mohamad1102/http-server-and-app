package org.example.application.echo;

import org.example.server.Application;
import org.example.server.http.Request;
import org.example.server.http.Response;
import org.example.server.http.Status;

public class EchoApplication implements Application {
    @Override
    public Response handle(Request request) {

        Response response = new Response();
        response.setStatus(Status.OK);
        response.setHeader("Content-Type", "text/plain");
        response.setBody(request.getHttp());

        return response;
    }
}
