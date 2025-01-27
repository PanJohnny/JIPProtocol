package me.panjohnny.jip.commons;

import java.util.Arrays;

public enum StatusCode {
    OK(1),
    NOT_FOUND(2),
    DENIED(3),
    ERROR(4);

    private final int code;
    StatusCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(code);
    }

    public static StatusCode getFromCode(String code) {
        return Arrays.stream(StatusCode.values()).filter((c) -> c.toString().equals(code)).findFirst().orElse(null);
    }
}
