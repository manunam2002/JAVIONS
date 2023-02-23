package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftDescription(String string) {

    private final static Pattern description = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    public AircraftDescription {
        if (string != null && !description.matcher(string).matches()) throw new IllegalArgumentException();
    }
}