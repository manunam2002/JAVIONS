package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 * collecte les données fixes d'un aéronef
 * @param registration immatriculation de l'aéronef
 * @param typeDesignator type de l'aéronef
 * @param model modèle de l'aéronef
 * @param description description de l'aéronef
 * @param wakeTurbulenceCategory catégorie de turbulence de sillage d'un aéronef
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public record AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator, String model,
                           AircraftDescription description, WakeTurbulenceCategory wakeTurbulenceCategory) {

    /**
     * constructeur compact
     * @param registration immatriculation de l'aéronef
     * @param typeDesignator type de l'aéronef
     * @param model modèle de l'aéronef
     * @param description description de l'aéronef
     * @param wakeTurbulenceCategory catégorie de turbulence de sillage d'un aéronef
     * @throws NullPointerException si l'un de ses arguments est null
     */
    public AircraftData{
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}
