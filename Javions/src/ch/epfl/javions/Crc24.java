package ch.epfl.javions;

/**
 * représente un calculateur de CRC de 24 bits
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class Crc24 {

    /**
     * générateur utilisé pour calculer le CRC24 des messages ADS-B
     */
    public final static int GENERATOR = 0xFFF409;
    private static final int CRC_BITS = 24;
    private static final int CRC_SIZE = 3;
    private final static int TABLE_SIZE = 256;

    private final int[] table;

    /**
     * constructeur public
     * @param generator générateur
     */
    public Crc24(int generator){
        table = buildTable(generator);
    }

    /**
     * algorithme octet à octet qui retourne le CRC24 du tableau donné
     * @param bytes le tableau donné
     * @return le CRC24 correspondant
     */
    public int crc(byte[] bytes){
        int crc = 0;
        for (byte b : bytes) {
            int n_1 = Bits.extractUInt(crc, 16, Byte.SIZE);
            crc = (crc << Byte.SIZE) | Byte.toUnsignedInt(b);
            crc = crc ^ table[n_1];

        }
        for (int k = 0; k < CRC_SIZE; ++k){
            int n_1 = Bits.extractUInt(crc,16,Byte.SIZE);
            crc = (crc << Byte.SIZE);
            crc = crc ^ table[n_1];
        }
        return Bits.extractUInt(crc,0, CRC_BITS);
    }

    /**
     * algorithme bit à bit qui retourne le CRC24 du tableau donné
     * @param generator le générateur
     * @param table le tableau donné
     * @return le CRC24 correspondant
     */
    private static int crc_bitwise(int generator, byte[] table){
        int[] tab = new int[]{0,generator};
        int crc = 0;
        for (byte b : table) {
            for (int j = 7; j >= 0; --j) {
                int n_1 = Bits.extractUInt(crc, 23, 1);
                crc = ((crc << 1) | Bits.extractUInt(b, j, 1));
                crc = crc ^ tab[n_1];
            }
        }
        for (int k = 0; k < CRC_BITS; ++k){
            int n_1 = Bits.extractUInt(crc,23,1);
            crc = (crc << 1);
            crc = crc ^ tab[n_1];
        }
        return Bits.extractUInt(crc,0, CRC_BITS);
    }

    /**
     * construit la table de 256 entrées correspondant à un générateur en utilisant la méthode crc_bitwise
     * @param generator le générateur
     * @return la table de 256 entrées
     */
    private static int[] buildTable(int generator){
        int[] table = new int[TABLE_SIZE];
        for (int i = 0 ; i < TABLE_SIZE; ++i){
            byte[] bytes = new byte[]{(byte) i};
            table[i] = crc_bitwise(generator,bytes);
        }
        return table;
    }
}