package org.example.application.game.repository;

import org.example.application.game.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserMemoryRepository implements UserRepository {
    private final List<User> users;

    public UserMemoryRepository() {
        this.users = new ArrayList<>();
    }

    @Override
    public User save(User user) {
        user.setId(this.users.size() + 1);
        this.users.add(user);

        return user;
    }

    @Override
    public List<User> findAll() {
        return this.users;
    }

    @Override
    public Optional<User> find(int id) {
        return Optional.empty();
    }

    @Override
    public User delete(User user) {
        return null;
    }

    public boolean findByUsername(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }
    public boolean isValidUser(String username, String password) {
        return users.stream()
                .anyMatch(user -> user.getUsername().equals(username) && user.getPassword().equals(password));
    }
}

