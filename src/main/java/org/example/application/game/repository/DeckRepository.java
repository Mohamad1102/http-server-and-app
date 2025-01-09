package org.example.application.game.repository;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.User;

import java.util.ArrayList;
import java.util.UUID;

public interface DeckRepository {
    UUID createDeck(ArrayList<UUID> cardsID, User myUser);

    ArrayList<Card> getDeck(UUID userid);
    ArrayList<Card> getDeckPlain(UUID userid);


}
