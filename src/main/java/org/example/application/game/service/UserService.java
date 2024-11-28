package org.example.application.game.service;

import org.example.application.game.entity.User;
import org.example.application.game.exception.EntityNotFoundException;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.repository.UserMemoryRepository;
import org.example.application.game.repository.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserMemoryRepository userMemoryRepository) {
        this.userRepository = userMemoryRepository;
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

    public boolean login(User user) {
        // Benutzername und Passwort prüfen
        System.out.println("Username: " + user.getUsername());
        System.out.println("Password: " + user.getPassword());
        if (!userRepository.isValidUser(user.getUsername(), user.getPassword())) {
            System.out.println("Invalid credentials");
            return false; // Login fehlgeschlagen
        }

        System.out.println("Login successful");
        return true; // Login erfolgreich
    }
}
