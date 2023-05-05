package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * représente un message ADS-B de positionnement en vol
 * @param timeStampNs l'horodatage du message, en nanosecondes
 * @param icaoAddress l'adresse OACI de l'expéditeur du message
 * @param altitude l'altitude à laquelle se trouvait l'aéronef au moment de l'envoi du message, en mètres
 * @param parity la parité du message (0 s'il est pair, 1 s'il est impair)
 * @param x la longitude locale et normalisée, donc comprise entre 0 et 1,
 *         à laquelle se trouvait l'aéronef au moment de l'envoi du message
 * @param y la latitude locale et normalisée, donc comprise entre 0 et 1,
 *         à laquelle se trouvait l'aéronef au moment de l'envoi du message
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude,
                                      int parity, double x, double y) implements Message {

    private static final byte[] DETANGLE_INDEXES = new byte[]{9,3,10,4,11,5,6,0,7,1,8,2};

    /**
     * constructeur compact
     * @param timeStampNs l'horodatage du message, en nanosecondes
     * @param icaoAddress l'adresse OACI de l'expéditeur du message
     * @param altitude l'altitude à laquelle se trouvait l'aéronef au moment de l'envoi du message, en mètres
     * @param parity la parité du message (0 s'il est pair, 1 s'il est impair)
     * @param x la longitude locale et normalisée, donc comprise entre 0 et 1,
     *         à laquelle se trouvait l'aéronef au moment de l'envoi du message
     * @param y la latitude locale et normalisée, donc comprise entre 0 et 1,
     *          à laquelle se trouvait l'aéronef au moment de l'envoi du message
     * @throws NullPointerException si icaoAddress est nul
     * @throws IllegalArgumentException si timeStamp est strictement inférieure à 0,
     *          ou parity est différent de 0 ou 1, ou x ou y ne sont pas compris entre 0 (inclus) et 1 (exclu)
     */
    public AirbornePositionMessage{
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs >= 0 && (parity == 0 || parity == 1) &&
                (x >= 0 && x < 1) && (y >= 0 && y < 1));
    }

    /**
     * retourne le message de positionnement en vol correspondant au message brut donné,
     * ou null si l'altitude qu'il contient est invalide
     * @param rawMessage le message brut donné
     * @return le message de positionnement en vol correspondant au message brut donné
     */
    public static AirbornePositionMessage of(RawMessage rawMessage){
        long me = rawMessage.bytes().bytesInRange(5,11);
        int LON_CPR = (int) (me & 0x1FFFF);
        int LAT_CPR = (int) ((me >>> 17) & 0x1FFFF);
        int FORMAT = (int) ((me >>> 34) & 0x1);
        int ALT = (int) ((me >>> 36) & 0xFFF);

        double altitude;
        if (Bits.testBit(ALT,4)){
            int alt = ((Bits.extractUInt(ALT,0,4)) | ((Bits.extractUInt(ALT,5,7))<<4));
            altitude = Units.convertFrom((-1000 + alt*25),Units.Length.FOOT);
        } else {
            int orderedALT = 0;
            for (int i = 0 ; i < 12 ; ++i){
                if (Bits.testBit(ALT,i)) orderedALT = (orderedALT | (1 << DETANGLE_INDEXES[i])) & 0xFFF;
            }
            int f100Gray = Bits.extractUInt(orderedALT,0,3);
            int f500Gray = Bits.extractUInt(orderedALT,3,9);
            int f100Decoded = f100Gray;
            int f500Decoded = f500Gray;
            for (byte i = 1 ; i < 3 ; ++ i){
                f100Decoded = f100Decoded ^ (f100Gray>>i);
            }
            for (byte i = 1 ; i < 9 ; ++i){
                f500Decoded = f500Decoded ^ (f500Gray>>i);
            }
            if ((f100Decoded == 0) || (f100Decoded == 5) || (f100Decoded == 6)) return null;
            if (f100Decoded == 7) f100Decoded = 5;
            if (f500Decoded%2 != 0) f100Decoded = 6 - f100Decoded;
            altitude = Units.convertFrom((-1300 + f100Decoded*100 + f500Decoded*500),Units.Length.FOOT);
        }
        return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(),
                altitude, FORMAT, Math.scalb(LON_CPR,-17), Math.scalb(LAT_CPR,-17));
    }
}
