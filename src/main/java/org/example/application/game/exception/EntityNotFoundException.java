package org.example.application.game.exception;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String type, UUID id) {
        super("%s: %s not found");
    }
}
