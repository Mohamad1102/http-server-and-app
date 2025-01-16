package org.example.application.game.repository;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.Package;
import org.example.application.game.entity.User;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface CardPackageRepository {
    UUID createPackageForAdmin(ArrayList<Card> cards, User adminUser);
    Optional<Package> findAvailablePackage();
    void assignPackageToUser(UUID packageId, User user);
    ArrayList<Card> findCardsByUsername(UUID userid);
    Card findCardById(UUID cardId);
    boolean isCardOwnedByUser(UUID cardId, UUID userId);
    int getCardCount(String username);
}
