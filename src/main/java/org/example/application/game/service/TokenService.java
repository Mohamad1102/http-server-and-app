package org.example.application.game.service;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenService {

        private static final Map<String, String> userTokens = new HashMap<>();

        public static String createToken(String username) {
            String token = UUID.randomUUID().toString();
            userTokens.put(username, token);
            return token;
        }

        public static boolean validateToken(String token, String username) {
            return token.equals(userTokens.get(username));
        }
        public static void removeToken(String username) {
            userTokens.remove(username);
        }

}
