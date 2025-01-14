package org.example.application.game.repository;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    ArrayList<User> findAll();
    boolean findByUsername(String username);
    boolean isValidUser(String username, String password);
    void updateCoins(String username, int coins);
    User findUserByUsername(String username);
    UUID getUserIdByUsername(String username) throws SQLException;
    User updateUser(User user);
    User getUserData(String username);
}
