package org.example.application.game.service;

import org.example.application.game.entity.User;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.repository.UserDbRepository;

import java.util.*;

public class UserService {
    private final UserDbRepository userRepository;
    private final TokenService tokenService;
    private final Map<String, String> tokens = new HashMap<>();



    public UserService(UserDbRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }
    public User create(User user) throws UserAlreadyExistsException{
        // Prüfen, ob der Benutzer existiert
        if (userRepository.findByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("User '" + user.getUsername() + "' already exists.");
        }
        // Benutzer speichern
        user.setId(UUID.randomUUID());
        return userRepository.save(user);
    }
    public User getUserData(String username) {
        // Benutzer aus dem Repository abrufen
        return userRepository.getUserData(username);
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

    public User updateUser(String username, User updatedUser) {
        // Benutzer anhand des Benutzernamens abrufen
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            // Aktualisiere nur die Felder name, bio und image
            user.setName(updatedUser.getName());
            user.setBio(updatedUser.getBio());
            user.setImage(updatedUser.getImage());
            return userRepository.updateUser(user);
        }
        // Benutzer existiert nicht
        return null;
    }
}
