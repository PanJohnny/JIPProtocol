package me.panjohnny.jip.commons;

import java.util.Arrays;

/**
 * Reprezentuje stavový kód odpovědi.
 *
 * @author Jan Štefanča
 * @see Response
 * @since 1.0
 */
public enum StatusCode {
    OK(1),
    NOT_FOUND(2),
    DENIED(3),
    ERROR(4);

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    /**
     * @return číselná hodnota kódu
     */
    @Override
    public String toString() {
        return String.valueOf(code);
    }

    /**
     * Převede číselnou hodnotu na kód.
     *
     * @param code kód jako číslo
     * @return kód jako {@code StatusCode}
     */
    public static StatusCode getFromCode(String code) {
        return Arrays.stream(StatusCode.values()).filter((c) -> c.toString().equals(code)).findFirst().orElse(null);
    }
}
