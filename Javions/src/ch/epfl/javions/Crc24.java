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
    private final int generator1;
    private int[] table;

    /**
     * constructeur public
     * @param generator générateur
     */
    public Crc24(int generator){
        this.generator1 = generator;
        this.table = buildTable(generator);
    }

    /**
     * algorithme octet à octet qui retourne le CRC24 du tableau donné
     * @param bytes le tableau donné
     * @return le CRC24 correspondant
     */
    public int crc(byte[] bytes){
        int crc = 0;
        for (byte b : bytes) {
            int n_1 = Bits.extractUInt(crc, 16, 8);
            crc = (crc << 8) | Byte.toUnsignedInt(b);
            crc = crc ^ table[n_1];

        }
        for (int k = 0 ; k < 3 ; ++k){
            int n_1 = Bits.extractUInt(crc,16,8);
            crc = (crc << 8);
            crc = crc ^ table[n_1];
        }
        return Bits.extractUInt(crc,0,24);
    }

    /**
     * algorithme bit à bit qui retourne le CRC24 du tableau donné
     * @param generator le générateur
     * @param table le tableau donnée
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
        for (int k = 0 ; k < 24 ; ++k){
            int n_1 = Bits.extractUInt(crc,23,1);
            crc = (crc << 1);
            crc = crc ^ tab[n_1];
        }
        return Bits.extractUInt(crc,0,24);
    }

    /**
     * appelle crc_bitwise avec le générateur pour calculer le CRC24 des messages ADS-B
     * @param bytes le tableau donné
     * @return le CRC24 correspondant
     */
    public int crc_basic(byte[] bytes){
        return crc_bitwise(GENERATOR,bytes);
    }

    /**
     * construit la table de 256 entrées correspondant à un générateur en utilisant la méthode crc_bitwise
     * @param generator le générateur
     * @return la table de 256 entrées
     */
    private static int[] buildTable(int generator){
        int[] table = new int[256];
        for (int i = 0 ; i < 256; ++i){
            table[i] = crc_bitwise(generator,new byte[]{(byte) i});
        }
        return table;
    }
}