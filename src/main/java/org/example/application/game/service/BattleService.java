package org.example.application.game.service;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.User;
import org.example.application.game.repository.*;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class BattleService {
    private final BattleRepository battleRepository;
    private final UserRepository userRepository;
    private final DeckDbRepository deckRepository;
    private final BlockingDeque<String> battleRequestQueue;
    private final BlockingDeque<String> battleResultQueue;

    // Konstruktor
    public BattleService(BattleRepository battleRepository, UserRepository userRepository, DeckDbRepository deckRepository) {
        this.battleRepository = battleRepository;
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.battleRequestQueue = new LinkedBlockingDeque<>();
        this.battleResultQueue = new LinkedBlockingDeque<>();
    }

    // Die Methode, die den Battle-Request bearbeitet
    public String startBattle(String username) throws InterruptedException {
        // Prüfen, ob der Benutzer existiert
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            return "User not found!";
        }

        String opponentUsername =  null;
        synchronized (battleRequestQueue) {
            opponentUsername = battleRequestQueue.poll();
            if(opponentUsername == null) {
                battleRequestQueue.add(username);
            }
        }

        // Wenn die Warteschlange leer ist, füge den Benutzer hinzu und warte auf einen Gegner
        if (opponentUsername == null) {
            System.out.println("IF ABFREAGE");
            String result = battleResultQueue.take();
            return result;
        } else {
            // Wenn bereits ein anderer Benutzer in der Warteschlange ist, starte den Kampf
            User opponent = userRepository.findUserByUsername(opponentUsername);

            System.out.println("ELSE IF 1");
            // Decks der beiden Benutzer abrufen
            List<Card> userDeck = battleRepository.getDeckForUser(user.getId());
            List<Card> opponentDeck = battleRepository.getDeckForUser(opponent.getId());

            System.out.println("ELSE IF 2");
            // Kampflogik ausführen
            String battleResult = startCombat(user, userDeck, opponent, opponentDeck);

            System.out.println("ELSE IF 3");
            // Speichere das Ergebnis des Kampfes und benachrichtige beide Benutzer
            battleResultQueue.add(battleResult);  // Füge den Gewinner der Result-Warteschlange hinzu

            System.out.println(battleResult);
            // Gib eine Nachricht zurück, dass der Kampf zwischen den beiden gestartet wurde
            return battleResult;
        }
    }

    // Simulierte Kampf-Logik und Ergebnis
    public String startCombat(User player1, List<Card> deck1, User player2, List<Card> deck2) {
        StringBuilder battleLog = new StringBuilder();
        battleLog.append("Battle between ").append(player1.getUsername()).append(" and ").append(player2.getUsername()).append("\n");

        int roundCount = 0;
        while (!deck1.isEmpty() && !deck2.isEmpty() && roundCount < 100) {
            roundCount++;

            // Ziehe zufällige Karten
            Card card1 = deck1.remove((int) (Math.random() * deck1.size()));
            Card card2 = deck2.remove((int) (Math.random() * deck2.size()));

            // Berechne Schaden
            double damage1 = calculateDamage(card1, card2);
            double damage2 = calculateDamage(card2, card1);

            // Logge die Runde
            battleLog.append("Round ").append(roundCount).append(": ")
                    .append(player1.getUsername()).append("'s ").append(card1.getName()).append(" (").append(damage1).append(") vs ")
                    .append(player2.getUsername()).append("'s ").append(card2.getName()).append(" (").append(damage2).append(")\n");

            // Gewinner der Runde bestimmen
            if (damage1 > damage2) {
                battleLog.append(player1.getUsername()).append(" wins this round and takes ").append(card2.getName()).append("\n");
                deck1.add(card2); // Karte des Gegners gewinnen
            } else if (damage2 > damage1) {
                battleLog.append(player2.getUsername()).append(" wins this round and takes ").append(card1.getName()).append("\n");
                deck2.add(card1); // Karte des Gegners gewinnen
            } else {
                battleLog.append("Draw! No cards exchanged.\n");
                deck1.add(card1); // Beide Karten zurücklegen
                deck2.add(card2);
            }
        }

        // Ergebnis des Battles
        String result;
        List<UUID> loserDeckCardIds = new ArrayList<>();

        if (deck1.isEmpty() && deck2.isEmpty()) {
            result = "Draw! Both players ran out of cards.";
        } else if (deck1.isEmpty()) {
            result = player2.getUsername() + " wins the battle!";
            loserDeckCardIds = deckRepository.getDeckCardIds(player1.getId());// Karten des Verlierers abrufen

            deckRepository.updateCardUserId(player2.getId(), loserDeckCardIds);  // Karten übertragen
            battleRepository.resetDeckCardsForLoser(player1.getId());  // Setze Karten des Verlierers auf NULL
            updateStatsAndElo(player2, player1, false); // player2 ist Gewinner, player1 der Verlierer
        } else if (deck2.isEmpty()) {
            result = player1.getUsername() + " wins the battle!";
            loserDeckCardIds = deckRepository.getDeckCardIds(player2.getId());  // Karten des Verlierers abrufen
            deckRepository.updateCardUserId(player1.getId(), loserDeckCardIds);  // Karten übertragen
            battleRepository.resetDeckCardsForLoser(player2.getId());  // Setze Karten des Verlierers auf NULL
            updateStatsAndElo(player1, player2, true); // player1 ist Gewinner, player2 der Verlierer
        } else {
            result = "Draw! The battle timed out.";
        }

        battleLog.append("Battle Result: ").append(result).append("\n");
        return battleLog.toString();
    }

    public double calculateDamage(Card attacker, Card defender) {
        // Goblins haben Angst vor Drachen
        if (attacker.isGoblin() && defender.isDragon()) {
            return 0; // Goblins greifen Drachen nicht an
        }

        // Zauberer kontrollieren Orks
        if (attacker.isWizard() && defender.getName().toLowerCase().contains("ork")) {
            return 0; // Orks können Zauberer nicht verletzen
        }

        // Ritter ertrinken durch Wassersprüche
        if (attacker.isSpellCard() && attacker.getName().toLowerCase().contains("water") && defender.isKnight()) {
            return Double.MAX_VALUE; // Ritter verliert sofort
        }

        // Kraken sind immun gegen Sprüche
        if (defender.isKraken() && attacker.isSpellCard()) {
            return 0; // Sprüche haben keine Wirkung auf Kraken
        }

        // Feuerelfen können Drachen ausweichen
        if (attacker.isDragon() && defender.isFireElf()) {
            return 0; // Feuerelfen entkommen Drachen
        }

        // Berechnung von elementarer Effektivität
        double baseDamage = attacker.getDamage();
        if (attacker.isSpellCard() || defender.isSpellCard()) {
            switch (attacker.getName().toLowerCase()) {
                case "water":
                    if (defender.getName().toLowerCase().contains("fire")) {
                        return baseDamage * 2; // Effektiv
                    } else if (defender.getName().toLowerCase().contains("normal")) {
                        return baseDamage / 2; // Nicht effektiv
                    }
                    break;
                case "fire":
                    if (defender.getName().toLowerCase().contains("normal")) {
                        return baseDamage * 2; // Effektiv
                    } else if (defender.getName().toLowerCase().contains("water")) {
                        return baseDamage / 2; // Nicht effektiv
                    }
                    break;
                case "normal":
                    if (defender.getName().toLowerCase().contains("water")) {
                        return baseDamage * 2; // Effektiv
                    } else if (defender.getName().toLowerCase().contains("fire")) {
                        return baseDamage / 2; // Nicht effektiv
                    }
                    break;
            }
        }

        return baseDamage; // Keine Elementarinteraktion
    }

    private void updateStatsAndElo(User winner, User loser, boolean player1Won) {
        // Update der Stats für den Gewinner und den Verlierer
        battleRepository.updateStats(winner.getId(), true);  // Gewinner stat update
        battleRepository.updateStats(loser.getId(), false);   // Verlierer stat update

        // Hole die aktuellen Elo-Werte des Gewinners und des Verlierers
        int winnerElo = battleRepository.getEloByUserId(winner.getId());
        int loserElo = battleRepository.getEloByUserId(loser.getId());

        // Berechne neue Elo-Werte
        int newWinnerElo = winnerElo + 3;  // Gewinner Elo um 3 erhöhen
        int newLoserElo = loserElo - 5;    // Verlierer Elo um 5 verringern

        // Sicherstellen, dass der Elo-Wert des Verlierers nicht negativ wird
        if (newLoserElo < 0) {
            newLoserElo = 0;
        }

        // Speichern der neuen Elo-Werte in der Datenbank
        battleRepository.updateElo(winner.getId(), newWinnerElo);
        battleRepository.updateElo(loser.getId(), newLoserElo);
    }




}

