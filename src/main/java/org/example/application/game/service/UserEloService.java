package org.example.application.game.service;

import org.example.application.game.entity.UserElo;
import org.example.application.game.repository.UserEloRepository;

import java.util.UUID;

public class UserEloService {
    private final UserEloRepository userEloRepository;

    public UserEloService(UserEloRepository userEloRepository) {
        this.userEloRepository = userEloRepository;
    }
    public UserElo getEloForUser(UUID userId) {
        return userEloRepository.findEloByUserId(userId);
    }

    public void updateElo(UUID userId, boolean isWin) {
        UserElo userElo = userEloRepository.findEloByUserId(userId);
        int currentElo = userElo.getEloRating();
        int newElo = isWin ? currentElo + 3 : currentElo - 5;
        userEloRepository.updateEloRating(userId, newElo);
    }
}
