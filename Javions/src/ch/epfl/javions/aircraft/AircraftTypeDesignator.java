package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * represente l'indicateur de type
 * @param string indicateur de type
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public record AircraftTypeDesignator(String string) {

    private final static Pattern type = Pattern.compile("[A-Z0-9]{2,4}");

    /**
     * constructeur compact
     * @param string indicateur de type
     * @throws IllegalArgumentException si la chaine n'est pas vide et ne représente pas un'indicateur de type valide
     */
    public AircraftTypeDesignator {
        Preconditions.checkArgument(string.equals("") || string == null || type.matcher(string).matches());
    }
}