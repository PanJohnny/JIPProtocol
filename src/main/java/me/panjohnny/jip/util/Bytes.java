package me.panjohnny.jip.util;

import java.util.Arrays;
import java.util.function.Consumer;

public record Bytes(byte[]... bytes) {
    public int length() {
        int length = 0;
        for (byte[] b : bytes) {
            length += b.length;
        }
        return length;
    }

    public Bytes append(byte[] arr) {
        byte[][] appended = new byte[bytes.length + 1][];
        System.arraycopy(bytes, 0, appended, 0, bytes.length);
        appended[bytes.length] = arr;
        return new Bytes(appended);
    }

    public Bytes prepend(byte[] arr) {
        byte[][] prepended = new byte[bytes.length + 1][];
        prepended[0] = arr;
        System.arraycopy(bytes, 0, prepended, 1, bytes.length);
        return new Bytes(prepended);
    }

    public void forEach(Consumer<byte[]> consumer) {
        for (byte[] b : bytes) {
            consumer.accept(b);
        }
    }

    public byte[] at(int index) {
        return bytes[index];
    }

    public void clear() {
        // Nullify references
        Arrays.fill(bytes, null);
    }
}
