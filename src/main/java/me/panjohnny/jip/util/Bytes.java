package me.panjohnny.jip.util;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Třída Bytes slouží k manipulaci s poly bajtů.
 * <p>
 * Poskytuje metody pro přidávání, odebírání a iteraci přes pole bajtů.
 * </p>
 *
 * @author Jan Štefanča
 * @since 1.0
 */
public record Bytes(byte[]... bytes) {
    /**
     * Vrátí celkovou délku všech polí bajtů.
     *
     * @return celková délka
     */
    public int length() {
        int length = 0;
        for (byte[] b : bytes) {
            length += b.length;
        }
        return length;
    }

    /**
     * Přidá nové pole bajtů na konec.
     *
     * @param arr pole bajtů k přidání
     * @return nová instance Bytes s přidaným polem bajtů
     */
    public Bytes append(byte[] arr) {
        byte[][] appended = new byte[bytes.length + 1][];
        System.arraycopy(bytes, 0, appended, 0, bytes.length);
        appended[bytes.length] = arr;
        return new Bytes(appended);
    }

    /**
     * Přidá nové pole bajtů na začátek.
     *
     * @param arr pole bajtů k přidání
     * @return nová instance Bytes s přidaným polem bajtů
     */
    public Bytes prepend(byte[] arr) {
        byte[][] prepended = new byte[bytes.length + 1][];
        prepended[0] = arr;
        System.arraycopy(bytes, 0, prepended, 1, bytes.length);
        return new Bytes(prepended);
    }

    /**
     * Provede danou akci pro každé pole bajtů.
     *
     * @param consumer akce k provedení
     */
    public void forEach(Consumer<byte[]> consumer) {
        for (byte[] b : bytes) {
            consumer.accept(b);
        }
    }

    /**
     * Vrátí pole bajtů na daném indexu.
     *
     * @param index index pole bajtů
     * @return pole bajtů na daném indexu
     */
    public byte[] at(int index) {
        return bytes[index];
    }

    /**
     * Vymaže všechna pole bajtů.
     */
    public void clear() {
        // Nullify references
        Arrays.fill(bytes, null);
    }
}