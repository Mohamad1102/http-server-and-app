package org.example.application.game.repository;

import org.example.application.game.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    List<User> findAll();

    Optional<User> find(int id);

    User delete(User user);

    boolean findByUsername(String username);

    boolean isValidUser(String username, String password);

}
