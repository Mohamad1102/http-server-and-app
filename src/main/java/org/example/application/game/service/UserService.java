package org.example.application.game.service;

import org.example.application.game.entity.User;
import org.example.application.game.exception.EntityNotFoundException;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final Map<String, String> tokens = new HashMap<>();



    public UserService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }
    public User create(User user) throws UserAlreadyExistsException {
        // Prüfen, ob der Benutzer existiert
        if (userRepository.findByUsername(user.getUsername())) {
            System.out.println("user exists");
            throw new UserAlreadyExistsException("User '" + user.getUsername() + "' already exists.");
        }
        // Benutzer speichern
        user.setId(UUID.randomUUID().toString());
        return userRepository.save(user);
    }
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(int id) {
        return userRepository.find(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getName(), id));
    }
    public String login(User user) {
        // Benutzername und Passwort prüfen
        if (!userRepository.isValidUser(user.getUsername(), user.getPassword())) {
            System.out.println("Invalid credentials");
            return null; // Login fehlgeschlagen
        }

        System.out.println("Login successful");
        String token = tokenService.CreatToken(user.getUsername());
        tokens.put(user.getUsername(), token);
        return token;
    }
}
