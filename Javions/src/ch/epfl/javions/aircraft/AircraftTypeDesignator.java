package ch.epfl.javions.aircraft;

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
     */
    public AircraftTypeDesignator {
        if (!string.equals("") && string != null && !type.matcher(string).matches())
            throw new IllegalArgumentException();
    }
}