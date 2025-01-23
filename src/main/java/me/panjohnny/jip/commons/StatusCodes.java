package me.panjohnny.jip.commons;

public enum StatusCodes {
    OK(1),
    NOT_FOUND(2),
    DENIED(3),
    ERROR(4);

    private final int code;
    StatusCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return String.valueOf(code);
    }
}
