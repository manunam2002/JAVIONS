package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 * représente une adresse OACI
 * @param string la chaîne contenant la représentation textuelle de l'adresse OACI
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public record IcaoAddress(String string) {

    private final static Pattern oACI = Pattern.compile("[0-9A-F]{6}");

    /**
     * constructeur compact
     * @param string la chaîne contenant la représentation textuelle de l'adresse OACI
     */
    public IcaoAddress {
        if (!oACI.matcher(string).matches()) throw new IllegalArgumentException();
    }
}
