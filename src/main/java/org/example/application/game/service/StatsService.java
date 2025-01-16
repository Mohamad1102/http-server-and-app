package org.example.application.game.service;

import org.example.application.game.entity.UserStats;
import org.example.application.game.repository.StatsDbRepository;
import org.example.application.game.repository.UserDbRepository;
import org.example.application.game.repository.UserRepository;

import java.sql.SQLException;
import java.util.UUID;

public class StatsService {
    private final StatsDbRepository statsDbRepository;
    private final UserRepository userRepository;
    public StatsService(StatsDbRepository statsDbRepository, UserRepository userRepository) {
        this.statsDbRepository = statsDbRepository;
        this.userRepository = userRepository;
    }

    private String extractUsernameFromToken(String token) {
        if (token == null || !token.contains("-")) {
            throw new IllegalArgumentException("Invalid token format");
        }
        return token.split("-")[0];
    }

    public UserStats getUserStats(String token) throws SQLException {
       String username = extractUsernameFromToken(token);

        UUID userId = userRepository.getUserIdByUsername(username);

        System.out.println("ZUR REPOS");
        UserStats stats = statsDbRepository.getUserStats(userId);

        return stats;
    }

}
