package ch.epfl.javions.adsb;

import java.util.regex.Pattern;

public record CallSign(String string) {

    private final static Pattern sign = Pattern.compile("[A-Z0-9 ]{0,8}");

    public CallSign {
        if (!sign.matcher(string).matches()) throw new IllegalArgumentException();
    }
}