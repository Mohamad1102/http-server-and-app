package org.example.server;

import org.example.server.http.Request;
import org.example.server.http.Response;

public interface Application {
    Response handle(Request request) throws RuntimeException;
}
