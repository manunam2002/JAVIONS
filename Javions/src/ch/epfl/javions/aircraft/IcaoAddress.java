package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * représente une adresse OACI
 * @param string la chaîne contenant la représentation textuelle de l'adresse OACI
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public record IcaoAddress(String string) {

    private final static Pattern ICAO_PATTERN = Pattern.compile("[0-9A-F]{6}");

    /**
     * constructeur compact
     * @param string la chaîne contenant la représentation textuelle de l'adresse OACI
     * @throws IllegalArgumentException si la chaine ne représente pas une adresse OACI valide
     */
    public IcaoAddress {
        Preconditions.checkArgument(ICAO_PATTERN.matcher(string).matches());
    }
}
