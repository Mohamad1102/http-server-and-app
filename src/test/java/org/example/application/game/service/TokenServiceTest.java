package org.example.application.game.service;

import org.example.application.game.entity.User;
import org.example.application.game.repository.UserDbRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Test
    public void give_Token_when_einloggt() {
        UserDbRepository userRepository = mock(UserDbRepository.class);
        User user = new User();
        user.setUsername("test");
        TokenService tokenService = new TokenService();
        UserService userService = new UserService(userRepository, tokenService);
        when(userRepository.isValidUser(any(), any())).thenReturn(true);

        String token = userService.login(user);

        assertEquals("test-mtcgToken", token);
    }
}