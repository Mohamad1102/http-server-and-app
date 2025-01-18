package org.example.application.game.service;

import org.example.application.game.entity.UserElo;
import org.example.application.game.repository.EloDbRepository;

import java.util.List;
import java.util.Map;

public class EloService {
    private final EloDbRepository eloDbRepository;

    public EloService(EloDbRepository eloDbRepository) {
        this.eloDbRepository = eloDbRepository;
    }

    public List<Map<String, Object>> getScoreBord()
    {
        List<Map<String, Object>> scorebord = eloDbRepository.getScoreBord();

        return scorebord;
    }
}
