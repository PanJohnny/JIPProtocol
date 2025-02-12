package me.panjohnny.jip.util;

/**
 * Utilitní třída pro manipulaci s bajty.
 * <p>
 * Poskytuje metody pro konverzi mezi celými čísly a poli bajtů.
 * </p>
 *
 * @since 1.0
 */
public final class ByteUtil {
    /**
     * Převede celé číslo na pole 4 bajtů.
     *
     * @param length celé číslo k převedení
     * @return pole 4 bajtů reprezentující celé číslo
     */
    public static byte[] intToByteArray4(int length) {
        return new byte[]{
                (byte) ((length >> 24) & 0xFF),
                (byte) ((length >> 16) & 0xFF),
                (byte) ((length >> 8) & 0xFF),
                (byte) (length & 0xFF)
        };
    }

    /**
     * Převede pole 4 bajtů na celé číslo.
     *
     * @param length pole 4 bajtů k převedení
     * @return celé číslo reprezentované polem bajtů
     */
    public static int byteArray4ToInt(byte[] length) {
        return ((length[0] & 0xFF) << 24) |
                ((length[1] & 0xFF) << 16) |
                ((length[2] & 0xFF) << 8) |
                (length[3] & 0xFF);
    }
}