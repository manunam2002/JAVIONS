package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * représente l'indicatif d'un aéronef
 * @param string l'indicatif d'un aéronef
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public record CallSign(String string) {

    private final static Pattern CALLSIGN_PATTERN = Pattern.compile("[A-Z0-9 ]{0,8}");

    /**
     * constructeur compact
     * @param string l'indicatif d'un aéronef
     * @throws IllegalArgumentException si la chaine ne représente pas un'indicatif valide
     */
    public CallSign {
        Preconditions.checkArgument(CALLSIGN_PATTERN.matcher(string).matches());
    }
}