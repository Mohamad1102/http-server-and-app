package org.example.application.game.service;

import org.example.application.game.entity.UserStats;
import org.example.application.game.exception.SQLException;
import org.example.application.game.repository.StatsDbRepository;
import org.example.application.game.repository.UserDbRepository;
import org.example.application.game.repository.UserRepository;

import java.util.UUID;

public class StatsService {
    private final StatsDbRepository statsDbRepository;
    private final UserRepository userRepository;
    public StatsService(StatsDbRepository statsDbRepository, UserRepository userRepository) {
        this.statsDbRepository = statsDbRepository;
        this.userRepository = userRepository;
    }

    String extractUsernameFromToken(String token) {
        if (token == null || !token.contains("-")) {
            throw new IllegalArgumentException("Invalid token format");
        }
        return token.split("-")[0];
    }

    public UserStats getUserStats(String token) throws SQLException {
        String username = extractUsernameFromToken(token);

        UUID userId = userRepository.getUserIdByUsername(username);
        if (userId == null) {
            throw new NullPointerException("User not found");
        }

        System.out.println("ZUR REPOS");
        return statsDbRepository.getUserStats(userId);
    }

}
