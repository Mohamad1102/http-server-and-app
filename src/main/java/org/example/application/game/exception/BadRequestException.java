package org.example.application.game.exception;
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
