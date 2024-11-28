package org.example.application.game.service;

import org.example.application.game.entity.User;
import org.example.application.game.exception.EntityNotFoundException;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.repository.UserMemoryRepository;
import org.example.application.game.repository.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserMemoryRepository();
    }

    public User create(User user) throws UserAlreadyExistsException {
        // Prüfen, ob der Benutzer existiert
        if (userRepository.findByUsername(user.getUsername())) {
            System.out.println("user exists");
            throw new UserAlreadyExistsException("User '" + user.getUsername() + "' already exists.");
        }
        // Benutzer speichern
        System.out.println("user saved");
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(int id) {
        return userRepository.find(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getName(), id));
    }
}
