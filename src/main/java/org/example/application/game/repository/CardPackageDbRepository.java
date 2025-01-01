package org.example.application.game.repository;

import org.example.application.game.entity.Package;

import java.util.ArrayList;
import java.util.List;

public class CardPackageDbRepository implements CardPackageRepository{
    private final List<Package> packages = new ArrayList<>();
    @Override
    public void savePackage(Package cardPackage) {
        packages.add(cardPackage);
        System.out.println("Package saved: " + cardPackage);
    }
}
