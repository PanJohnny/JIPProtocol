package me.panjohnny.jip.util;

public final class ByteUtil {
    /**
     * Converts an integer to a byte array of length 4.
     * @param length The integer to convert.
     * @return The byte array.
     */
    public static byte[] intToByteArray4(int length) {
        return new byte[] {
                (byte) ((length >> 24) & 0xFF),
                (byte) ((length >> 16) & 0xFF),
                (byte) ((length >> 8) & 0xFF),
                (byte) (length & 0xFF)
        };
    }

    /**
     * Converts a byte array of length 4 to an integer.
     * @param length The byte array to convert.
     * @return The integer.
     */
    public static int byteArray4ToInt(byte[] length) {
        return ((length[0] & 0xFF) << 24) |
                ((length[1] & 0xFF) << 16) |
                ((length[2] & 0xFF) << 8) |
                (length[3] & 0xFF);
    }
}
