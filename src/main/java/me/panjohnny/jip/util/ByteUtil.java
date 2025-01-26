package me.panjohnny.jip.util;

public final class ByteUtil {
    public static byte[] intToByteArray4(int length) {
        return new byte[] {
                (byte) ((length >> 24) & 0xFF),
                (byte) ((length >> 16) & 0xFF),
                (byte) ((length >> 8) & 0xFF),
                (byte) (length & 0xFF)
        };
    }

    public static int byteArray4ToInt(byte[] length) {
        return ((length[0] & 0xFF) << 24) |
                ((length[1] & 0xFF) << 16) |
                ((length[2] & 0xFF) << 8) |
                (length[3] & 0xFF); //
    }
}
