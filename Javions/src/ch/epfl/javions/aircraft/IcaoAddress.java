package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record IcaoAddress(String string) {

    private final static Pattern oACI = Pattern.compile("[0-9A-F]{6}");

    public IcaoAddress {
        if (!oACI.matcher(string).matches()) throw new IllegalArgumentException();
    }
}
