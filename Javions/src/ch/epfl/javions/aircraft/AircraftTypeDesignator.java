package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftTypeDesignator(String string) {

    private final static Pattern type = Pattern.compile("[A-Z0-9]{2,4}");

    public AircraftTypeDesignator {
        if (string != null && !type.matcher(string).matches()) throw new IllegalArgumentException();
    }
}