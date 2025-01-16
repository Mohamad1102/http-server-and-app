package org.example.application.game.repository;

import org.example.application.game.data.ConnectionPool;

public class EloDbRepository {
    private final ConnectionPool connectionPool;
    public EloDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

}
