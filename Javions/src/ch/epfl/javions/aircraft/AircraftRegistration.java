package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftRegistration(String string) {

    private final static Pattern immatriculation = Pattern.compile("[A-Z0-9 .?/_+-]+");

    public AircraftRegistration {
        if (!immatriculation.matcher(string).matches()) throw new IllegalArgumentException();
    }
}