package org.example.application.game.service;

import org.example.application.game.entity.UserStats;
import org.example.application.game.repository.StatsDbRepository;
import org.example.application.game.repository.UserRepository;
import org.example.application.game.exception.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatsServiceTest {

    private StatsService statsService;
    private StatsDbRepository statsDbRepositoryMock;
    private UserRepository userRepositoryMock;

    @BeforeEach
    void setUp() {
        statsDbRepositoryMock = Mockito.mock(StatsDbRepository.class);
        userRepositoryMock = Mockito.mock(UserRepository.class);
        statsService = new StatsService(statsDbRepositoryMock, userRepositoryMock);
    }

    @Test
    void getUserStats_ValidToken_ReturnsUserStats() throws RuntimeException {
        // Arrange
        String token = "testUser-mtcgToken";
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        UserStats expectedStats = new UserStats(10, 5, 3); // Beispielwerte

        when(userRepositoryMock.getUserIdByUsername(username)).thenReturn(userId);
        when(statsDbRepositoryMock.getUserStats(userId)).thenReturn(expectedStats);

        // Act
        UserStats actualStats = statsService.getUserStats(token);

        // Assert
        assertNotNull(actualStats, "UserStats sollte nicht null sein");
        assertEquals(expectedStats.getNumberOfBattles(), actualStats.getNumberOfBattles(), "Anzahl der Kämpfe sollte übereinstimmen");
        assertEquals(expectedStats.getWins(), actualStats.getWins(), "Siege sollten übereinstimmen");
        assertEquals(expectedStats.getLosses(), actualStats.getLosses(), "Niederlagen sollten übereinstimmen");
        verify(userRepositoryMock, times(1)).getUserIdByUsername(username);
        verify(statsDbRepositoryMock, times(1)).getUserStats(userId);
    }

    @Test
    void getUserStats_InvalidToken_ThrowsIllegalArgumentException() {
        // Arrange
        String token = "invalidTokenFormat";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> statsService.getUserStats(token),
                "Ein ungültiges Token sollte eine IllegalArgumentException auslösen");
    }

    @Test
    void getUserStats_UsernameNotFound_ThrowsException() throws RuntimeException {
        // Arrange
        String token = "unknownUser-mtcgToken";
        String username = "unknownUser";

        when(userRepositoryMock.getUserIdByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> statsService.getUserStats(token),
                "Ein unbekannter Benutzername sollte eine NullPointerException auslösen");
        verify(userRepositoryMock, times(1)).getUserIdByUsername(username);
        verifyNoInteractions(statsDbRepositoryMock);
    }
    @Test
    void getUserStats_StatsNotFound_ThrowsSQLException() throws SQLException {
        // Arrange
        String token = "testUser-mtcgToken";
        String username = "testUser";
        UUID userId = UUID.randomUUID();

        when(userRepositoryMock.getUserIdByUsername(username)).thenReturn(userId);
        when(statsDbRepositoryMock.getUserStats(userId)).thenThrow(new RuntimeException("Stats not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> statsService.getUserStats(token),
                "If stats are not found, a RuntimeException should be thrown");
        verify(userRepositoryMock, times(1)).getUserIdByUsername(username);
        verify(statsDbRepositoryMock, times(1)).getUserStats(userId);
    }


    @Test
    void extractUsernameFromToken_ValidToken_ReturnsUsername() {
        // Arrange
        String token = "testUser-mtcgToken";

        // Act
        String username = statsService.extractUsernameFromToken(token);

        // Assert
        assertEquals("testUser", username, "Der extrahierte Benutzername sollte mit dem erwarteten Wert übereinstimmen");
    }

    @Test
    void extractUsernameFromToken_InvalidToken_ThrowsIllegalArgumentException() {
        // Arrange
        String token = "invalidToken";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> statsService.extractUsernameFromToken(token),
                "Ein ungültiges Token-Format sollte eine IllegalArgumentException auslösen");
    }

    @Test
    void extractUsernameFromToken_NullToken_ThrowsIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> statsService.extractUsernameFromToken(null),
                "Ein null-Token sollte eine IllegalArgumentException auslösen");
    }
}
