package ch.epfl.javions.adsb;

import java.util.regex.Pattern;

/**
 * représente l'indicatif d'un aéronef
 * @param string l'indicatif d'un aéronef
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public record CallSign(String string) {

    private final static Pattern sign = Pattern.compile("[A-Z0-9 ]{0,8}");

    /**
     * constructeur compact
     * @param string l'indicatif d'un aéronef
     */
    public CallSign {
        if (!sign.matcher(string).matches()) throw new IllegalArgumentException();
    }
}