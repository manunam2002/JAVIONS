package ch.epfl.javions.aircraft;

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
     */
    public AircraftRegistration {
        if (!immatriculation.matcher(string).matches()) throw new IllegalArgumentException();
    }
}