package org.example.application.game.exception;

import org.example.server.http.Status;

public class CardPackageCreationException extends RuntimeException {
    private final Status status = Status.CONFLICT; // Status direkt initialisieren

    public CardPackageCreationException(String message) {
        super(message);
    }

    public Status getStatus() {
        return status;
    }
}

