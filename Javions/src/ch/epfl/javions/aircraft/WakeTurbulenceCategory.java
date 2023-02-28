package ch.epfl.javions.aircraft;

/**
 * représente la catégorie de turbulence de sillage d'un aéronef
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public enum WakeTurbulenceCategory {

    LIGHT,
    MEDIUM,
    HEAVY,
    UNKNOWN;

    /**
     * retourne la catégorie de turbulence de sillage correspondant à la chaîne donnée
     * @param s la chaine donnée
     * @return la catégorie de turbulence
     */
    public static WakeTurbulenceCategory of(String s){
        return switch (s) {
            case "L" -> LIGHT;
            case "M" -> MEDIUM;
            case "H" -> HEAVY;
            default -> UNKNOWN;
        };
    }

}