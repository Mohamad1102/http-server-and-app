package org.example.application.game.service;

import org.example.application.game.entity.User;
import org.example.application.game.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Test
    public void give_Token_when_einloggt() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = new User();
        TokenService tokenService = new TokenService();
        UserService userService = new UserService(userRepository, tokenService);
        OngoingStubbing<String> stringOngoingStubbing = when(userService.login(any())).thenReturn(user.getUsername() + "-mtcgToken");
    }
}