package me.panjohnny.jip.security;

public class SecureTransportException extends Exception {
    public SecureTransportException(String message) {
        super(message);
    }

    public SecureTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
