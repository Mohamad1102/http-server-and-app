package org.example.application.game.service;

import org.example.application.game.entity.User;
import org.example.application.game.exception.EntityNotFoundException;
import org.example.application.game.exception.SQLException;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.repository.UserRepository;

import java.util.*;

public class UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final Map<String, String> tokens = new HashMap<>();



    public UserService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }
    public User create(User user) throws UserAlreadyExistsException{
        // Prüfen, ob der Benutzer existiert
        if (userRepository.findByUsername(user.getUsername())) {
            System.out.println("user exists");
            throw new UserAlreadyExistsException("User '" + user.getUsername() + "' already exists.");
        }
        // Benutzer speichern
        user.setId(UUID.randomUUID());
        return userRepository.save(user);
    }
    public ArrayList<User> getAll() {
        return userRepository.findAll();
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

    public User updateUser(String username, User updatedUser) throws SQLException, java.sql.SQLException {
        // Benutzer aus der Datenbank holen
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            // Aktualisieren der Felder
            user.setPassword(updatedUser.getPassword());
            user.setCoins(updatedUser.getCoins());
            return userRepository.save(user);
        }
        return null;
    }
}
