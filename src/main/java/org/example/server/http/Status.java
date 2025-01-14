package org.example.server.http;

public enum Status {
    OK(200, "OK"),
    CREATED(201, "CREATED"),
    NO_CONTENT(204, "No Content"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    UNAUTHORIZED (401, "NON Existent"),
    FORBIDDEN (403, "FORBIDDEN"),
    NOT_FOUND(404, "Not Found"),
    METHODE_NOT_FOUND(405, "METHODE NOT FOUND"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),;

    private final int code;
    private final String message;

    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
