package org.example.application.game.repository;

import org.example.application.game.entity.Package;
import org.example.application.game.entity.User;

import java.util.Optional;

public interface CardPackageRepository {
    void savePackage(Package cardPackage);
    Optional<Package> findAvailablePackage();
    void assignPackageToUser(Package cardPackage, User user);
    // Paket aus der Liste entfernen
    public void removePackage(Package cardPackage);

}
