package ch.epfl.javions.adsb;

/**
 * transforme les messages ADS-B bruts en messages d'identification, de position en vol ou de vitesse en vol
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public class MessageParser {

    /**
     * constructeur privé
     */
    private MessageParser(){}

    /**
     * retourne l'instance correspondant au message brut donné, ou null si le code de type
     * de ce dernier ne correspond à aucun de ces trois types de messages, ou si il est invalide
     * @param rawMessage le message brut donné
     * @return l'instance correspondant au message brut donné
     */
    public static Message parse(RawMessage rawMessage) {
        if (rawMessage.typeCode() > 0 && rawMessage.typeCode() < 5) {
            return AircraftIdentificationMessage.of(rawMessage);
        }
        if ((rawMessage.typeCode() > 8 && rawMessage.typeCode() < 19) ||
                (rawMessage.typeCode() > 19 && rawMessage.typeCode() < 23)) {
            return AirbornePositionMessage.of(rawMessage);
        }
        if (rawMessage.typeCode() == 19) {
            return AirborneVelocityMessage.of(rawMessage);
        }
        return null;
    }
}
