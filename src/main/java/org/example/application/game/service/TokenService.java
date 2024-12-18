package org.example.application.game.service;

public class TokenService {
    public String CreatToken(String username) {
        return username + "-" + "mtcgToken";
    }
}