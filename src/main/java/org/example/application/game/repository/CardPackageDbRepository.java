package org.example.application.game.repository;

import org.example.application.game.entity.Package;
import org.example.application.game.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardPackageDbRepository implements CardPackageRepository{
    private final List<Package> packages = new ArrayList<>();
    @Override
    public void savePackage(Package cardPackage) {
        System.out.println("Packages before adding: " + packages.size());
        packages.add(cardPackage);
        System.out.println("Packages after adding: " + packages.size());

        System.out.println("Package saved: " + cardPackage);
    }
    public Optional<Package> findAvailablePackage() {
        return packages.stream().findFirst();  // Gibt das erste verfügbare Paket zurück
    }
    public void assignPackageToUser(Package cardPackage, User user) {
        // Hier könnte man die Logik hinzufügen, um das Paket einem Benutzer zuzuweisen
        System.out.println("Package assigned to user: " + user.getUsername());
    }
    public void removePackage(Package cardPackage) {
        System.out.println("Packages before removing: " + packages.size());
        packages.remove(cardPackage); // Entfernt das Paket
        System.out.println("!!!!PAKAGE DELETED!!!!");
        System.out.println("Packages after removing: " + packages.size());
    }
}
