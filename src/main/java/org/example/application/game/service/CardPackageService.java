package org.example.application.game.service;

import org.example.application.game.entity.Card;
import org.example.application.game.entity.Package;
import org.example.application.game.repository.CardPackageRepository;

import java.util.List;

public class CardPackageService {

    private final CardPackageRepository cardPackageRepository;

    // Konstruktor-Injektion
    public CardPackageService(CardPackageRepository cardPackageRepository) {
        this.cardPackageRepository = cardPackageRepository;
    }

    public boolean createPackage(List<Card> cards) {
        if (cards.size() != 5) {
            throw new IllegalArgumentException("Es müssen genau 5 Karten im Paket sein.");
        }
        // Paket erstellen und speichern
        Package cardPackage = new Package(cards);  // Vermeide java.lang.Package
        cardPackageRepository.savePackage(cardPackage);
        return true;
    }
}
