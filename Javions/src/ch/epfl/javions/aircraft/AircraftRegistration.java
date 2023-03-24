package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * represente l'immatriculation de l'aéronef
 * @param string immatriculation
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public record AircraftRegistration(String string) {

    private final static Pattern immatriculation = Pattern.compile("[A-Z0-9 .?/_+-]+");

    /**
     * constructeur compact
     * @param string immatriculation
     * @throws IllegalArgumentException si la chaine ne représente pas un'immatriculation valide
     */
    public AircraftRegistration {
        Preconditions.checkArgument(immatriculation.matcher(string).matches());
    }
}