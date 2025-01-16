package org.example.application.game.service;

import org.example.application.game.entity.User;
import org.example.application.game.exception.EntityNotFoundException;
import org.example.application.game.exception.UserAlreadyExistsException;
import org.example.application.game.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserRepository userRepository;
    private TokenService tokenService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        tokenService = mock(TokenService.class);
        userService = new UserService(userRepository, tokenService);
    }

    @Test
    void testCreateUser_Success() throws UserAlreadyExistsException {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userRepository.findByUsername("testuser")).thenReturn(false);
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.create(user);

        assertNotNull(createdUser.getId());
        assertEquals("testuser", createdUser.getUsername());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void testCreateUser_AlreadyExists() {
        User user = new User();
        user.setUsername("existinguser");

        when(userRepository.findByUsername("existinguser")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.create(user));
        verify(userRepository, never()).save(Mockito.any(User.class));
    }

    @Test
    void testGetUserData_Success() {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.getUserData("testuser")).thenReturn(user);

        User retrievedUser = userService.getUserData("testuser");

        assertEquals("testuser", retrievedUser.getUsername());
        verify(userRepository, times(1)).getUserData("testuser");
    }

    @Test
    void testGetUserData_NotFound() {
        when(userRepository.getUserData("nonexistent")).thenReturn(null);

        User user = userService.getUserData("nonexistent");

        assertNull(user);
        verify(userRepository, times(1)).getUserData("nonexistent");
    }

    @Test
    void testLogin_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userRepository.isValidUser("testuser", "password")).thenReturn(true);
        when(tokenService.CreatToken("testuser")).thenReturn("mockToken");

        String token = userService.login(user);

        assertEquals("mockToken", token);
        verify(tokenService, times(1)).CreatToken("testuser");
    }

    @Test
    void testLogin_Failure() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("wrongpassword");

        when(userRepository.isValidUser("testuser", "wrongpassword")).thenReturn(false);

        String token = userService.login(user);

        assertNull(token);
        verify(tokenService, never()).CreatToken(Mockito.anyString());
    }

    @Test
    void testUpdateUser_Success() {
        User existingUser = new User();
        existingUser.setUsername("testuser");

        User updatedUser = new User();
        updatedUser.setName("New Name");
        updatedUser.setBio("Updated Bio");
        updatedUser.setImage("New Image");

        when(userRepository.findUserByUsername("testuser")).thenReturn(existingUser);
        when(userRepository.updateUser(Mockito.any(User.class))).thenReturn(existingUser);

        User result = userService.updateUser("testuser", updatedUser);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("Updated Bio", result.getBio());
        assertEquals("New Image", result.getImage());
        verify(userRepository, times(1)).updateUser(Mockito.any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        User updatedUser = new User();
        updatedUser.setName("New Name");

        when(userRepository.findUserByUsername("nonexistent")).thenReturn(null);

        User result = userService.updateUser("nonexistent", updatedUser);

        assertNull(result);
        verify(userRepository, never()).updateUser(Mockito.any(User.class));
    }
}


/*
Erklärungen:
Mocking mit Mockito:
userRepository und tokenService werden gemockt, um das Verhalten zu simulieren.
when(...).thenReturn(...) definiert, was zurückgegeben wird, wenn die Methoden des Repositories oder Services aufgerufen werden.
Testfälle:
create: Getestet, ob ein neuer Benutzer erstellt wird und was passiert, wenn der Benutzer bereits existiert.
getUserData: Überprüft, ob Benutzerdaten korrekt zurückgegeben werden und ob null zurückgegeben wird, wenn der Benutzer nicht existiert.
login: Validiert, ob die Login-Methode bei korrekten/inkorrekten Daten korrekt funktioniert.
updateUser: Überprüft, ob Benutzer erfolgreich aktualisiert werden können und ob null zurückgegeben wird, wenn der Benutzer nicht existiert.
Assertions:
Es wird überprüft, ob die Ergebnisse den Erwartungen entsprechen (assertNotNull, assertEquals, assertThrows, etc.).
Verify:
Prüft, ob die entsprechenden Methoden der Repositories und Services aufgerufen wurden (verify).

 */
