package org.example.application.game.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPool {
    private final static String URL = "jdbc:postgresql://localhost:5432/swen1";
    private final static String USERNAME = "swen1";
    private final static String PASSWORD = "swen1";

    public Connection getConnection() throws RuntimeException {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
