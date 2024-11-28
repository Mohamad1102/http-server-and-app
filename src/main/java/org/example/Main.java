package org.example;

import org.example.application.game.Game;
import org.example.server.Server;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting server");
        Server server = new Server(new Game());
        server.start();
    }
}
