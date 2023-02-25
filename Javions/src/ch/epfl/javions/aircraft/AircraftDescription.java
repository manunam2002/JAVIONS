package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 * represente la description de l'aéronef
 * @param string description de l'aéronef
 */
public record AircraftDescription(String string) {

    private final static Pattern description = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * constructeur compact
     * @param string description de l'aéronef
     */
    public AircraftDescription {
        if (string != null && !description.matcher(string).matches()) throw new IllegalArgumentException();
    }
}